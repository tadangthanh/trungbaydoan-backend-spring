//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.*;
import vnua.edu.xdptpm09.service.IProjectService;
import vnua.edu.xdptpm09.validation.AllowedValues;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/projects"})
@Validated
@RequiredArgsConstructor
public class ProjectRest {
    private final IProjectService projectService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<ResponseCustom> create(@RequestPart("createProjectDTO") @Valid CreateProjectDTO createProjectDTO, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        ProjectDTO projectDTO = this.projectService.createProject(createProjectDTO, files).orElse(null);
        return projectDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Tạo project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Tạo project thành công", HttpStatus.CREATED.value(), projectDTO));
    }

    @PatchMapping({"/{projectId}"})
    public ResponseEntity<ResponseCustom> updateProject(@PathVariable @Min(1L) Long projectId, @RequestPart("projectDTO") @Valid ProjectDTO projectDTO, @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        projectDTO.setId(projectId);
        ProjectDTO project = this.projectService.updateProject(projectDTO, files).orElse(null);
        return project == null ? ResponseEntity.badRequest().body(new ResponseCustom("Cập nhật project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Cập nhật project thành công", HttpStatus.OK.value(), project));
    }

    @PutMapping({"/{projectId}"})
    public ResponseEntity<ResponseCustom> update(@PathVariable @Min(1L) Long projectId, @RequestParam List<MultipartFile> files, @RequestParam("data") @NotNull String jsonData) throws JsonProcessingException {
        ProjectDTO projectDTO = this.objectMapper.readValue(jsonData, ProjectDTO.class);
        projectDTO.setId(projectId);
        ProjectDTO project = this.projectService.updateDocumentProject(projectDTO, files).orElse(null);
        return project == null ? ResponseEntity.badRequest().body(new ResponseCustom("Cập nhật project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Cập nhật project thành công", HttpStatus.OK.value(), project));
    }

    @PostMapping({"/{projectId}/documents"})
    public ResponseEntity<ResponseCustom> addDocument(@PathVariable @Min(1L) Long projectId, @RequestParam List<MultipartFile> files) {
        ProjectDTO project = this.projectService.addDocumentProject(projectId, files).orElse(null);
        return project == null ? ResponseEntity.badRequest().body(new ResponseCustom("Thêm document thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Thêm document thành công", HttpStatus.OK.value(), project));
    }

    @DeleteMapping({"/{projectId}/documents/{documentId}"})
    public ResponseEntity<ResponseCustom> removeDocument(@PathVariable @Min(1L) Long projectId, @PathVariable @Min(1L) Long documentId) {
        ProjectDTO project = this.projectService.removeDocumentProject(projectId, documentId).orElse(null);
        return project == null ? ResponseEntity.badRequest().body(new ResponseCustom("Xóa document thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Xóa document thành công", HttpStatus.OK.value(), project));
    }

    @PostMapping({"/approve"})
    public ResponseEntity<ResponseCustom> approveProject(@RequestBody @Valid ProjectRequest projectRequest) {
        int result = this.projectService.approveProjects(projectRequest);
        return result == 0 ? ResponseEntity.badRequest().body(new ResponseCustom("Duyệt project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Duyệt project thành công", HttpStatus.OK.value(), null));
    }

    @PostMapping({"/reject"})
    public ResponseEntity<ResponseCustom> rejectProject(@RequestBody @Valid ProjectRequest projectRejectRequest) {
        int result = this.projectService.rejectProjects(projectRejectRequest);
        return result == 0 ? ResponseEntity.badRequest().body(new ResponseCustom("Từ chối project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Từ chối project thành công", HttpStatus.OK.value(), null));
    }

    @DeleteMapping({"/{projectId}"})
    public ResponseEntity<ResponseCustom> deleteProject(@PathVariable @Min(1L) Long projectId) {
        this.projectService.deleteProject(projectId);
        return ResponseEntity.ok(new ResponseCustom("Xóa project thành công", HttpStatus.OK.value(), null));
    }

