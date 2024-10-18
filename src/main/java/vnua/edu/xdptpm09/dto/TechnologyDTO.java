package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class TechnologyDTO extends BaseDTO {
    private @NotNull
    @NotBlank String name;
    private String acronym;
}
