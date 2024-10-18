package vnua.edu.xdptpm09.mapping.impl;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.MessageDTO;
import vnua.edu.xdptpm09.entity.Message;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.MessageRepo;
import vnua.edu.xdptpm09.repository.UserRepo;

@Component
@RequiredArgsConstructor
public class MessageMapping implements Mapping<Message, MessageDTO> {
    private final MessageRepo messageRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    public Message toEntity(MessageDTO dto) {
        Message message = this.modelMapper.map(dto, Message.class);
        if (dto.getAuthorEmail() != null) {
            message.setAuthor(this.userRepo.findByEmailAndStatus(dto.getAuthorEmail(), true).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        }

        if (dto.getReceiverEmail() != null) {
            message.setReceiver(this.userRepo.findByEmailAndStatus(dto.getReceiverEmail(), true).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        }

        return message;
    }

    public MessageDTO toDto(Message entity) {
        MessageDTO messageDTO = this.modelMapper.map(entity, MessageDTO.class);
        if (entity.getAuthor() != null) {
            messageDTO.setAuthorEmail(entity.getAuthor().getEmail());
        }

        if (entity.getReceiver() != null) {
            messageDTO.setReceiverEmail(entity.getReceiver().getEmail());
        }

        return messageDTO;
    }

    public Message updateFromDTO(MessageDTO dto) {
        Message messageExisting = this.messageRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        messageExisting.setContent(dto.getContent());
        return messageExisting;
    }

    public List<Message> toEntity(List<MessageDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<MessageDTO> toDto(List<Message> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
