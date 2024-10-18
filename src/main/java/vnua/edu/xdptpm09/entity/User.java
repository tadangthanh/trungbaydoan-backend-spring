package vnua.edu.xdptpm09.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import vnua.edu.xdptpm09.service.Observer;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "users")
@Entity
public class User extends BaseEntity implements Observer {
    @Column(
            unique = true
    )
    private @Email String email;
    private String department;
    private String major;
    private String className;
    private String fullName;
    private int academicYear;
    private String password;
    private String facebookUrl;
    private String githubUrl;
    private String verifyCode;
    @OneToOne
    private Avatar avatar;
    private boolean status;
    @ManyToOne
    @JoinColumn(
            name = "role_id"
    )
    private Role role;
    @OneToMany(
            mappedBy = "receiver"
    )
    private List<Notification> notifications;
    @OneToMany(
            mappedBy = "approver"
    )
    private List<Project> approvedProjects;
    @ManyToMany(
            mappedBy = "mentors"
    )
    private List<Project> projects;
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    private List<GroupMember> groupMemberships;

    public void update(Notification notification) {
        if (this.notifications == null) {
            this.notifications = new ArrayList();
        }

        this.notifications.add(notification);
    }
}
