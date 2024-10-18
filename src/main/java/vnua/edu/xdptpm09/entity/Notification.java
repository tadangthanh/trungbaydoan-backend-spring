package vnua.edu.xdptpm09.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification extends BaseEntity{
    private String message;
    @Column(
            name = "seen"
    )
    private boolean seen;
    @ManyToOne
    @JoinColumn(
            name = "project_id"
    )
    private Project project;
    @ManyToOne
    @JoinColumn(
            name = "receiver_id"
    )
    private User receiver;
    @ManyToOne
    @JoinColumn(
            name = "comment_id"
    )
    private Comment comment;
}
