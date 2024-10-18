package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "member_roles")
@Entity
public class MemberRole extends BaseEntity{
    private String name;
    @OneToMany(
            mappedBy = "memberRole"
    )
    private List<GroupMember> groupMembers;
}
