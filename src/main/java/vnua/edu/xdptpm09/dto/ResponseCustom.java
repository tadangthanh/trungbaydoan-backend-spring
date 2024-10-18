package vnua.edu.xdptpm09.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class ResponseCustom {
    private String message;
    private int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

}
