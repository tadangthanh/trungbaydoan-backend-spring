package vnua.edu.xdptpm09.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
public class Project extends BaseEntity{
    private String name;
    @Column(
            columnDefinition = "TEXT"
    )
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;
    private LocalDate submissionDate;
    private String summary;
    @ManyToOne
    @JoinColumn(
            name = "category_id"
    )
    private Category category;
    @OneToMany(
            mappedBy = "project",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    private List<Document> documents;
    @OneToOne(
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "group_id"
    )
    private Group group;
    @OneToMany(
            mappedBy = "project",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    private List<Notification> notifications;
    @ManyToOne
    @JoinColumn(
            name = "academy_year_id"
    )
    private AcademyYear academyYear;
    @ManyToOne
    @JoinColumn(
            name = "approver_id"
    )
    private User approver;
    @ManyToMany
    @JoinTable(
            name = "project_mentor",
            joinColumns = {@JoinColumn(
                    name = "project_id"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "mentor_id"
            )}
    )
    private List<User> mentors;
    @ManyToMany
    @JoinTable(
            name = "project_technology",
            joinColumns = {@JoinColumn(
                    name = "project_id"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "technology_id"
            )}
    )
    private List<Technology> technologies;
}
