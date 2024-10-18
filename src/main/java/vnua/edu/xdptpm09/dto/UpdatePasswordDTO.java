package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import vnua.edu.xdptpm09.validation.Update;
@Getter
@Setter
public class UpdatePasswordDTO implements Serializable {
    private @NotBlank(
            message = "Current password is required",
            groups = {Update.class}
    ) String currentPassword;
    private @NotBlank(
            message = "Current password is required",
            groups = {Update.class}
    ) @Size(
            min = 3,
            message = "Password must be at least 3 characters",
            groups = {Update.class}
    ) String newPassword;
    private @NotBlank(
            message = "Confirm password is required",
            groups = {Update.class}
    ) @Size(
            min = 3,
            message = "Password must be at least 3 characters",
            groups = {Update.class}
    ) String confirmPassword;
    private String code;
}
