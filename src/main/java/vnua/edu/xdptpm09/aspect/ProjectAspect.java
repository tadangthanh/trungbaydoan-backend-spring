package vnua.edu.xdptpm09.aspect;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.NotificationDTO;


@Component
@Aspect
@RequiredArgsConstructor
public class ProjectAspect {
    private final SimpMessagingTemplate messagingTemplate;

    @AfterReturning(
            pointcut = "execution(* vnua.edu.xdptpm09.service.IProjectService.createProject(..))",
            returning = "result"
    )
    public void afterReturningCreateProject(Object result) {
        if (result != null) {
            System.out.println("Giá trị trả về: " + result);
        } else {
            System.out.println("Phương thức không trả về giá trị.");
        }

    }

    @AfterThrowing(
            pointcut = "execution(* vnua.edu.xdptpm09.service.IProjectService.createProject(..))",
            throwing = "ex"
    )
    public void afterThrowingCreateProject(Exception ex) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage(ex.getMessage());
        this.messagingTemplate.convertAndSendToUser(this.getCurrentEmail(), "/topic/notification", notificationDTO);
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
