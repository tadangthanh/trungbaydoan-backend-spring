package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
@Getter
@Setter
public class ResetPassword  implements Serializable {
    private @NotNull(
            message = "Email không được để trống"
    ) @NotBlank(
            message = "Email không được để trống"
    ) String email;
    private @NotNull(
            message = "Code không được để trống"
    ) @NotBlank(
            message = "Code không được để trống"
    ) String verifyCode;

}
