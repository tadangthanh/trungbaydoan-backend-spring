package vnua.edu.xdptpm09.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.GroupMember;

@Repository
public interface GroupMemberRepo extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndMemberRoleNameAndUserId(Long groupId, String memberRoleName, Long userId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    boolean existsByGroupIdAndId(Long groupId, Long id);

    @Query("select gm from GroupMember gm where gm.group.id = :groupId and gm.memberRole.name = 'ROLE_LEADER'")
    GroupMember findLeaderByGroupId(Long groupId);

    List<GroupMember> findAllByGroupProjectId(Long projectId);

    @Query("select gm from GroupMember gm where gm.group.project.id in :projectIds")
    List<GroupMember> findAllByProjectIdIn(List<Long> projectIds);
}
