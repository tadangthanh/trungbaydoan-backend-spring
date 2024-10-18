package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vnua.edu.xdptpm09.validation.Create;
import vnua.edu.xdptpm09.validation.Update;
@Getter
@Setter
public class CommentDTO extends BaseDTO{
    private @NotNull(
            message = "Nội dung không được để trống",
            groups = {Create.class}
    ) @NotBlank(
            message = "Nội dung không được để trống",
            groups = {Create.class, Update.class}
    ) String content;
    private @NotNull(
            message = "Id dự án không được để trống",
            groups = {Create.class}
    ) Long projectId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String authorName;
    @JsonInclude(Include.NON_NULL)
    private Long parentCommentId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private Long authorAvatarId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private int totalReply;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String authorEmail;
    private String receiverEmail;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String authorAvatarUrl;
}
