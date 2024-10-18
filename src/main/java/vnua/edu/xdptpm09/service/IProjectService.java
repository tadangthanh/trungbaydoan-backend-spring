package vnua.edu.xdptpm09.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.CreateProjectDTO;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ProjectDTO;
import vnua.edu.xdptpm09.dto.ProjectRequest;
import vnua.edu.xdptpm09.dto.UserDTO;

public interface IProjectService {
    Optional<ProjectDTO> createProject(CreateProjectDTO createProjectDTO, List<MultipartFile> files);

    Optional<ProjectDTO> updateDocumentProject(ProjectDTO projectDTO, List<MultipartFile> documents);

    Optional<ProjectDTO> addDocumentProject(Long projectId, List<MultipartFile> documents);

    Optional<ProjectDTO> removeDocumentProject(Long projectId, Long documentId);

    Optional<ProjectDTO> updateProject(ProjectDTO projectDTO, List<MultipartFile> files);

    int approveProjects(ProjectRequest projectRequest);

    Optional<ProjectDTO> submitProject(Long projectId);

    int inactivateProjects(ProjectRequest projectRequest);

    int activateProjects(ProjectRequest projectRequest);

    void deleteProject(Long projectId);

    int rejectProjects(ProjectRequest projectRequest);

    PageResponse<?> getAllProjectPendingByMemberEmail(String email, Pageable pageable);

    PageResponse<?> getAllProjectActiveAndApproved(Pageable pageable, String searchField, String search);

    PageResponse<?> searchProject(String search, Pageable pageable);

    Optional<ProjectDTO> getProjectById(Long projectId);

    PageResponse<?> getAllProjectByUser(String email, Pageable pageable);

    List<UserDTO> getMentorsByProjectId(Long projectId);

    List<UserDTO> getMembersByProjectId(Long projectId);

    PageResponse<?> getAllProjectByMentor(String email, Pageable pageable);

    PageResponse<?> getAllProjectByAdmin(Pageable pageable, String search, String searchField);

    int deleteProjectByIds(ProjectRequest projectRequest);

    PageResponse<?> getAllProjectByCategoryId(String searchField, String search, Long categoryId, Pageable pageable);
}
