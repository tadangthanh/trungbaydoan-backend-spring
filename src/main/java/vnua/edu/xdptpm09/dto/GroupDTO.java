package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@Getter
@Setter
public class GroupDTO extends BaseDTO{
    @JsonInclude(Include.NON_NULL)
    private List<Long> memberIds;
    private List<String> memberNames;
    @JsonInclude(Include.NON_NULL)
    private Long projectId;

}
