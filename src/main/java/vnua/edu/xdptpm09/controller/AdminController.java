//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.IProjectService;
import vnua.edu.xdptpm09.service.IUserService;
import vnua.edu.xdptpm09.validation.AllowedValues;

@RestController
@RequestMapping({"/api/v1/admin"})
@RequiredArgsConstructor
public class AdminController {
    private final IProjectService projectService;
    private final IUserService userService;

    @GetMapping({"/projects"})
    public ResponseEntity<ResponseCustom> getAllProjectByAdmin(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "5") int size,
                                                               @RequestParam(defaultValue = "id") String sort,
                                                               @AllowedValues(anyOf = {"ASC", "DESC"})
                                                                   @RequestParam(defaultValue = "ASC") String direction,
                                                               @RequestParam(defaultValue = "") String searchField,
                                                               @RequestParam(defaultValue = "") String search) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.getAllProjectByAdmin(pageRequest, search, searchField);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @GetMapping({"/users"})
    public ResponseEntity<ResponseCustom> getAllUserByAdmin(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction, @RequestParam(defaultValue = "") String searchField, @RequestParam(defaultValue = "") String search) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.userService.getAllUserByAdmin(pageRequest, search, searchField);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }


}
