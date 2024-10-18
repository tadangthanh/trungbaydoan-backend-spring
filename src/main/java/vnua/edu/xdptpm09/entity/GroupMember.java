package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "group_members")
@Entity
@Data
public class GroupMember extends BaseEntity{
    @ManyToOne
    @JoinColumn(
            name = "role_id"
    )
    private MemberRole memberRole;
    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private User user;
    @ManyToOne
    @JoinColumn(
            name = "group_id"
    )
    private Group group;

}
