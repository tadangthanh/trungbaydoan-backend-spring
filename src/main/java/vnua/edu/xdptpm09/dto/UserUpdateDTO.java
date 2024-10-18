package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO extends BaseDTO{
    private String githubUrl;
    private String facebookUrl;
}
