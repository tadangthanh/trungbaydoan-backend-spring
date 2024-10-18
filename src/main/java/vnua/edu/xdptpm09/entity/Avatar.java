package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "avatars")
@Data
public class Avatar extends BaseEntity {
    private String name;
    private String blobName;
    private String url;
}
