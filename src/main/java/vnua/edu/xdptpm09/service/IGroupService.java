package vnua.edu.xdptpm09.service;

import java.util.Optional;
import vnua.edu.xdptpm09.dto.GroupDTO;

public interface IGroupService {
    Optional<GroupDTO> create(GroupDTO groupDTO);

    Optional<GroupDTO> addMemberWithStudentCode(Long groupId, String studentCode);

    Optional<GroupDTO> removeMember(Long groupId, Long memberId);

    void deleteGroup(Long groupId);

    Optional<GroupDTO> getGroupByProjectId(Long projectId);
}
