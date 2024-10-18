package vnua.edu.xdptpm09.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Message;

@Repository
public interface MessageRepo  extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE (m.receiver.id = :userId1 AND m.author.id = :userId2) OR (m.receiver.id = :userId2 AND m.author.id = :userId1) ORDER BY m.createdDate DESC")
    Page<Message> findAllByUsers(Pageable pageable, @Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE m.id IN (SELECT MAX(m2.id) FROM Message m2 WHERE m2.receiver.email = :currentEmail OR m2.author.email = :currentEmail GROUP BY CASE WHEN m2.receiver.email = :currentEmail THEN m2.author.email ELSE m2.receiver.email END)ORDER BY m.createdDate DESC")
    Page<Message> findLatestMessages(Pageable pageable, @Param("currentEmail") String currentEmail);
}
