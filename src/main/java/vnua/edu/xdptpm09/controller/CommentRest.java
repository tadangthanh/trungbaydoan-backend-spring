//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.CommentDTO;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.ICommentService;
import vnua.edu.xdptpm09.validation.AllowedValues;
import vnua.edu.xdptpm09.validation.Create;
import vnua.edu.xdptpm09.validation.Update;

@RestController
@RequestMapping({"/api/v1/comments"})
@Validated
@RequiredArgsConstructor
public class CommentRest {
    private final ICommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseCustom> save(@RequestBody @Validated({Create.class}) CommentDTO commentDTO) {
        commentDTO = this.commentService.save(commentDTO).orElse(null);
        return commentDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Lưu thất bại", 400, null)) : ResponseEntity.ok(new ResponseCustom("Lưu thành công", 201, commentDTO));
    }

    @PatchMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> update(@PathVariable @Min(1L) Long id, @RequestBody @Validated({Update.class}) CommentDTO commentDTO) {
        commentDTO.setId(id);
        commentDTO = this.commentService.update(commentDTO).orElse(null);
        return commentDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Cập nhật thất bại", 400, null)) : ResponseEntity.ok(new ResponseCustom("Cập nhật thành công", 200, commentDTO));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> delete(@PathVariable @Min(1L) Long id) {
        this.commentService.delete(id);
        return ResponseEntity.ok(new ResponseCustom("Xóa thành công", 204, null));
    }

    @GetMapping({"/project/{projectId}"})
    public ResponseEntity<ResponseCustom> getAllByProjectId(@PathVariable @Min(1L) Long projectId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageComment = this.commentService.getAllByProjectId(projectId, pageRequest);
        return pageComment.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("empty", HttpStatus.NO_CONTENT.value(), null)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("success", HttpStatus.OK.value(), pageComment));
    }

    @GetMapping({"/parent-comment/{parentCommentId}"})
    public ResponseEntity<ResponseCustom> getAllByParentCommentId(@PathVariable @Min(1L) Long parentCommentId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageComment = this.commentService.getAllByParentCommentId(parentCommentId, pageRequest);
        return pageComment.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("empty", HttpStatus.NO_CONTENT.value(), null)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("successNo", HttpStatus.OK.value(), pageComment));
    }

    @GetMapping({"/{commentId}/project/{projectId}"})
    public ResponseEntity<ResponseCustom> getByCommentIdAndProjectId(@PathVariable @Min(1L) Long commentId, @PathVariable @Min(1L) Long projectId) {
        CommentDTO commentDTO = this.commentService.getCommentByCommentIdAndProjectId(commentId, projectId).orElse(null);
        return commentDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Không tìm thấy", 400, null)) : ResponseEntity.ok(new ResponseCustom("Thành công", 200, commentDTO));
    }

}
