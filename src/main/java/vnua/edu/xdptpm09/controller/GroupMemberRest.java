//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import jakarta.validation.constraints.Min;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.IGroupMemberService;

@RestController
@RequestMapping({"/api/v1/members"})
@RequiredArgsConstructor
@Validated
public class GroupMemberRest {
    private final IGroupMemberService groupMemberService;

    @GetMapping({"/project/{projectId}"})
    public ResponseEntity<ResponseCustom> getMembersByProjectId(@PathVariable @Min(1L) Long projectId) {
        return ResponseEntity.ok(new ResponseCustom("Lấy danh sách thành viên thành công", HttpStatus.OK.value(), this.groupMemberService.getMembersByProjectId(projectId)));
    }

    @PostMapping({"/projects/members"})
    public ResponseEntity<ResponseCustom> getMembersByProjectId2(@RequestBody List<Long> projectIds) {
        return ResponseEntity.ok(new ResponseCustom("Lấy danh sách thành viên thành công", HttpStatus.OK.value(), this.groupMemberService.getAllMembersByProjectIds(projectIds)));
    }

}
