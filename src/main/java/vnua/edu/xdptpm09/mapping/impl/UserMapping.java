package vnua.edu.xdptpm09.mapping.impl;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.UserDTO;
import vnua.edu.xdptpm09.entity.GroupMember;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.IAzureService;

@Component
@RequiredArgsConstructor
public class UserMapping implements Mapping<User, UserDTO> {
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final IAzureService azureService;

    public User toEntity(UserDTO dto) {
        return this.modelMapper.map(dto, User.class);
    }

    public UserDTO toDto(User entity) {
        UserDTO userDTO = this.modelMapper.map(entity, UserDTO.class);
        userDTO.setRole(entity.getRole().getName());
        int totalProject = 0;
        if (entity.getGroupMemberships() != null && !entity.getGroupMemberships().isEmpty()) {
            List<GroupMember> groupMembers = entity.getGroupMemberships();
            totalProject = groupMembers.size();
        }

        if (entity.getAvatar() != null) {
            userDTO.setAvatarId(entity.getAvatar().getId());
            userDTO.setAvatarUrl(this.azureService.getBlobUrl(entity.getAvatar().getBlobName()));
        }

        userDTO.setTotalsProject(totalProject);
        return userDTO;
    }

    public User updateFromDTO(UserDTO dto) {
        User userExisting = this.userRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        this.modelMapper.map(dto, userExisting);
        return userExisting;
    }

    public List<User> toEntity(List<UserDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<UserDTO> toDto(List<User> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
