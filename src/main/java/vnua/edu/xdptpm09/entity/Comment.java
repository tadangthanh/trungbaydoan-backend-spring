package vnua.edu.xdptpm09.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Entity
@Table(name = "comments")
@Data
public class Comment extends BaseEntity {
    private String content;
    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private User author;
    @ManyToOne
    @JoinColumn(
            name = "project_id"
    )
    private Project project;
    @OneToMany(
            mappedBy = "parentComment",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private List<Comment> replies;
    @ManyToOne
    @JoinColumn(
            name = "parent_id"
    )
    private Comment parentComment;
}
