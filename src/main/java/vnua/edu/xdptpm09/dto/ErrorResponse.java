package vnua.edu.xdptpm09.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

import java.util.Date;

@Builder
public class ErrorResponse {
    private String message;
    private int status;
    @JsonInclude(Include.NON_NULL)
    private String error;
    private String path;
    private Date timestamp;
}
