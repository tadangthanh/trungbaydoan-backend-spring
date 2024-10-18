
package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.GroupDTO;
import vnua.edu.xdptpm09.entity.Group;
import vnua.edu.xdptpm09.entity.GroupMember;
import vnua.edu.xdptpm09.entity.MemberRole;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.GroupMapping;
import vnua.edu.xdptpm09.repository.*;
import vnua.edu.xdptpm09.service.IGroupService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupServiceImpl implements IGroupService {
    private final GroupMapping groupMapping;
    private final GroupRepo groupRepo;
    private final ProjectRepo projectRepo;
    private final GroupMemberRepo groupMemberRepo;
    private final UserRepo userRepo;
    private final MemberRoleRepo memberRoleRepo;

    @Caching(
            evict = {@CacheEvict(
                    value = {"groups"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"group"},
                    allEntries = true
            )}
    )
    public Optional<GroupDTO> create(GroupDTO groupDTO) {
        List<User> members = this.userRepo.findByIdIn(groupDTO.getMemberIds());
        List<GroupMember> groupMembers = this.createMembers(members);
        Group group = this.initGroup(this.getCurrentUser());
        group.setMembers(groupMembers);
        group = this.groupRepo.saveAndFlush(group);
        for (GroupMember groupMember : groupMembers) {
            groupMember.setGroup(group);
            this.groupMemberRepo.saveAndFlush(groupMember);
        }
        return Optional.of(this.groupMapping.toDto(group));
    }

    private List<GroupMember> createMembers(List<User> users) {
        return users.stream().map((user) -> {
            GroupMember groupMember = new GroupMember();
            groupMember.setUser(user);
            MemberRole memberRole = this.memberRoleRepo.findMemberRoleByName("ROLE_MEMBER").orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ROLE_MEMBER"));
            groupMember.setMemberRole(memberRole);
            return groupMember;
        }).toList();
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"groups"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"group"},
                    allEntries = true
            )}
    )
    public Optional<GroupDTO> addMemberWithStudentCode(Long groupId, String studentCode) {
        User user = this.getCurrentUser();
        Group group =this.groupRepo.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy group"));
        if (!this.isLeader(groupId, user.getId())) {
            throw new BadRequestException("Bạn không phải leader của group này");
        } else {
            User newMember = this.userRepo.findByEmailAndStatus(studentCode + "@sv.vnua.edu", true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
            if (this.existMemberWithUserId(groupId, newMember.getId())) {
                throw new BadRequestException("Thành viên đã tồn tại trong group");
            } else {
                GroupMember groupMember = new GroupMember();
                groupMember.setUser(newMember);
                groupMember.setGroup(group);
                MemberRole memberRole = this.memberRoleRepo.findMemberRoleByName("ROLE_MEMBER").orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role member"));
                groupMember.setMemberRole(memberRole);
                group.addMember(groupMember);
                this.groupRepo.saveAndFlush(group);
                return Optional.of(this.groupMapping.toDto(group));
            }
        }
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"groups"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"group"},
                    allEntries = true
            )}
    )
    public Optional<GroupDTO> removeMember(Long groupId, Long memberId) {
        Group group = this.groupRepo.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm"));
        User user = this.getCurrentUser();
        if (!this.isLeader(groupId, user.getId())) {
            throw new BadRequestException("Bạn không phải leader của group này");
        } else if (!this.existMemberWithMemberId(groupId, memberId)) {
            throw new BadRequestException("Thành viên không tồn tại trong group");
        } else {
            GroupMember groupMember = this.groupMemberRepo.findById(memberId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên"));
            group.removeMember(groupMember);
            return Optional.of(this.groupMapping.toDto(group));
        }
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"groups"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"group"},
                    allEntries = true
            )}
    )
    public void deleteGroup(Long groupId) {
        User user = this.getCurrentUser();
        if (!this.isLeader(groupId, user.getId())) {
            throw new BadRequestException("Bạn không phải leader của group này");
        } else {
            Group group = this.groupRepo.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy group"));
            this.groupRepo.delete(group);
        }
    }

    public Optional<GroupDTO> getGroupByProjectId(Long projectId) {
        return this.projectRepo.findById(projectId).map((project) -> this.groupMapping.toDto(project.getGroup()));
    }

    private boolean isLeader(Long groupId, Long userId) {
        return this.groupMemberRepo.existsByGroupIdAndMemberRoleNameAndUserId(groupId, "ROLE_LEADER", userId);
    }

    private User getCurrentUser() {
        return this.userRepo.findByEmailAndStatus(this.getCurrentEmail(), true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
    }

    private Group initGroup(User user) {
        Group group = new Group();
        GroupMember leader = new GroupMember();
        leader.setUser(user);
        MemberRole memberRole = this.memberRoleRepo.findMemberRoleByName("ROLE_LEADER").orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role leader"));
        leader.setMemberRole(memberRole);
        this.groupMemberRepo.saveAndFlush(leader);
        group.addMember(leader);
        leader.setGroup(group);
        return group;
    }

    private boolean existMemberWithUserId(Long groupId, Long userId) {
        return this.groupMemberRepo.existsByGroupIdAndUserId(groupId, userId);
    }

    private boolean existMemberWithMemberId(Long groupId, Long memberId) {
        return this.groupMemberRepo.existsByGroupIdAndId(groupId, memberId);
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
