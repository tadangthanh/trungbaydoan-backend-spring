package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "technologies")
@AllArgsConstructor
@NoArgsConstructor
public class Technology extends BaseEntity {
    private String name;
    private String acronym;
    @ManyToMany(
            mappedBy = "technologies"
    )
    private List<Project> projects;

    public Technology(String name, String acronym) {
        this.name = name;
        this.acronym = acronym;
    }
}
