package vnua.edu.xdptpm09.mapping.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.NotificationDTO;
import vnua.edu.xdptpm09.entity.Notification;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.NotificationRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;

@Component
@RequiredArgsConstructor
public class NotificationMapping implements Mapping<Notification, NotificationDTO> {
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;
    private final ProjectRepo projectRepo;

    public Notification toEntity(NotificationDTO dto) {
        Notification notification = this.modelMapper.map(dto, Notification.class);
        if (dto.getProjectId() != null) {
            Project project = this.projectRepo.findById(dto.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
            notification.setProject(project);
        }

        return notification;
    }

    public NotificationDTO toDto(Notification entity) {
        NotificationDTO notificationDTO = this.modelMapper.map(entity, NotificationDTO.class);
        if (entity.getProject() != null) {
            notificationDTO.setProjectId(entity.getProject().getId());
        }

        if (entity.getComment() != null) {
            notificationDTO.setCommentId(entity.getComment().getId());
        }

        return notificationDTO;
    }

    public Notification updateFromDTO(NotificationDTO dto) {
        Notification notificationExisting = this.notificationRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy notification"));
        this.modelMapper.map(dto, notificationExisting);
        return notificationExisting;
    }

    public List<Notification> toEntity(List<NotificationDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<NotificationDTO> toDto(List<Notification> entity) {
        return entity.stream().map(this::toDto).toList();
    }

}
