package vnua.edu.xdptpm09.mapping.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.ProjectDTO;
import vnua.edu.xdptpm09.entity.AcademyYear;
import vnua.edu.xdptpm09.entity.BaseEntity;
import vnua.edu.xdptpm09.entity.Category;
import vnua.edu.xdptpm09.entity.Document;
import vnua.edu.xdptpm09.entity.Group;
import vnua.edu.xdptpm09.entity.GroupMember;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.AcademyYearRepo;
import vnua.edu.xdptpm09.repository.CategoryRepo;
import vnua.edu.xdptpm09.repository.DocumentRepo;
import vnua.edu.xdptpm09.repository.GroupRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;
import vnua.edu.xdptpm09.repository.UserRepo;

@Component
@RequiredArgsConstructor
public class ProjectMapping implements Mapping<Project, ProjectDTO> {
    private final ProjectRepo projectRepo;
    private final ModelMapper modelMapper;
    private final DocumentRepo documentRepo;
    private final AcademyYearRepo academyYearRepo;
    private final UserRepo userRepo;
    private final GroupRepo groupRepo;
    private final CategoryRepo categoryRepo;

    public Project toEntity(ProjectDTO dto) {
        Project project = this.modelMapper.map(dto, Project.class);
        Category category = this.categoryRepo.findById(dto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại này"));
        project.setCategory(category);
        List<Document> documents = this.documentRepo.findAllById(dto.getDocumentIds());
        project.setDocuments(documents);
        Group group = this.groupRepo.findById(dto.getGroupId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm này"));
        User approver = this.userRepo.findById(dto.getApproverId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt này"));
        if (dto.getMentorIds() != null && !dto.getMentorIds().isEmpty()) {
            List<User> mentors = this.userRepo.findAllById(dto.getMentorIds());
            AcademyYear academyYear = this.academyYearRepo.findByNumber(dto.getAcademicYear()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa này"));
            project.setGroup(group);
            project.setApprover(approver);
            project.setMentors(mentors);
            project.setAcademyYear(academyYear);
            return project;
        } else {
            throw new ResourceNotFoundException("Không tìm thấy người hướng dẫn");
        }
    }

    public ProjectDTO toDto(Project entity) {
        ProjectDTO projectDTO = this.modelMapper.map(entity, ProjectDTO.class);
        projectDTO.setCategoryId(entity.getCategory().getId());
        if (entity.getDocuments() != null) {
            List<Long> documentIds = entity.getDocuments().stream().map(BaseEntity::getId).toList();
            projectDTO.setDocumentIds(documentIds);
        }

        projectDTO.setGroupId(entity.getGroup().getId());
        if (entity.getApprover() != null) {
            projectDTO.setApproverId(entity.getApprover().getId());
        }

        projectDTO.setMentorIds(entity.getMentors().stream().map(BaseEntity::getId).toList());
        if (entity.getAcademyYear() != null) {
            projectDTO.setAcademicYear(entity.getAcademyYear().getNumber());
        }

        if (entity.getCategory() != null) {
            projectDTO.setCategoryName(entity.getCategory().getName());
        }

        if (entity.getApprover() != null) {
            projectDTO.setApproverName(entity.getApprover().getFullName());
        }

        if (!entity.getTechnologies().isEmpty()) {
            projectDTO.setIdsTechnology(entity.getTechnologies().stream().map(BaseEntity::getId).toList());
        }

        projectDTO.setMemberNames(entity.getGroup().getMembers().stream().map(GroupMember::getUser).map(User::getFullName).toList());
        projectDTO.setMentorNames(entity.getMentors().stream().map(User::getFullName).toList());
        return projectDTO;
    }

    public Project updateFromDTO(ProjectDTO dto) {
        Project projectExisting = this.projectRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dự án này"));
        this.modelMapper.map(dto, projectExisting);
        return projectExisting;
    }

    public List<Project> toEntity(List<ProjectDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<ProjectDTO> toDto(List<Project> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
