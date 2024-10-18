

package vnua.edu.xdptpm09.service;

import jakarta.servlet.http.HttpServletRequest;
import vnua.edu.xdptpm09.dto.AuthenticationRequest;
import vnua.edu.xdptpm09.dto.AuthenticationResponse;

public interface IAuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest request);

    AuthenticationResponse refreshToken(String refreshToken, HttpServletRequest request);

    boolean verifyAccount(String code);

    void resendCode(String email);

    void logout(String token, String refreshToken);

    boolean verifyToken(String token);

    void sendCodeChangePassword(String email);

    boolean verifyTokenAdmin(String token);
}
