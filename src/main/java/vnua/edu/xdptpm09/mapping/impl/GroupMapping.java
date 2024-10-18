package vnua.edu.xdptpm09.mapping.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.GroupDTO;
import vnua.edu.xdptpm09.entity.BaseEntity;
import vnua.edu.xdptpm09.entity.Group;
import vnua.edu.xdptpm09.entity.GroupMember;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.GroupMemberRepo;
import vnua.edu.xdptpm09.repository.GroupRepo;

@Component
@RequiredArgsConstructor
public class GroupMapping  implements Mapping<Group, GroupDTO> {
    private final GroupRepo groupRepo;
    private final GroupMemberRepo groupMemberRepo;
    private final ModelMapper modelMapper;

    public Group toEntity(GroupDTO dto) {
        Group group = this.modelMapper.map(dto, Group.class);
        if (dto.getMemberIds() != null && !dto.getMemberIds().isEmpty()) {
            List<GroupMember> members = this.groupMemberRepo.findAllById(dto.getMemberIds());
            group.setMembers(members);
        }

        return this.getGroup(dto, group);
    }

    private Group getGroup(GroupDTO dto, Group group) {
        if (!this.isEmptyOrNull(dto.getMemberIds())) {
            List<GroupMember> members = this.groupMemberRepo.findAllById(dto.getMemberIds());
            group.setMembers(members);
        }

        return group;
    }

    public GroupDTO toDto(Group entity) {
        GroupDTO groupDTO = this.modelMapper.map(entity, GroupDTO.class);
        if (entity.getProject() != null) {
            groupDTO.setProjectId(entity.getProject().getId());
        }

        if (entity.getMembers() != null && !entity.getMembers().isEmpty()) {
            List<Long> memberIds = entity.getMembers().stream().map(GroupMember::getUser).map(BaseEntity::getId).toList();
            groupDTO.setMemberIds(memberIds);
            List<String> memberNames = entity.getMembers().stream().map(GroupMember::getUser).map(User::getFullName).toList();
            groupDTO.setMemberNames(memberNames);
        }

        return groupDTO;
    }

    public Group updateFromDTO(GroupDTO dto) {
        Group groupExisting =this.groupRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm với id: " + dto.getId()));
        return this.getGroup(dto, groupExisting);
    }

    public List<Group> toEntity(List<GroupDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<GroupDTO> toDto(List<Group> entity) {
        return entity.stream().map(this::toDto).toList();
    }

    private boolean isEmptyOrNull(List<Long> list) {
        return list == null || list.isEmpty();
    }
}
