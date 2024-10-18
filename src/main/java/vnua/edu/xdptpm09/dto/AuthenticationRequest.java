package vnua.edu.xdptpm09.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthenticationRequest implements Serializable {
    private String email;
    private String password;
}
