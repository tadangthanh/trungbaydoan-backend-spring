//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.*;
import vnua.edu.xdptpm09.entity.*;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.GroupMapping;
import vnua.edu.xdptpm09.mapping.impl.NotificationMapping;
import vnua.edu.xdptpm09.mapping.impl.ProjectMapping;
import vnua.edu.xdptpm09.repository.*;
import vnua.edu.xdptpm09.service.IDocumentService;
import vnua.edu.xdptpm09.service.IGroupService;
import vnua.edu.xdptpm09.service.IProjectService;
import vnua.edu.xdptpm09.service.Subject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements IProjectService {
    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private final ProjectRepo projectRepo;
    private final UserRepo userRepo;
    private final ProjectMapping projectMapping;
    private final ModelMapper modelMapper;
    private final GroupMemberRepo groupMemberRepo;
    private final GroupRepo groupRepo;
    private final CategoryRepo categoryRepo;
    private final AcademyYearRepo academyYearRepo;
    private final IDocumentService documentService;
    private final DocumentRepo documentRepo;
    private final Subject messagePublisher;
    private final GroupMapping groupMapping;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepo notificationRepo;
    private final IGroupService groupService;
    private final NotificationMapping notificationMapping;
    private final CommentServiceImpl commentServiceImpl;
    private final CommentRepo commentRepo;
    private final TechnologyRepo technologyRepo;

    @Caching(
            evict = {@CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            )}
    )
    public Optional<ProjectDTO> createProject(CreateProjectDTO createProjectDTO, List<MultipartFile> files) {
        User currentUser = this.getCurrentUser();
        Project project = this.modelMapper.map(createProjectDTO, Project.class);
        this.checkDate(project);
        Category category = this.categoryRepo.findById(createProjectDTO.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại này"));
        project.setCategory(category);
        Group group = this.groupRepo.findById(this.createGroup(createProjectDTO).getId()).orElseThrow(() -> new BadRequestException("Có lỗi khi thêm mới đồ án"));
        project.setGroup(group);
        List<User> mentors = this.userRepo.findAllById(createProjectDTO.getMentorIds());
        mentors.forEach(this::isTeacher);
        project.setMentors(mentors);
        project.setAcademyYear(this.academyYearRepo.findByNumber(currentUser.getAcademicYear()).orElse(this.academyYearRepo.findFirstByOrderByIdDesc().orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa nào"))));
        project.setProjectStatus(ProjectStatus.PENDING);
        project.setActive(true);
        project.setTechnologies(this.technologyRepo.findAllByIdIn(createProjectDTO.getIdsTechnology()));
        project = this.projectRepo.saveAndFlush(project);
        if (files != null && !files.isEmpty()) {
            List<DocumentDTO> documentDTOS = this.documentService.saveAll(files);
            List<Long> ids = documentDTOS.stream().map(BaseDTO::getId).toList();
            List<Document> documents = this.documentRepo.findAllById(ids);
            project.setDocuments(documents);
            for (Document document : documents) {
                document.setProject(project);
                this.documentRepo.saveAndFlush(document);
            }
        }
        List<Document> documents=this.documentRepo.findAllById(createProjectDTO.getDocumentIds());
        for (Document document : documents) {
            document.setProject(project);
        }
        this.sendNotificationForTeacher(project);
        this.sendNotificationForCurrentUser(this.getCurrentUser(), "Đồ án đã thêm thành công, chờ giáo viên duyệt!");
        return Optional.of(this.projectMapping.toDto(project));
    }

    private void sendNotificationForTeacher(Project project) {
        List<User> teachers = this.userRepo.findTeachers();
        for (User teacher : teachers) {
            if (!teacher.getEmail().equals(this.getCurrentEmail())) {
                Notification notification = new Notification();
                notification.setMessage("Có đồ án mới cần phê duyệt");
                notification.setSeen(false);
                notification.setProject(project);
                notification.setReceiver(teacher);
                this.notificationRepo.saveAndFlush(notification);
                NotificationDTO notificationDTO = this.notificationMapping.toDto(notification);
                this.messagingTemplate.convertAndSendToUser(teacher.getEmail(), "/topic/notification", notificationDTO);
            }
        }

    }

    private Group createGroup(CreateProjectDTO createProjectDTO) {
        GroupDTO groupDTO = new GroupDTO();
        if (createProjectDTO.getMemberIds() != null) {
            groupDTO.setMemberIds(createProjectDTO.getMemberIds());
        }

        groupDTO = this.groupService.create(groupDTO).orElseThrow(() -> new ResourceNotFoundException("Không tạo được group"));
        return this.groupMapping.toEntity(groupDTO);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    key = "#projectDTO.id"
            ), @CacheEvict(
                    value = {"projectCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            )},
            put = {@CachePut(
                    value = {"project"},
                    key = "#projectDTO.id"
            ), @CachePut(
                    value = {"projectsAdmin"},
                    key = "#projectDTO.id"
            ), @CachePut(
                    value = {"projectsMentor"},
                    key = "#projectDTO.id"
            )}
    )
    public Optional<ProjectDTO> updateDocumentProject(ProjectDTO projectDTO, List<MultipartFile> documents) {
        Project projectExisting = this.projectMapping.updateFromDTO(projectDTO);
        this.checkApproveProject(projectExisting);
        this.checkDate(projectExisting);
        this.checkLeaderAndExistGroup(projectExisting.getGroup().getId());
        this.documentService.deleteAllByProjectId(projectDTO.getId());
        this.projectRepo.saveAndFlush(projectExisting);
        this.documentService.saveAll(documents, projectDTO.getId());
        return Optional.of(this.projectMapping.toDto(this.projectRepo.save(projectExisting)));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"projectCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            )},
            put = {@CachePut(
                    value = {"project"},
                    key = "#projectId"
            ), @CachePut(
                    value = {"projectsAdmin"},
                    key = "#projectId"
            ), @CachePut(
                    value = {"projectsMentor"},
                    key = "#projectId"
            )}
    )
    public Optional<ProjectDTO> addDocumentProject(Long projectId, List<MultipartFile> documents) {
        this.checkQuantityVideo(projectId);
        Project projectExisting = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        this.checkApproveProject(projectExisting);
        this.checkLeaderAndExistGroup(projectExisting.getGroup().getId());
        this.documentService.saveAll(documents, projectId);
        return Optional.of(this.projectMapping.toDto(projectExisting));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"projectCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            )},
            put = {@CachePut(
                    value = {"project"},
                    key = "#projectId"
            ), @CachePut(
                    value = {"projectsAdmin"},
                    key = "#projectId"
            ), @CachePut(
                    value = {"projectsMentor"},
                    key = "#projectId"
            )}
    )
    public Optional<ProjectDTO> removeDocumentProject(Long projectId, Long documentId) {
        Project projectExisting = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        this.checkApproveProject(projectExisting);
        this.checkLeaderAndExistGroup(projectExisting.getGroup().getId());
        Document document = this.documentRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        projectExisting.getDocuments().remove(document);
        this.documentService.delete(documentId);
        return Optional.of(this.projectMapping.toDto(projectExisting));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            )}
    )
    public Optional<ProjectDTO> updateProject(ProjectDTO projectDTO, List<MultipartFile> files) {
        Project projectExisting = this.projectMapping.updateFromDTO(projectDTO);
        User user = this.getCurrentUser();
        User leader = (projectExisting.getGroup().getMembers().stream().filter((groupMember) -> groupMember.getMemberRole().getName().equals("ROLE_LEADER")).findFirst().orElseThrow(() -> new BadRequestException("Không tìm thấy leader"))).getUser();
        if (!leader.getEmail().equals(user.getEmail())) {
            this.checkTeacher();
        }

        this.checkDate(projectExisting);
        List<Document> documents;
        List<User> members;
        if (files != null && !files.isEmpty()) {
            List<DocumentDTO> documentDTOS = this.documentService.saveAll(files);
            documents = this.documentRepo.findAllById(documentDTOS.stream().map(BaseDTO::getId).toList());
            projectExisting.setDocuments(documents);
            for (Document document : documents) {
                if (document.getType().equals(DocumentType.VIDEO)) {
                    this.documentService.deleteVideoByProjectId(projectDTO.getId());
                }
                document.setProject(projectExisting);
                this.documentRepo.saveAndFlush(document);
            }
        }

        documents = this.documentRepo.findAllById(projectDTO.getDocumentIds());
        documents.forEach((document) -> {
            if (document.getType().equals(DocumentType.VIDEO)) {
                this.documentService.deleteVideoByProjectId(projectDTO.getId());
            }
            document.setProject(projectExisting);
            this.documentRepo.saveAndFlush(document);
        });
        projectExisting.setDocuments(documents);
        members = projectExisting.getGroup().getMembers().stream().map(GroupMember::getUser).toList();
        if (user.getRole().getName().equals("ROLE_STUDENT")) {
            projectExisting.setProjectStatus(ProjectStatus.PENDING);
            this.sendNotificationForTeacher(projectExisting);
        } else {
            projectExisting.setProjectStatus(ProjectStatus.APPROVED);
        }
        this.sendNotificationToListUser(members, "Đồ án: " + Jsoup.parse(projectExisting.getName()).text() + " đã được cập nhật");
        return Optional.of(this.projectMapping.toDto(this.projectRepo.save(projectExisting)));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            )}
    )
    public int approveProjects(ProjectRequest projectRequest) {
        this.checkTeacher();
        User currentUser = this.getCurrentUser();
        AtomicInteger result = new AtomicInteger(0);
        List<Project> projects = this.projectRepo.findAllByIds(projectRequest.getProjectIds());
        projects.forEach((project) -> {
            if (!project.getProjectStatus().equals(ProjectStatus.APPROVED) && !project.getProjectStatus().equals(ProjectStatus.REJECTED)) {
                project.setApprover(currentUser);
                project.setProjectStatus(ProjectStatus.APPROVED);
                this.createNotificationForLeader(project, "Đồ án: " + Jsoup.parse(project.getName()).text() + " đã được duyệt");
                result.getAndIncrement();
            } else {
                throw new BadRequestException("Đồ án: " + Jsoup.parse(project.getName()).text() + " đã được duyệt hoặc từ chối, không thể thực hiện thao tác này");
            }
        });
        return result.get() == projectRequest.getProjectIds().size() ? result.get() : 0;
    }

    private void createNotificationForLeader(Project project, String message) {
        User leader = this.groupMemberRepo.findLeaderByGroupId(project.getGroup().getId()).getUser();
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setProject(project);
        notification.setSeen(false);
        notification.setReceiver(leader);
        notification = this.notificationRepo.saveAndFlush(notification);
        NotificationDTO notificationDTO = this.notificationMapping.toDto(notification);
        this.messagingTemplate.convertAndSendToUser(leader.getEmail(), "/topic/notification", notificationDTO);
        this.notificationRepo.saveAndFlush(notification);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            )}
    )
    public Optional<ProjectDTO> submitProject(Long projectId) {
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        if (project.getProjectStatus() == ProjectStatus.APPROVED) {
            throw new BadRequestException("Project đã được duyệt không thể nộp lại");
        } else if (project.getProjectStatus() == ProjectStatus.PENDING) {
            throw new BadRequestException("Project đang chờ phê duyệt không thể nộp lại");
        } else {
            this.checkLeaderAndExistGroup(project.getGroup().getId());
            project.setProjectStatus(ProjectStatus.PENDING);
            Notification notification = this.createNotificationForTeacher(project, "Project " + Jsoup.parse(project.getName()).text() + " đã được nộp, đang chờ phê duyệt");
            this.messagePublisher.sendToTeacher(notification);
            return Optional.of(this.projectMapping.toDto(this.projectRepo.save(project)));
        }
    }

    private Notification createNotificationForTeacher(Project project, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setProject(project);
        notification.setSeen(false);
        return this.notificationRepo.save(notification);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            )}
    )
    public int inactivateProjects(ProjectRequest projectRequest) {
        this.checkTeacher();
        AtomicInteger result = new AtomicInteger();
        List<Project> projects = this.projectRepo.findAllByIds(projectRequest.getProjectIds());
        projects.forEach((project) -> {
            project.setActive(false);
            result.getAndIncrement();
        });
        return result.get() == projectRequest.getProjectIds().size() ? result.get() : 0;
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            )}
    )
    public int activateProjects(ProjectRequest projectRequest) {
        this.checkTeacher();
        AtomicInteger result = new AtomicInteger();
        List<Project> projects = this.projectRepo.findAllByIds(projectRequest.getProjectIds());
        projects.forEach((project) -> {
            project.setActive(true);
            result.getAndIncrement();
        });
        return result.get() == projectRequest.getProjectIds().size() ? result.get() : 0;
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"membersByProjectId"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"membersByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectIdAndParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public void deleteProject(Long projectId) {
        this.checkTeacher();
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        project.getTechnologies().forEach((technology) -> {
            technology.getProjects().remove(project);
        });
        this.projectRepo.delete(project);
        this.deleteNotificationByProjectId(projectId);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            )}
    )
    public int rejectProjects(ProjectRequest projectRequest) {
        this.checkTeacher();
        AtomicInteger result = new AtomicInteger();
        List<Project> projects = this.projectRepo.findAllByIds(projectRequest.getProjectIds());
        User currentUser = this.getCurrentUser();
        projects.forEach((project) -> {
            this.checkStatus(project);
            project.setApprover(currentUser);
            project.setProjectStatus(ProjectStatus.REJECTED);
            Notification notification = new Notification();
            notification.setProject(project);
            notification.setSeen(false);
            notification.setReceiver(this.groupMemberRepo.findLeaderByGroupId(project.getGroup().getId()).getUser());
            String var10001 = Jsoup.parse(project.getName()).text();
            notification.setMessage("Project " + var10001 + " của bạn đã bị " + currentUser.getFullName() + " từ chối với lý do: " + projectRequest.getReason());
            this.notificationRepo.saveAndFlush(notification);
            NotificationDTO notificationDTO = this.notificationMapping.toDto(notification);
            this.createNotificationForLeader(project, notificationDTO.getMessage());
            result.getAndIncrement();
        });
        return result.get() == projectRequest.getProjectIds().size() ? result.get() : 0;
    }

    @Cacheable(
            value = {"projectsPending"},
            key = "#email+'-'+#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    public PageResponse<?> getAllProjectPendingByMemberEmail(String email, Pageable pageable) {
        User currentUser = this.getCurrentUser();
        if (!currentUser.getEmail().equals(email)) {
            throw new BadRequestException("Không có quyền truy cập");
        } else {
            Page<ProjectDTO> page = this.projectRepo.findAllByProjectStatusPendingAndMemberEmail(email, pageable).map(this.projectMapping::toDto);
            return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
        }
    }

    @Cacheable(
            value = {"projectsActiveAndApproved"},
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #searchField + '-' + #search +'-'+ #pageable.sort"
    )
    public PageResponse<?> getAllProjectActiveAndApproved(Pageable pageable, String searchField, String search) {
        AtomicReference<Specification<Project>> spec = new AtomicReference<>(Specification.where((Specification) null));
        spec.updateAndGet((currentSpec) -> currentSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.equal(root.get("projectStatus"), ProjectStatus.APPROVED), criteriaBuilder.isTrue(root.get("active")))));
        if (searchField.equals("category")) {
            spec.updateAndGet((currentSpec) -> currentSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.join("category").get("name"), "%" + search + "%")));
        } else if (searchField.equals("academyYear")) {
            spec.updateAndGet((currentSpec) -> currentSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("academyYear").get("number"), Integer.parseInt(search))));
        } else {
            spec.updateAndGet((currentSpec) -> currentSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(searchField), "%" + search + "%")));
        }

        Page<ProjectDTO> page = this.projectRepo.findAll(spec.get(), pageable).map(this.projectMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Cacheable(
            value = {"projects"},
            key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public PageResponse<?> searchProject(String search, Pageable pageable) {
        Page<ProjectDTO> page = this.projectRepo.searchAllByNameOrDescriptionOrSummary(search, pageable).map(this.projectMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).hasNext(page.hasNext()).currentPage(page.getNumber() + 1).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Cacheable(
            value = {"project"},
            key = "#projectId"
    )
    public Optional<ProjectDTO> getProjectById(Long projectId) {
        User currentUser = this.getCurrentUser();
        Project project;
        if (currentUser != null && !currentUser.getRole().getName().equals("ROLE_STUDENT")) {
            project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
            this.checkStatusAndRole(project, currentUser);
            return Optional.of(this.projectMapping.toDto(project));
        } else {
            project = this.projectRepo.findProjectByIdAndActiveAndProjectStatus(projectId, true, ProjectStatus.APPROVED).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
            return Optional.of(this.projectMapping.toDto(project));
        }
    }

    private void checkStatus(Project project) {
        if (project.getProjectStatus().equals(ProjectStatus.APPROVED) || project.getProjectStatus().equals(ProjectStatus.REJECTED)) {
            throw new BadRequestException("Không thể cập nhật project đã được duyệt hoặc từ chối");
        }
    }

    @Cacheable(
            value = {"projects"},
            key = "#email + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public PageResponse<?> getAllProjectByUser(String email, Pageable pageable) {
        User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        List<Long> groupIds = user.getGroupMemberships().stream().map(GroupMember::getGroup).map(BaseEntity::getId).toList();
        Page<ProjectDTO> page = this.projectRepo.findAllByGroupIdsAndActiveAndApproved(groupIds, pageable).map(this.projectMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Cacheable(
            value = {"mentorsProject"},
            key = "#projectId"
    )
    public List<UserDTO> getMentorsByProjectId(Long projectId) {
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        return project.getMentors().stream().map((user) -> this.modelMapper.map(user, UserDTO.class)).toList();
    }

    @Cacheable(
            value = {"membersProject"},
            key = "#projectId"
    )
    public List<UserDTO> getMembersByProjectId(Long projectId) {
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        return project.getGroup().getMembers().stream().map((groupMember) -> this.modelMapper.map(groupMember.getUser(), UserDTO.class)).toList();
    }

    @Cacheable(
            value = {"projectsMentor"},
            key = "#email + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public PageResponse<?> getAllProjectByMentor(String email, Pageable pageable) {
        User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        Page<ProjectDTO> page = this.projectRepo.findAllByMentorIdAndApprovedAndActive(user.getId(), pageable).map(this.projectMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Cacheable(
            value = {"projectsAdmin"},
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #search + '-' + #searchField +'-'+#pageable.sort"
    )
    public PageResponse<?> getAllProjectByAdmin(Pageable pageable, String search, String searchField) {
        this.checkTeacher();
        Specification<Project> spec = Specification.where((Specification) null);
        if (searchField == null || searchField.isEmpty()) {
            searchField = "name";
        }

        if (search != null && !search.isEmpty()) {
            String finalSearchField = searchField;
            if (searchField.equals("active")) {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(finalSearchField), Boolean.parseBoolean(Integer.parseInt(search) == 1 ? "true" : "false")));
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(finalSearchField), "%" + search + "%"));
            }
        }

        Page<ProjectDTO> page = this.projectRepo.findAll(spec, pageable).map(this.projectMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectIdAndParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public int deleteProjectByIds(ProjectRequest projectRequest) {
        this.checkTeacher();
        AtomicInteger result = new AtomicInteger();
        projectRequest.getProjectIds().forEach((projectId) -> {
            Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
            List<User> authors = project.getGroup().getMembers().stream().map(GroupMember::getUser).toList();
            this.documentService.deleteAllByProjectId(projectId);
            this.deleteNotificationByProjectId(projectId);
            this.commentServiceImpl.deleteByProjectId(projectId);
            if (this.projectRepo.deleteProjectById(projectId) == 1) {
                result.getAndIncrement();
                String var10002 = Jsoup.parse(project.getName()).text();
                this.sendNotificationToListUser(authors, "Project " + var10002 + " đã bị xóa với lý do: " + projectRequest.getReason());
            }
        });
        return result.get() == projectRequest.getProjectIds().size() ? result.get() : 0;
    }

    @Cacheable(
            value = {"projectsCategory"},
            key = "#search + '-' + #searchField + '-' + #categoryId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public PageResponse<?> getAllProjectByCategoryId(String searchField, String search, Long categoryId, Pageable pageable) {
        Specification<Project> spec = Specification.where((Specification) null);
        if (!StringUtils.hasText(searchField)) {
            searchField = "name";
        }
        if (StringUtils.hasText(search)) {
            String finalSearchField = searchField;
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(finalSearchField), "%" + search + "%"));
        }

        if (categoryId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("category").get("id"), categoryId));
        }

        Page<ProjectDTO> page = this.projectRepo.findAll(spec, pageable).map(this.projectMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    private void sendNotificationForCurrentUser(User currentUser, String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setSeen(false);
        notification.setReceiver(currentUser);
        this.notificationRepo.saveAndFlush(notification);
        NotificationDTO notificationDTO = this.notificationMapping.toDto(notification);
        this.messagingTemplate.convertAndSendToUser(currentUser.getEmail(), "/topic/notification", notificationDTO);
    }

    private void sendNotificationToListUser(List<User> users, String message) {
        for (User user : users) {
            if (!user.getEmail().equals(this.getCurrentEmail())) {
                Notification notification = new Notification();
                notification.setMessage(message);
                notification.setSeen(false);
                notification.setReceiver(user);
                this.notificationRepo.saveAndFlush(notification);
                NotificationDTO notificationDTO = this.notificationMapping.toDto(notification);
                this.messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/notification", notificationDTO);
            }
        }

    }

    private void checkStatusAndRole(Project project, User user) {
        if (!user.getRole().getName().equals("ROLE_TEACHER") && !user.getRole().getName().equals("ROLE_ADMIN")) {
            if (!project.getProjectStatus().equals(ProjectStatus.APPROVED)) {
                if ((project.getProjectStatus().equals(ProjectStatus.PENDING) || project.getProjectStatus().equals(ProjectStatus.REJECTED)) && user.getRole().getName().equals("ROLE_STUDENT")) {
                    List<Long> ids = project.getGroup().getMembers().stream().map(GroupMember::getUser).map(BaseEntity::getId).toList();
                    if (!ids.contains(user.getId())) {
                        throw new BadRequestException("Chỉ thành viên mới có thể xem đồ án");
                    }
                }

            }
        }
    }

    private void checkQuantityVideo(Long projectId) {
        if (this.documentRepo.countByTypeAndProjectId(DocumentType.VIDEO, projectId) >= 1) {
            throw new BadRequestException("Chỉ được tải lên 1 video");
        }
    }

    private User getCurrentUser() {
        return this.userRepo.findByEmailAndStatus(this.getCurrentEmail(), true).orElse(null);
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isLeader(Long groupId, Long userId) {
        return this.groupMemberRepo.existsByGroupIdAndMemberRoleNameAndUserId(groupId, "ROLE_LEADER", userId);
    }

    private void isTeacher(User user) {
        if (!user.getRole().getName().equals("ROLE_TEACHER")) {
            throw new BadRequestException(user.getFullName() + " không phải là giáo viên");
        }
    }

    private void checkTeacher() {
        User currentUser = this.getCurrentUser();
        if (!currentUser.getRole().getName().equals("ROLE_TEACHER")) {
            throw new BadRequestException("Chỉ giáo viên mới thực hiện được");
        }
    }

    private void checkLeaderAndExistGroup(Long groupId) {
        User currentUser = this.getCurrentUser();
        if (!this.groupMemberRepo.existsByGroupIdAndUserId(groupId, currentUser.getId())) {
            throw new ResourceNotFoundException("Không tìm thấy group");
        } else if (!this.isLeader(groupId, currentUser.getId())) {
            throw new ResourceNotFoundException("Không phải leader");
        }
    }

    private void checkApproveProject(Project project) {
        if (project.getProjectStatus() == ProjectStatus.APPROVED) {
            throw new BadRequestException("Project đã được duyệt không thể cập nhật");
        }
    }

    private void checkDate(Project project) {
        if (project.getStartDate().isAfter(project.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu phải trước ngày kết thúc");
        } else if (project.getSubmissionDate().isBefore(project.getStartDate())) {
            throw new BadRequestException("Ngày nộp phải sau ngày bắt đầu");
        } else if (project.getEndDate().isAfter(project.getSubmissionDate())) {
            throw new BadRequestException("Ngày kết thúc phải trước ngày nộp");
        }
    }

    private void deleteNotificationByProjectId(Long projectId) {
        List<Long> ids = this.commentRepo.findAllByProjectId(projectId).stream().map(BaseEntity::getId).toList();
        this.notificationRepo.deleteByCommentIds(ids);
        this.notificationRepo.deleteByProjectId(projectId);
    }

}
