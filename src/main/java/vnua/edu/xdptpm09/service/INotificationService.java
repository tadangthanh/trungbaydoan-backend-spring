
package vnua.edu.xdptpm09.service;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import vnua.edu.xdptpm09.dto.NotificationDTO;
import vnua.edu.xdptpm09.dto.PageResponse;

public interface INotificationService {
    Optional<NotificationDTO> create(NotificationDTO notificationDTO);

    void seenNotification(Long id);

    void deleteNotification(Long id);

    void deleteNotificationByProjectId(Long projectId);

    PageResponse<?> getAllByUserId(Long userId, Pageable pageable);

    int countByNotSeen();

    void seenAll(String email);
}
