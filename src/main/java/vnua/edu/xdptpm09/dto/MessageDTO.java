package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
@Getter
@Setter
public class MessageDTO extends BaseDTO{
    private @NotNull @NotEmpty @NotBlank String content;
    private @NotNull String receiverEmail;
    private String authorEmail;
}
