//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.GroupMemberDTO;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.GroupMemberMapping;
import vnua.edu.xdptpm09.repository.GroupMemberRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;
import vnua.edu.xdptpm09.service.IGroupMemberService;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements IGroupMemberService {
    private final GroupMemberMapping groupMemberMapping;
    private final GroupMemberRepo groupMemberRepo;
    private final ProjectRepo projectRepo;

    @Cacheable(
            value = {"membersByProjectId"},
            key = "#projectId"
    )
    public List<GroupMemberDTO> getMembersByProjectId(Long projectId) {
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        System.out.println(project.getGroup().getMembers().size());
        return this.groupMemberMapping.toDto(project.getGroup().getMembers());
    }

    @Cacheable(
            value = {"membersByProjectIds"},
            key = "#projectIds"
    )
    public List<GroupMemberDTO> getAllMembersByProjectIds(List<Long> projectIds) {
        return this.groupMemberMapping.toDto(this.groupMemberRepo.findAllByProjectIdIn(projectIds));
    }

}
