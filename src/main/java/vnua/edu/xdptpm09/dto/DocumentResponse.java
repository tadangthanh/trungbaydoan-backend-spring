package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class DocumentResponse implements Serializable {
    private String url;
    private Long id;
}
