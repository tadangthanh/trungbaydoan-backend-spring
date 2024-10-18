package vnua.edu.xdptpm09.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            name = "created_date"
    )
    @CreatedDate
    private LocalDateTime createdDate;
    @Column(
            name = "created_by"
    )
    @CreatedBy
    private String createdBy;
    @Column(
            name = "last_modified_date"
    )
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @Column(
            name = "last_modified_by"
    )
    @LastModifiedBy
    private String lastModifiedBy;
}
