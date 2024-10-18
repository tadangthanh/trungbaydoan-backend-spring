package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import vnua.edu.xdptpm09.validation.Create;
import vnua.edu.xdptpm09.validation.Update;
@Getter
@Setter
public class UserDTO extends BaseDTO{
    private @Email(
            message = "Email is invalid",
            groups = {Create.class, Update.class}
    ) @NotNull(
            message = "Email is required",
            groups = {Create.class, Update.class}
    ) String email;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private int academicYear;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private String department;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private String fullName;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private String major;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String role;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private int totalsProject;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long avatarId;
    @JsonInclude(Include.NON_NULL)
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String avatarUrl;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String className;
    @JsonInclude(Include.NON_NULL)
    private String facebookUrl;
    @JsonInclude(Include.NON_NULL)
    private String githubUrl;
    private boolean status;
}
