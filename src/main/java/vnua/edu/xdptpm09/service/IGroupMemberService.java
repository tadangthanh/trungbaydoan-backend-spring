
package vnua.edu.xdptpm09.service;

import java.util.List;
import vnua.edu.xdptpm09.dto.GroupMemberDTO;

public interface IGroupMemberService {
    List<GroupMemberDTO> getMembersByProjectId(Long projectId);

    List<GroupMemberDTO> getAllMembersByProjectIds(List<Long> projectIds);
}
