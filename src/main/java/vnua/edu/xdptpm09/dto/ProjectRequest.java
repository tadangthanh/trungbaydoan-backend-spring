package vnua.edu.xdptpm09.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
public class ProjectRequest implements Serializable {
    private @NotNull(
            message = "Project id là bắt buộc"
    ) List<Long> projectIds;
    private String reason;
}
