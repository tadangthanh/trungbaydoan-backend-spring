//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.CommentDTO;
import vnua.edu.xdptpm09.dto.NotificationDTO;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.entity.BaseEntity;
import vnua.edu.xdptpm09.entity.Comment;
import vnua.edu.xdptpm09.entity.GroupMember;
import vnua.edu.xdptpm09.entity.Notification;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.exception.UnauthorizedException;
import vnua.edu.xdptpm09.mapping.impl.CommentMapping;
import vnua.edu.xdptpm09.mapping.impl.NotificationMapping;
import vnua.edu.xdptpm09.repository.CommentRepo;
import vnua.edu.xdptpm09.repository.NotificationRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.ICommentService;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    private final CommentRepo commentRepo;
    private final NotificationRepo notificationRepo;
    private final NotificationMapping notificationMapping;
    private final CommentMapping commentMapping;
    private final UserRepo userRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProjectRepo projectRepo;
    private final NotificationRepo notificationRepository;

    @Caching(
            evict = {@CacheEvict(
                    value = {"commentsByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectIdAndParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public void delete(Long id) {
        this.notificationRepository.deleteByCommentId(id);
        Comment comment = this.commentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!Objects.equals(comment.getAuthor().getId(), this.getCurrentUser().getId())) {
            throw new UnauthorizedException("Bạn không có quyền xóa bình luận này");
        } else {
            if (comment.getReplies() != null) {
                comment.getReplies().forEach((reply) -> this.notificationRepository.deleteByCommentId(reply.getId()));
            }

            this.commentRepo.deleteById(id);
        }
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"commentsByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectIdAndParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public Optional<CommentDTO> update(CommentDTO commentDTO) {
        Comment commentNew = this.commentMapping.updateFromDTO(commentDTO);
        if (!Objects.equals(commentNew.getAuthor().getId(), this.getCurrentUser().getId())) {
            throw new UnauthorizedException("Bạn không có quyền chỉnh sửa bình luận này");
        } else {
            commentNew = this.commentRepo.saveAndFlush(commentNew);
            return Optional.of(this.commentMapping.toDto(commentNew));
        }
    }

    private User getCurrentUser() {
        return this.userRepo.findByEmailAndStatus(SecurityContextHolder.getContext().getAuthentication().getName(), true).orElseThrow(() -> new ResourceNotFoundException("Not found user"));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"commentsByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectIdAndParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public Optional<CommentDTO> save(CommentDTO commentDTO) {
        Comment comment = this.commentMapping.toEntity(commentDTO);
        comment.setAuthor(this.getCurrentUser());
        comment = this.commentRepo.saveAndFlush(comment);
        this.sendNotification(comment, commentDTO);
        return Optional.of(this.commentMapping.toDto(comment));
    }

    private void sendNotification(Comment comment, CommentDTO commentDTO) {
        if (comment.getParentComment() == null || !Objects.equals(comment.getParentComment().getAuthor().getId(), this.getCurrentUser().getId())) {
            if (comment.getParentComment() == null) {
                List<User> receivers = this.getMemberProject(comment.getProject().getId());
                for (User receiver : receivers) {
                    Notification notification = new Notification();
                    notification.setProject(comment.getProject());
                    notification.setMessage("Bình luận  từ " + comment.getAuthor().getFullName());
                    notification.setComment(comment);
                    notification.setReceiver(receiver);
                    this.notificationRepo.saveAndFlush(notification);
                    NotificationDTO notificationDTO = this.notificationMapping.toDto(notification);
                    this.messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/topic/notification", notificationDTO);
                }
            } else {
                Notification notification;
                NotificationDTO notificationDTO;
                if (commentDTO.getReceiverEmail() != null && !commentDTO.getReceiverEmail().equals(this.getCurrentUser().getEmail())) {
                    notification = new Notification();
                    notification.setProject(comment.getProject());
                    notification.setMessage("Bình luận  từ " + comment.getAuthor().getFullName());
                    notification.setComment(comment);
                    notification.setReceiver(this.userRepo.findByEmailAndStatus(commentDTO.getReceiverEmail(), true).orElseThrow(() -> new ResourceNotFoundException("User not found")));
                    this.notificationRepo.saveAndFlush(notification);
                    notificationDTO = this.notificationMapping.toDto(notification);
                    this.messagingTemplate.convertAndSendToUser(commentDTO.getReceiverEmail(), "/topic/notification", notificationDTO);
                } else {
                    notification = new Notification();
                    notification.setProject(comment.getProject());
                    notification.setMessage("Bình luận  từ " + comment.getAuthor().getFullName());
                    notification.setComment(comment);
                    notification.setReceiver(comment.getParentComment().getAuthor());
                    this.notificationRepo.saveAndFlush(notification);
                    notificationDTO = this.notificationMapping.toDto(notification);
                    this.messagingTemplate.convertAndSendToUser(commentDTO.getReceiverEmail(), "/topic/notification", notificationDTO);
                }
            }

        }
    }

    private List<User> getMemberProject(Long projectId) {
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        List<Long> idsMember = new ArrayList<>();
        for (GroupMember groupMember : project.getGroup().getMembers()) {
            User user = groupMember.getUser();
            Long id = user.getId();
            if (!Objects.equals(id, this.getCurrentUser().getId())) {
                idsMember.add(id);
            }
        }
        return this.userRepo.findAllBydIdInAndStatus(idsMember, true);
    }

    @Cacheable(
            value = {"commentsByProjectId"},
            key = "#projectId + '-'+#pageable.pageNumber+ '-' + #pageable.pageSize "
    )
    public PageResponse<?> getAllByProjectId(Long projectId, Pageable pageable) {
        Page<CommentDTO> page = this.commentRepo.findAllByProjectId(projectId, pageable).map(this.commentMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).hasNext(page.hasNext()).items(page.getContent()).build();
    }

    @Cacheable(
            value = {"commentsByParentCommentId"},
            key = "#parentCommentId + '-'+#pageable.pageNumber + '-' + #pageable.pageSize "
    )
    public PageResponse<?> getAllByParentCommentId(Long parentCommentId, Pageable pageable) {
        Page<CommentDTO> page = this.commentRepo.findAllByParentCommentId(parentCommentId, pageable).map(this.commentMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).hasNext(page.hasNext()).items(page.getContent()).build();
    }

    @Cacheable(
            value = {"commentsByProjectIdAndParentCommentId"},
            key = "#projectId + '-' + #commentId"
    )
    public Optional<CommentDTO> getCommentByCommentIdAndProjectId(Long commentId, Long projectId) {
        Comment comment = this.commentRepo.findByCommentIdAndProjectId(commentId, projectId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return Optional.of(this.commentMapping.toDto(comment));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"commentsByProjectId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"commentsByProjectIdAndParentCommentId"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public int deleteByProjectId(Long projectId) {
        List<Comment> comments = this.commentRepo.findCommentByProjectId(projectId);
        List<Long> commentIds = comments.stream().map(BaseEntity::getId).toList();
        this.commentRepo.deleteAll(comments);
        this.notificationRepository.deleteByCommentIds(commentIds);
        return 1;
    }

}
