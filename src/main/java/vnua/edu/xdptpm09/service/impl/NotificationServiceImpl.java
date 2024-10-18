//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.NotificationDTO;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.entity.Notification;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.NotificationMapping;
import vnua.edu.xdptpm09.repository.NotificationRepo;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.INotificationService;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {
    private final NotificationMapping notificationMapping;
    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;

    @Caching(
            evict = {@CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public Optional<NotificationDTO> create(NotificationDTO notificationDTO) {
        Notification notification = this.notificationMapping.toEntity(notificationDTO);
        notification = this.notificationRepo.saveAndFlush(notification);
        return Optional.of(this.notificationMapping.toDto(notification));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public void seenNotification(Long id) {
        Notification notification = this.notificationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        notification.setSeen(true);
        this.notificationRepo.save(notification);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public void deleteNotification(Long id) {
        this.checkTeacher();
        Notification notification = this.notificationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        this.notificationRepo.delete(notification);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public void deleteNotificationByProjectId(Long projectId) {
        this.checkTeacher();
        this.notificationRepo.deleteByProjectId(projectId);
    }

    @Cacheable(
            value = {"notificationsByUserId"},
            key = "#userId"
    )
    public PageResponse<?> getAllByUserId(Long userId, Pageable pageable) {
        Page<NotificationDTO> page = this.notificationRepo.findAllByUserId(userId, pageable).map(this.notificationMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int)page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public int countByNotSeen() {
        return this.notificationRepo.countNotificationByUserEmailAndNotSeen(this.getCurrentEmail(), false);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"notificationsByUserId"},
                    allEntries = true
            )}
    )
    public void seenAll(String email) {
        this.notificationRepo.seenAllByUserEmail(email);
    }

    private User getCurrentUser() {
        return this.userRepo.findByEmailAndStatus(this.getCurrentEmail(), true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void checkTeacher() {
        User currentUser = this.getCurrentUser();
        if (!currentUser.getRole().getName().equals("ROLE_TEACHER")) {
            throw new BadRequestException("Bạn không có quyền thực hiện chức năng này");
        }
    }

}
