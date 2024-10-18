package vnua.edu.xdptpm09.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class CreateProjectDTO extends BaseDTO{
    private @NotNull(
            message = "Tên không được để trống"
    ) @NotBlank(
            message = "Tên không được để trống"
    ) String name;
    private @NotNull(
            message = "Mô tả không được để trống"
    ) @NotBlank(
            message = "Mô tả không được để trống"
    ) String description;
    private @NotNull(
            message = "Ngày bắt đầu không được để trống"
    ) LocalDate startDate;
    private @NotNull(
            message = "Ngày kết thúc không được để trống"
    ) LocalDate endDate;
    private @NotNull(
            message = "Ngày nộp không được để trống"
    ) LocalDate submissionDate;
    private @NotNull(
            message = "ID danh mục không được để trống"
    ) Long categoryId;
    private @NotNull(
            message = "Giáo viên hướng dẫn không được để trống"
    ) @NotEmpty(
            message = "Giáo viên hướng dẫn không được để trống"
    ) List<Long> mentorIds;
    private @NotNull(
            message = "Tóm tắt không được để trống"
    ) @NotBlank(
            message = "Tóm tắt không được để trống"
    ) String summary;
    private List<Long> memberIds;
    private @NotNull(
            message = "tài liêu không được để trống"
    ) List<Long> documentIds;
    private @NotNull(
            message = "Công nghệ không được để trống"
    ) List<Long> idsTechnology;
}
