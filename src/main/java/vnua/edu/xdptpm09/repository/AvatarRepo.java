package vnua.edu.xdptpm09.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Avatar;
@Repository
public interface AvatarRepo extends JpaRepository<Avatar, Long> {
}
