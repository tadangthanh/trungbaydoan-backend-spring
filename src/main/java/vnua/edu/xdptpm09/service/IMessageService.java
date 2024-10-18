
package vnua.edu.xdptpm09.service;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import vnua.edu.xdptpm09.dto.MessageDTO;
import vnua.edu.xdptpm09.dto.PageResponse;

public interface IMessageService {
    Optional<MessageDTO> create(MessageDTO messageDTO);

    PageResponse<?> getAllMessageByReceiverIdAndCurrentUser(Pageable pageable, String receiverEmail);

    PageResponse<?> getLatestMessages(Pageable pageable);
}
