package vnua.edu.xdptpm09.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVNUADTO extends BaseDTO{
    private String fullName;
    private String className;
    private String major;
    private String department;
    private int academicYear;
}
