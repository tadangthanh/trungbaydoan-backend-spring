package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class BaseDTO implements Serializable {
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private Long id;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private LocalDateTime createdDate;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private String createdBy;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private LocalDateTime lastModifiedDate;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private String lastModifiedBy;
}
