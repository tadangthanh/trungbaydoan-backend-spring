package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import vnua.edu.xdptpm09.validation.Create;

@Getter
@Setter
public class UserRegister implements Serializable {
    private @Email(
            message = "Email is invalid"
    ) @NotNull(
            message = "Email is required"
    ) String email;
    private @NotBlank(
            message = "Password is required"
    ) @NotNull(
            message = "Password is required",
            groups = {Create.class}
    ) String password;
    private @NotBlank(
            message = "Password Confirm is required"
    ) @NotNull(
            message = "Password Confirm is required"
    ) String passwordConfirm;

}
