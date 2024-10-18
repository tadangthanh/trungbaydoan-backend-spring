package vnua.edu.xdptpm09.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "academy_years")
@Data
public class AcademyYear extends BaseEntity {
    private int number;
    @OneToMany(
            mappedBy = "academyYear",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            fetch = FetchType.LAZY
    )
    private List<Project> projects;
}
