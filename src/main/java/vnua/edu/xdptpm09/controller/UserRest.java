//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.*;
import vnua.edu.xdptpm09.service.IUserService;
import vnua.edu.xdptpm09.validation.AllowedValues;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/users"})
@Validated
@RequiredArgsConstructor
public class UserRest {
    private final IUserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping({"/register"})
    public ResponseEntity<ResponseCustom> register(@RequestBody UserRegister userRegister) {
        UserDTO userDTO = this.userService.register(userRegister).orElse(null);
        return userDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("thất bại", 400, null)) : ResponseEntity.ok(new ResponseCustom("Mã xác nhận đã gửi vào email của bạn", 201, userDTO));
    }

    @PostMapping({"/{id}/change-password"})
    public ResponseEntity<ResponseCustom> changePassword(@PathVariable @Min(1L) Long id, @Validated @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return this.userService.changePassword(id, updatePasswordDTO) ? ResponseEntity.ok(new ResponseCustom("Đổi mật khẩu thành công", 204, null)) : ResponseEntity.badRequest().body(new ResponseCustom("Đổi mật khẩu thất bại", 400, null));
    }

    @GetMapping
    public ResponseEntity<ResponseCustom> getAllUser(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort, @AllowedValues(anyOf = {"ASC", "DESC"}) @RequestParam(defaultValue = "ASC") String direction) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Direction.fromString(direction),sort));
        PageResponse<?> pageUser = this.userService.getAllUser(pageRequest);
        return pageUser.getTotalItems() == 0 ? ResponseEntity.ok().body(new ResponseCustom("empty", HttpStatus.OK.value(), pageUser)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("success", HttpStatus.OK.value(), pageUser));
    }

    @GetMapping({"/email/{email}"})
    public ResponseEntity<ResponseCustom> findByEmail(@PathVariable @NotNull @NotBlank String email) {
        UserDTO userDTO = this.userService.findByEmail(email).orElse(null);
        return userDTO == null ? ResponseEntity.ok().body(new ResponseCustom("không tìm thấy email", HttpStatus.OK.value(), null)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("success", HttpStatus.OK.value(), userDTO));
    }

    @PostMapping({"/forgot-password"})
    public ResponseEntity<ResponseCustom> forgotPassword(@RequestParam String email) {
        this.userService.forgotPassword(email);
        return ResponseEntity.badRequest().body(new ResponseCustom("Mã xác nhận đã gửi vào email của bạn", 200, null));
    }

    @PostMapping({"/reset-password"})
    public ResponseEntity<ResponseCustom> resetPassword(@Validated @RequestBody ResetPassword resetPassword) {
        this.userService.resetPassword(resetPassword);
        return ResponseEntity.badRequest().body(new ResponseCustom("Đổi mật khẩu thành công, mật khẩu mới đã gửi vào email của bạn", 200, null));
    }

    @PostMapping({"/find-teacher-email"})
    public ResponseEntity<ResponseCustom> findAllTeacherByEmail(@RequestParam String email, @RequestBody(required = false) List<String> emailsIgnore) {
        List<UserDTO> emailFound = this.userService.findTeacherSuggestion(email, emailsIgnore);
        return emailFound != null && !emailFound.isEmpty() ? ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("success", HttpStatus.OK.value(), emailFound)) : ResponseEntity.ok().body(new ResponseCustom("không tìm thấy email", HttpStatus.OK.value(), null));
    }

    @PostMapping({"/find-student-email"})
    public ResponseEntity<ResponseCustom> findAllStudentByEmail(@RequestParam String email, @RequestBody(required = false) List<String> emailsIgnore) {
        List<UserDTO> emailFound = this.userService.findStudentSuggestion(email, emailsIgnore);
        return emailFound == null ? ResponseEntity.ok().body(new ResponseCustom("không tìm thấy email", HttpStatus.OK.value(), null)) : ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("success", HttpStatus.OK.value(), emailFound));
    }

    @PostMapping({"/avatar/{email}"})
    public ResponseEntity<ResponseCustom> changeAvatar(@PathVariable String email, @NotNull MultipartFile file) {
        UserDTO userDTO = this.userService.changeAvatar(email, file).orElse(null);
        return userDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Thay đổi ảnh đại diện thất bại", 400, null)) : ResponseEntity.ok(new ResponseCustom("Thay đổi ảnh đại diện thành công", 200, userDTO));
    }

    @GetMapping({"/avatar/view/{id}"})
    public ResponseEntity<InputStreamResource> viewAvatar(@PathVariable Long id) {
        InputStreamResource resource = this.userService.downloadById(id);
        return resource == null ? ResponseEntity.notFound().build() : (ResponseEntity.ok().header("Content-Disposition","inline; filename=" + URLEncoder.encode("avatar", StandardCharsets.UTF_8))).contentType(MediaType.IMAGE_JPEG).body(resource);
    }

    @PostMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> update(@PathVariable Long id, @RequestBody UserUpdateDTO userDTO) {
        UserDTO user = this.userService.updateProfile(id, userDTO).orElse(null);
        return user == null ? ResponseEntity.badRequest().body(new ResponseCustom("Cập nhật thất bại", 400, null)) : ResponseEntity.ok(new ResponseCustom("Cập nhật thành công", 200, user));
    }

    @PostMapping({"/send-private-message/{id}"})
    public void sendPrivateMessage(@PathVariable final String id, @RequestParam String message) {
        this.messagingTemplate.convertAndSendToUser(id, "/topic/private-messages", message);
    }

    @PostMapping({"/active/{id}"})
    public ResponseEntity<ResponseCustom> activeUser(@PathVariable Long id) {
        this.userService.activeUserById(id);
        return ResponseEntity.ok(new ResponseCustom("Kích hoạt tài khoản thành công", 200, null));
    }

    @PostMapping({"/inactive/{id}"})
    public ResponseEntity<ResponseCustom> inactiveUser(@PathVariable Long id) {
        this.userService.inActiveUserById(id);
        return ResponseEntity.ok(new ResponseCustom("Vô hiệu hóa tài khoản thành công", 200, null));
    }

    @PostMapping({"/emails"})
    public ResponseEntity<ResponseCustom> getAllByEmails(@RequestBody List<String> emails) {
        List<UserDTO> userDTOS = this.userService.getAllUserByEmails(emails);
        return userDTOS != null && !userDTOS.isEmpty() ? ResponseEntity.status(HttpStatus.OK).body(new ResponseCustom("success", HttpStatus.OK.value(), userDTOS)) : ResponseEntity.ok().body(new ResponseCustom("không tìm thấy email", HttpStatus.OK.value(), null));
    }


}
