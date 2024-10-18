
package vnua.edu.xdptpm09.service;

import com.azure.storage.blob.BlobClient;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IAzureService {
    String upload(MultipartFile file);

    String getBlobUrl(String blobName);

    List<String> uploadAll(MultipartFile[] files);

    boolean deleteBlob(String blobName);

    String uploadFileWithProgress(MultipartFile file) throws IOException;

    Resource getResourceByBlobName(String blobName);

    InputStreamResource downloadBlob(String blobName);

    BlobClient getBlobClient(String blobName);
}
