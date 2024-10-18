package vnua.edu.xdptpm09.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.MemberRole;

@Repository
public interface MemberRoleRepo extends JpaRepository<MemberRole, Long> {
    boolean existsMemberRoleByName(String name);

    Optional<MemberRole> findMemberRoleByName(String name);
}
