package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import vnua.edu.xdptpm09.entity.DocumentType;
@Getter
@Setter
public class DocumentDTO extends BaseDTO{
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String name;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String size;
    private @NotNull(
            message = "Project ID is required"
    ) Long projectId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    private String mimeType;
    private String url;
}
