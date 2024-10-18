package vnua.edu.xdptpm09.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter

public class DocumentRequest implements Serializable {
    private @NotNull List<Long> ids;
}
