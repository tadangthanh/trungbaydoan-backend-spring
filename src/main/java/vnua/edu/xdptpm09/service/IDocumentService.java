
package vnua.edu.xdptpm09.service;

import com.azure.storage.blob.BlobClient;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.DocumentDTO;
import vnua.edu.xdptpm09.dto.DocumentRequest;
import vnua.edu.xdptpm09.dto.DocumentResponse;

public interface IDocumentService {
    Optional<DocumentDTO> save(MultipartFile file, Long projectId);

    Optional<DocumentResponse> upload(MultipartFile file);

    List<DocumentDTO> saveAll(List<MultipartFile> files, Long projectId);

    List<DocumentDTO> saveAll(List<MultipartFile> files);

    void delete(Long id);

    InputStreamResource downloadById(Long documentId);

    String getFileName(Long documentId);

    Optional<DocumentDTO> getDocumentById(Long documentId);

    BlobClient getBlobClientWithDocumentId(Long documentId);

    void deleteAllByProjectId(Long projectId);

    List<DocumentDTO> getAllByProjectId(Long projectId);

    Optional<DocumentResponse> uploadWithProgress(MultipartFile file);

    String getBlobUrl(Long id);

    List<DocumentDTO> getAllByProjectIds(List<Long> projectIds);

    void deleteDocumentsAnonymous(DocumentRequest documentRequest);

    void deleteDocumentsByProjectId(DocumentRequest documentRequest);

    void deleteVideoByProjectId(Long projectId);
}