    @PostMapping({"/inactivate"})
    public ResponseEntity<ResponseCustom> inactivateProjects(@RequestBody ProjectRequest projectRequest) {
        int result = this.projectService.inactivateProjects(projectRequest);
        return result == 0 ? ResponseEntity.badRequest().body(new ResponseCustom("Ngưng hoạt động project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Ẩn đồ án thành công", HttpStatus.OK.value(), null));
    }

    @PostMapping({"/activate"})
    public ResponseEntity<ResponseCustom> activateProjects(@RequestBody ProjectRequest projectRequest) {
        int result = this.projectService.activateProjects(projectRequest);
        return result == 0 ? ResponseEntity.badRequest().body(new ResponseCustom("Kích hoạt đồ án thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Kích hoạt đồ án thành công", HttpStatus.OK.value(), null));
    }

    @GetMapping
    public ResponseEntity<ResponseCustom> getAllProjectActive(@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "name") String searchField, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "DESC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.getAllProjectActiveAndApproved(pageRequest, searchField, search);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @GetMapping({"/pending/{email}"})
    public ResponseEntity<ResponseCustom> getAllProjectPending(@PathVariable String email, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.getAllProjectPendingByMemberEmail(email, pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @GetMapping({"/search"})
    public ResponseEntity<ResponseCustom> searchProject(@RequestParam String search, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.searchProject(search, pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @PostMapping({"/{projectId}/submit"})
    public ResponseEntity<ResponseCustom> submitProject(@PathVariable @Min(1L) Long projectId) {
        ProjectDTO project = this.projectService.submitProject(projectId).orElse(null);
        return project == null ? ResponseEntity.badRequest().body(new ResponseCustom("Nộp project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Nộp project thành công", HttpStatus.OK.value(), project));
    }

    @GetMapping({"/{projectId}"})
    public ResponseEntity<ResponseCustom> getProjectById(@PathVariable @Min(1L) Long projectId) {
        ProjectDTO project = this.projectService.getProjectById(projectId).orElse(null);
        return project == null ? ResponseEntity.badRequest().body(new ResponseCustom("Không tìm thấy project", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Thành công", HttpStatus.OK.value(), project));
    }

    @GetMapping({"/user/{email}"})
    public ResponseEntity<ResponseCustom> getAllProjectByUser(@PathVariable String email, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.getAllProjectByUser(email, pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @GetMapping({"/{projectId}/mentors"})
    public ResponseEntity<ResponseCustom> getMentorsByProjectId(@PathVariable @Min(1L) Long projectId) {
        List<UserDTO> mentors = this.projectService.getMentorsByProjectId(projectId);
        return mentors.isEmpty() ? ResponseEntity.ok().body(new ResponseCustom("không có mentor nào", HttpStatus.OK.value(), mentors)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), mentors));
    }

    @GetMapping({"/{projectId}/members"})
    public ResponseEntity<ResponseCustom> getMembersByProjectId(@PathVariable @Min(1L) Long projectId) {
        List<UserDTO> members = this.projectService.getMembersByProjectId(projectId);
        return members.isEmpty() ? ResponseEntity.ok().body(new ResponseCustom("không có thành viên nào", HttpStatus.OK.value(), members)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), members));
    }

    @GetMapping({"/mentor/{email}"})
    public ResponseEntity<ResponseCustom> getAllProjectByMentor(@PathVariable String email, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.getAllProjectByMentor(email, pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @DeleteMapping
    public ResponseEntity<ResponseCustom> deleteProjectByIds(@RequestBody @Valid ProjectRequest projectRequest) {
        int result = this.projectService.deleteProjectByIds(projectRequest);
        return result == 0 ? ResponseEntity.badRequest().body(new ResponseCustom("Xóa project thất bại", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Xóa project thành công", HttpStatus.OK.value(), null));
    }

    @GetMapping({"/category/{categoryId}"})
    public ResponseEntity<ResponseCustom> getAllProjectByCategoryId(@PathVariable @Min(1L) Long categoryId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction, @RequestParam(defaultValue = "") String searchField, @RequestParam(defaultValue = "") String search) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.projectService.getAllProjectByCategoryId(searchField, search, categoryId, pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có dự án nào", HttpStatus.OK.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

}
