package vnua.edu.xdptpm09.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Document;
import vnua.edu.xdptpm09.entity.DocumentType;

@Repository
public interface DocumentRepo  extends JpaRepository<Document, Long> {
    List<Document> findAllByProjectId(Long projectId);

    int countByTypeAndProjectId(DocumentType type, Long project_id);

    void deleteByProjectId(Long projectId);

    int deleteDocumentById(Long documentId);

    @Query("SELECT d FROM Document d WHERE d.project.id IN :projectIds and d.mimeType in ('application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document')")
    List<Document> findAllByProjectIdIn(List<Long> projectIds);

    @Query("SELECT d FROM Document d WHERE d.project.id = :projectId")
    Document isContainProject(Long projectId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Document d WHERE d.project.id = :projectId and d.type = vnua.edu.xdptpm09.entity.DocumentType.VIDEO")
    void deleteVideoByProjectId(Long projectId);

    @Query("SELECT d FROM Document d WHERE d.project.id = :projectId and d.type = :type")
    Document getDocumentByProjectIdAndType(Long projectId, DocumentType type);

    @Query("SELECT d FROM Document d WHERE d.project = null and d.createdDate < :createdDate")
    List<Document> findAllDocumentByCreatedDate(LocalDateTime createdDate);
}
