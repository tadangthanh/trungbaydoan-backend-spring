package vnua.edu.xdptpm09.mapping.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.CommentDTO;
import vnua.edu.xdptpm09.entity.Comment;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.CommentRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;
import vnua.edu.xdptpm09.service.IAzureService;
@Component
@RequiredArgsConstructor
public class CommentMapping  implements Mapping<Comment, CommentDTO> {
    private final ModelMapper modelMapper;
    private final CommentRepo commentRepo;
    private final ProjectRepo projectRepo;
    private final IAzureService azureService;

    public Comment toEntity(CommentDTO dto) {
        Comment comment = this.modelMapper.map(dto, Comment.class);
        Project project = this.projectRepo.findById(dto.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đồ án"));
        if (dto.getParentCommentId() != null && dto.getParentCommentId() != 0L) {
            Comment parentComment = this.commentRepo.findById(dto.getParentCommentId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận"));
            comment.setParentComment(parentComment);
        }

        comment.setProject(project);
        return comment;
    }

    public CommentDTO toDto(Comment entity) {
        CommentDTO commentDTO = this.modelMapper.map(entity, CommentDTO.class);
        commentDTO.setAuthorName(entity.getAuthor().getFullName());
        commentDTO.setProjectId(entity.getProject().getId());
        if (entity.getParentComment() != null) {
            commentDTO.setParentCommentId(entity.getParentComment().getId());
        }

        commentDTO.setAuthorEmail(entity.getAuthor().getEmail());
        commentDTO.setTotalReply(this.commentRepo.countByParentCommentId(entity.getId()));
        if (entity.getAuthor().getAvatar() != null) {
            commentDTO.setAuthorAvatarId(entity.getAuthor().getAvatar().getId());
            commentDTO.setAuthorAvatarUrl(this.azureService.getBlobUrl(entity.getAuthor().getAvatar().getBlobName()));
        }

        return commentDTO;
    }

    public Comment updateFromDTO(CommentDTO dto) {
        Comment commentExisting = this.commentRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận"));
        commentExisting.setContent(dto.getContent());
        return commentExisting;
    }

    public List<Comment> toEntity(List<CommentDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<CommentDTO> toDto(List<Comment> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
