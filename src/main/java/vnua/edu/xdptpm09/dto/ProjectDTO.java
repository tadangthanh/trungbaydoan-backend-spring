package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import vnua.edu.xdptpm09.entity.ProjectStatus;
import vnua.edu.xdptpm09.validation.Create;
import vnua.edu.xdptpm09.validation.Update;
@Getter
@Setter
public class ProjectDTO extends BaseDTO{
    private @NotNull(
            message = "tên project không được để trống",
            groups = {Update.class}
    ) String name;
    private @NotNull(
            message = "mô tả không được để trống",
            groups = {Update.class}
    ) String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private ProjectStatus projectStatus;
    @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy/MM/dd"
    )
    private LocalDate submissionDate;
    private String summary;
    private Long categoryId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    private String categoryName;
    @JsonInclude(Include.NON_NULL)
    private List<Long> documentIds;
    private @NotNull(
            message = "groupId không được để trống",
            groups = {Create.class}
    ) Long groupId;
    @JsonProperty(
            access = Access.READ_ONLY
    )
    @JsonInclude(Include.NON_NULL)
    private Long approverId;
    @JsonInclude(Include.NON_NULL)
    private String approverName;
    private int academicYear;
    @JsonInclude(Include.NON_NULL)
    private List<Long> mentorIds;
    @JsonInclude(Include.NON_NULL)
    private List<String> mentorNames;
    @JsonInclude(Include.NON_NULL)
    private List<String> memberNames;
    private List<Long> idsTechnology;

}
