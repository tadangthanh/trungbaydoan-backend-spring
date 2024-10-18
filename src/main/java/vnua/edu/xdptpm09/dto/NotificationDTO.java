package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Getter
@Setter
public class NotificationDTO extends BaseDTO{
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String message;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private boolean seen;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long projectId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long commentId;
}
