
package vnua.edu.xdptpm09.service.impl;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditingService implements AuditorAware<String> {
    public AuditingService() {
    }

    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() ? Optional.of(authentication.getName()) : Optional.empty();
    }
}
