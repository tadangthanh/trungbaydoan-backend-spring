package vnua.edu.xdptpm09.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Data
@Entity
@Table(name = "groups")
public class Group extends BaseEntity{
    @OneToOne(
            mappedBy = "group",
            cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    private Project project;
    @OneToMany(
            mappedBy = "group",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY
    )
    private List<GroupMember> members;

    public void removeMember(GroupMember member) {
        if (this.members != null) {
            this.members.remove(member);
            member.setGroup((Group)null);
        }

    }

    public void addMember(GroupMember member) {
        if (this.members == null) {
            this.members = new ArrayList();
        }

        this.members.add(member);
        member.setGroup(this);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getId()});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Group group = (Group)obj;
            return Objects.equals(this.getId(), group.getId());
        } else {
            return false;
        }
    }
}
