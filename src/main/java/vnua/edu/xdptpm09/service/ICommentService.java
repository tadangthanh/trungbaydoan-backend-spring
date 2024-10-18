
package vnua.edu.xdptpm09.service;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import vnua.edu.xdptpm09.dto.CommentDTO;
import vnua.edu.xdptpm09.dto.PageResponse;

public interface ICommentService {
    void delete(Long id);

    Optional<CommentDTO> update(CommentDTO commentDTO);

    Optional<CommentDTO> save(CommentDTO commentDTO);

    PageResponse<?> getAllByProjectId(Long projectId, Pageable pageable);

    PageResponse<?> getAllByParentCommentId(Long parentCommentId, Pageable pageable);

    Optional<CommentDTO> getCommentByCommentIdAndProjectId(Long commentId, Long projectId);

    int deleteByProjectId(Long projectId);
}
