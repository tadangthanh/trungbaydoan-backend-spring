package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
@Setter
@Getter
public class GroupMemberDTO extends BaseDTO{
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String role;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long userId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String memberName;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String email;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String className;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String department;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String major;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private int academicYear;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long groupId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String avatarUrl;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long projectId;
}
