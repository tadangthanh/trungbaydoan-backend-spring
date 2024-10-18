//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.GroupDTO;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.IGroupService;

@RestController
@RequestMapping({"/api/v1/groups"})
@RequiredArgsConstructor
public class GroupRest {
    private final IGroupService groupService;

    @PostMapping
    public ResponseEntity<ResponseCustom> create(@RequestBody GroupDTO groupDTO) {
        groupDTO = this.groupService.create(groupDTO).orElse(null);
        return groupDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Tạo group thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Tạo group thành công", HttpStatus.CREATED.value(), groupDTO));
    }

    @PostMapping({"/{id}/group-members"})
    public ResponseEntity<ResponseCustom> addMemberWithStudentCode(@PathVariable @Min(1L) Long id, @RequestParam("code") String studentCode) {
        GroupDTO groupDTO = this.groupService.addMemberWithStudentCode(id, studentCode).orElse(null);
        return groupDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Thêm member thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Thêm member thành công", HttpStatus.CREATED.value(), groupDTO));
    }

    @DeleteMapping({"/{id}/group-members/{userId}"})
    public ResponseEntity<ResponseCustom> removeMember(@PathVariable("id") @Min(1L) Long groupId, @PathVariable @Min(1L) Long userId) {
        GroupDTO groupDTO = this.groupService.removeMember(groupId, userId).orElse(null);
        return groupDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Xóa member thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Xóa member thành công", HttpStatus.OK.value(), groupDTO));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> deleteGroup(@PathVariable("id") @Min(1L) Long groupId) {
        this.groupService.deleteGroup(groupId);
        return ResponseEntity.ok(new ResponseCustom("Xóa group thành công", HttpStatus.OK.value(), null));
    }

    @GetMapping({"/project/{projectId}"})
    public ResponseEntity<ResponseCustom> getGroupByProjectId(@PathVariable @Min(1L) Long projectId) {
        return ResponseEntity.ok(new ResponseCustom("Lấy group thành công", HttpStatus.OK.value(), this.groupService.getGroupByProjectId(projectId)));
    }

}
