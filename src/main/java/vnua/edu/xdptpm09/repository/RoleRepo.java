package vnua.edu.xdptpm09.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    boolean existsRoleByName(String name);
}
