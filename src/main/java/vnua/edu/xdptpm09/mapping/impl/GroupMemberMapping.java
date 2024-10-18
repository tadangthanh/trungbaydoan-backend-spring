package vnua.edu.xdptpm09.mapping.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.GroupMemberDTO;
import vnua.edu.xdptpm09.entity.GroupMember;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.GroupMemberRepo;
import vnua.edu.xdptpm09.repository.MemberRoleRepo;
import vnua.edu.xdptpm09.service.IAzureService;

@Component
@RequiredArgsConstructor
public class GroupMemberMapping implements Mapping<GroupMember, GroupMemberDTO> {
    private final GroupMemberRepo groupMemberRepo;
    private final MemberRoleRepo memberRoleRepo;
    private final ModelMapper modelMapper;
    private final IAzureService azureService;

    public GroupMember toEntity(GroupMemberDTO dto) {
        return this.modelMapper.map(dto, GroupMember.class);
    }

    public GroupMemberDTO toDto(GroupMember entity) {
        GroupMemberDTO groupMemberDTO = this.modelMapper.map(entity, GroupMemberDTO.class);
        groupMemberDTO.setId(entity.getUser().getId());
        if (entity.getMemberRole() != null) {
            groupMemberDTO.setRole(entity.getMemberRole().getName());
        }

        if (entity.getUser() != null) {
            groupMemberDTO.setUserId(entity.getUser().getId());
            groupMemberDTO.setMemberName(entity.getUser().getFullName());
            groupMemberDTO.setEmail(entity.getUser().getEmail());
            groupMemberDTO.setClassName(entity.getUser().getClassName());
            groupMemberDTO.setDepartment(entity.getUser().getDepartment());
            groupMemberDTO.setMajor(entity.getUser().getMajor());
            if (entity.getUser().getAvatar() != null) {
                groupMemberDTO.setAvatarUrl(this.azureService.getBlobUrl(entity.getUser().getAvatar().getBlobName()));
            }

            groupMemberDTO.setAcademicYear(entity.getUser().getAcademicYear());
        }

        if (entity.getGroup() != null) {
            groupMemberDTO.setGroupId(entity.getGroup().getId());
        }

        if (entity.getGroup() != null && entity.getGroup().getProject() != null) {
            groupMemberDTO.setProjectId(entity.getGroup().getProject().getId());
        }

        return groupMemberDTO;
    }

    public GroupMember updateFromDTO(GroupMemberDTO dto) {
        return null;
    }

    public List<GroupMember> toEntity(List<GroupMemberDTO> dto) {
        return List.of();
    }

    public List<GroupMemberDTO> toDto(List<GroupMember> entity) {
        return entity.stream().map(this::toDto).toList();
    }

}
