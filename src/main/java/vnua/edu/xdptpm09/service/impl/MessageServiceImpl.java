
package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.MessageDTO;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.entity.Message;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.MessageMapping;
import vnua.edu.xdptpm09.repository.MessageRepo;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.IMessageService;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements IMessageService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final MessageMapping messageMapping;

    public Optional<MessageDTO> create(MessageDTO messageDTO) {
        if (messageDTO.getReceiverEmail().equals(this.getCurrentEmail())) {
            throw new BadRequestException("Không thể gửi tin nhắn cho chính mình");
        } else {
            Message message = this.messageMapping.toEntity(messageDTO);
            message = this.messageRepo.saveAndFlush(message);
            messageDTO = this.messageMapping.toDto(message);
            this.simpMessagingTemplate.convertAndSendToUser(messageDTO.getReceiverEmail(), "/queue/messages", messageDTO);
            return Optional.of(this.messageMapping.toDto(message));
        }
    }

    public PageResponse<?> getAllMessageByReceiverIdAndCurrentUser(Pageable pageable, String receiverEmail) {
        Pageable sortedByCreatedDateDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());
        User receiver = this.userRepo.findByEmailAndStatus(receiverEmail, true).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<MessageDTO> page = this.messageRepo.findAllByUsers(sortedByCreatedDateDesc, receiver.getId(), this.getCurrentUser().getId()).map(this.messageMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    public PageResponse<?> getLatestMessages(Pageable pageable) {
        Page<MessageDTO> page = this.messageRepo.findLatestMessages(pageable, this.getCurrentEmail()).map(this.messageMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return this.userRepo.findByEmailAndStatus(this.getCurrentEmail(), true).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
