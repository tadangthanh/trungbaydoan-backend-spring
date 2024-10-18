package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "roles")
@Entity
public class Role extends BaseEntity {
    private String name;
}
