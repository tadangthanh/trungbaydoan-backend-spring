package vnua.edu.xdptpm09.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.entity.ProjectStatus;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Query("select p from Project p where p.active = true and p.projectStatus = vnua.edu.xdptpm09.entity.ProjectStatus.APPROVED")
    Page<Project> findAllByActiveTrueAndProjectStatusApproved(Specification<Project> spec, Pageable pageable);

    @Query("select p from Project p join p.group g join g.members m where p.active = true and p.projectStatus = vnua.edu.xdptpm09.entity.ProjectStatus.PENDING and m.user.email = :email")
    Page<Project> findAllByProjectStatusPendingAndMemberEmail(String email, Pageable pageable);

    @Query("select p from Project p where lower(p.name) like lower(concat('%', :search, '%')) or lower(p.description) like lower(concat('%', :search, '%')) or lower(p.summary) like lower(concat('%', :search, '%'))")
    Page<Project> searchAllByNameOrDescriptionOrSummary(String search, Pageable pageable);

    @Query("select p from Project p where p.group.id in :groupIds and p.active = true and p.projectStatus = vnua.edu.xdptpm09.entity.ProjectStatus.APPROVED")
    Page<Project> findAllByGroupIdsAndActiveAndApproved(List<Long> groupIds, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.mentors m WHERE m.id = :mentorId and p.active = true and p.projectStatus = vnua.edu.xdptpm09.entity.ProjectStatus.APPROVED")
    Page<Project> findAllByMentorIdAndApprovedAndActive(Long mentorId, Pageable pageable);

    @Query("select p from Project p where p.id in :projectIds")
    List<Project> findAllByIds(List<Long> projectIds);

    int deleteProjectById(Long projectId);

    Optional<Project> findProjectByIdAndActiveAndProjectStatus(Long projectId, boolean active, ProjectStatus projectStatus);

    @Query("select p from Project p where p.category.id = :categoryId and p.active = true and p.projectStatus = vnua.edu.xdptpm09.entity.ProjectStatus.APPROVED")
    Page<Project> findAllByCategoryIdAndActiveTrueAndProjectStatusApproved(Specification<Project> categoryId, Pageable pageable);

}
