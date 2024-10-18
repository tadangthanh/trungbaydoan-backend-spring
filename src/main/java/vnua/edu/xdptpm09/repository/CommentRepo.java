package vnua.edu.xdptpm09.repository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Comment;
@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.project.id = ?1 and c.parentComment is null")
    Page<Comment> findAllByProjectId(Long projectId, Pageable pageable);

    Page<Comment> findAllByParentCommentId(Long parentCommentId, Pageable pageable);

    @Query("select count(c) from Comment c where c.parentComment.id = ?1")
    int countByParentCommentId(Long parentCommentId);

    @Query("select c from Comment c where c.id = ?1 and c.project.id = ?2")
    Optional<Comment> findByCommentIdAndProjectId(Long commentId, Long projectId);

    int deleteByProjectId(Long projectId);

    @Query("select c from Comment c where c.project.id = ?1")
    List<Comment> findCommentByProjectId(Long projectId);

    @Modifying
    @Transactional
    @Query("delete from Comment c where c.parentComment.id in ?1")
    void deleteCommentByParentCommentIds(List<Long> parentCommentIds);

    List<Comment> findAllByProjectId(Long projectId);
}
