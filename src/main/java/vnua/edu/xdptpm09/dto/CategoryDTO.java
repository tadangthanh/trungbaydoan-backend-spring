package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
@Getter
@Setter
public class CategoryDTO extends BaseDTO{
    private @NotBlank(
            message = "Name is required"
    ) @Size(
            min = 3,
            max = 50,
            message = "Name must be between 3 and 50 characters"
    ) @NotNull(
            message = "Name is required"
    ) String name;
}
