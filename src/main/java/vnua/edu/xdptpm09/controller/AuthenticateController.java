
package vnua.edu.xdptpm09.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vnua.edu.xdptpm09.dto.AuthenticationRequest;
import vnua.edu.xdptpm09.dto.AuthenticationResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.service.impl.AuthenticationServiceImpl;

@RestController
@RequestMapping({"/api/v1/auth"})
@RequiredArgsConstructor
@Validated
public class AuthenticateController {
    private final AuthenticationServiceImpl authenticationServiceImpl;

    @PostMapping({"/login"})
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request) {
        return this.authenticationServiceImpl.authenticate(authenticationRequest, request);
    }

    @PostMapping({"/logout"})
    public ResponseEntity<ResponseCustom> logout(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        String refreshToken = request.getHeader("Refresh-Token");
        this.authenticationServiceImpl.logout(token, refreshToken);
        return ResponseEntity.ok(new ResponseCustom("Đăng xuất thành công", 200, null));
    }

    @PostMapping({"/refresh"})
    public ResponseEntity<ResponseCustom> refresh(@RequestHeader("Refresh-Token") String refreshToken, HttpServletRequest request) {
        AuthenticationResponse authenticationResponse = this.authenticationServiceImpl.refreshToken(refreshToken, request);
        if (authenticationResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            String newAccessToken = authenticationResponse.getToken();
            String newRefreshToken = authenticationResponse.getRefreshToken();
            return ResponseEntity.ok(new ResponseCustom("Làm mới token thành công", 200, new AuthenticationResponse(newAccessToken, newRefreshToken)));
        }
    }

    @PostMapping({"/verify"})
    public ResponseEntity<ResponseCustom> verify(@RequestParam String code) {
        return this.authenticationServiceImpl.verifyAccount(code) ? ResponseEntity.ok(new ResponseCustom("Xác thực thành công", 200, null)) : ResponseEntity.badRequest().body(new ResponseCustom("Xác thực thất bại", 400, null));
    }

    @PostMapping({"/resend-code"})
    public ResponseEntity<ResponseCustom> resendCode(@RequestParam @Email(
            message = "định dạng email k đúng "
    ) String email) {
        this.authenticationServiceImpl.resendCode(email);
        return ResponseEntity.ok(new ResponseCustom("Gửi mã xác thực thành công", 200, null));
    }

    @PostMapping({"/send-code-change-password"})
    public ResponseEntity<ResponseCustom> sendCodeChangePassword(@RequestParam @Email(
            message = "định dạng email k đúng "
    ) String email) {
        this.authenticationServiceImpl.sendCodeChangePassword(email);
        return ResponseEntity.ok(new ResponseCustom("Gửi mã xác thực thành công", 200, null));
    }

    @PostMapping({"/verify-token"})
    public ResponseEntity<ResponseCustom> verifyToken(@RequestHeader("Access-Token") String token) {
        return this.authenticationServiceImpl.verifyToken(token) ? ResponseEntity.ok(new ResponseCustom("Token hợp lệ", 200, null)) : ResponseEntity.badRequest().body(new ResponseCustom("Token không hợp lệ", 400, null));
    }

    @PostMapping({"/verify-admin"})
    public ResponseEntity<ResponseCustom> verifyTokenAdmin(@RequestHeader("Access-Token") String token) {
        System.out.println("token" + token);
        return this.authenticationServiceImpl.verifyTokenAdmin(token) ? ResponseEntity.ok(new ResponseCustom("Token hợp lệ", 200, null)) : ResponseEntity.badRequest().body(new ResponseCustom("Token không hợp lệ", 400, null));
    }

}
