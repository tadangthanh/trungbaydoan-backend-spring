package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Data
@Table(name = "documents")
@Entity
public class Document extends BaseEntity{
    private String name;
    private String size;
    private String blobName;
    @Enumerated(EnumType.STRING)
    private DocumentType type;
    @ManyToOne
    @JoinColumn(
            name = "project_id"
    )
    private Project project;
    private String mimeType;
}
