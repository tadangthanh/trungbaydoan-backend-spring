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
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.INotificationService;
import vnua.edu.xdptpm09.validation.AllowedValues;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/notifications"})
public class NotificationRest {
    private final INotificationService notificationService;

    @GetMapping({"/seen/{id}"})
    public ResponseEntity<ResponseCustom> seen(@PathVariable Long id) {
        this.notificationService.seenNotification(id);
        return ResponseEntity.ok().body(new ResponseCustom("success", HttpStatus.NO_CONTENT.value(), null));
    }

    @PostMapping({"/delete/{id}"})
    public ResponseEntity<ResponseCustom> delete(@PathVariable Long id) {
        this.notificationService.deleteNotification(id);
        return ResponseEntity.ok().body(new ResponseCustom("success", HttpStatus.NO_CONTENT.value(), null));
    }

    @GetMapping({"/user/{id}"})
    public ResponseEntity<ResponseCustom> getAllByUserId(@PathVariable Long id, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "DESC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction), sort));
        PageResponse<?> pageProject = this.notificationService.getAllByUserId(id, pageRequest);
        return pageProject.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("không có thông báo nào", HttpStatus.NO_CONTENT.value(), pageProject)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("thành công", HttpStatus.OK.value(), pageProject));
    }

    @GetMapping({"/count-not-seen"})
    public ResponseEntity<ResponseCustom> countBySeen() {
        return ResponseEntity.ok().body(new ResponseCustom("success", HttpStatus.OK.value(), this.notificationService.countByNotSeen()));
    }

    @GetMapping({"/seen-all/{email}"})
    public ResponseEntity<ResponseCustom> seenAll(@PathVariable String email) {
        this.notificationService.seenAll(email);
        return ResponseEntity.ok().body(new ResponseCustom("success", HttpStatus.NO_CONTENT.value(), null));
    }
}
