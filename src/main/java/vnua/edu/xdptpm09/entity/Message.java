package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "messages")
@Entity
public class Message extends BaseEntity{
    private String content;
    @ManyToOne
    @JoinColumn(
            name = "receiver_id"
    )
    private User receiver;
    @ManyToOne
    @JoinColumn(
            name = "author_id"
    )
    private User author;
}
