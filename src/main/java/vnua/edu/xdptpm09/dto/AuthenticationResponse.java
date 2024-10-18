package vnua.edu.xdptpm09.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
public class AuthenticationResponse implements Serializable {
    private String token;
    private String refreshToken;
}
