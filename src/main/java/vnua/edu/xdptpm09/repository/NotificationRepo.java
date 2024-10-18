package vnua.edu.xdptpm09.repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Notification;
import jakarta.transaction.Transactional;
@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    void deleteByProjectId(Long projectId);

    @Query("select n from Notification n join n.receiver r where r.id = ?1")
    Page<Notification> findAllByUserId(Long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from Notification n where n.comment.id = ?1")
    void deleteByCommentId(Long commentId);

    @Transactional
    @Modifying
    @Query("delete from Notification n where n.comment.id in ?1")
    void deleteByCommentIds(List<Long> commentIds);

    @Query("select count(n) from Notification n where n.receiver.email = ?1 and n.seen = ?2")
    int countNotificationByUserEmailAndNotSeen(String userEmail, boolean seen);

    @Query("select n from Notification n where n.comment.id = ?1")
    List<Notification> findByCommentId(Long commentId);

    @Transactional
    @Modifying
    @Query("update Notification n set n.seen = true where n.receiver.email = ?1")
    void seenAllByUserEmail(String email);
}
