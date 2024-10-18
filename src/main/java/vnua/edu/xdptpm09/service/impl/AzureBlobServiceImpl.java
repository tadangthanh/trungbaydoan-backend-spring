//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.UploadFailureException;
import vnua.edu.xdptpm09.service.IAzureService;

@RequiredArgsConstructor
@Service
public class AzureBlobServiceImpl implements IAzureService {
    private static final Logger log = LoggerFactory.getLogger(AzureBlobServiceImpl.class);
    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String containerName;
    private final BlobServiceClient blobServiceClient;
    @Value("${azure.blob-storage.connection-string}")
    private String connectionString;
    private final SimpMessagingTemplate messagingTemplate;

    public String upload(MultipartFile file) {
        try {
            long var10000 = System.currentTimeMillis();
            String blobFileName = "" + var10000 + "_" + file.getOriginalFilename();
            BlobClient blobClient = this.blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobFileName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return blobClient.getBlobName();
        } catch (IOException var4) {
            throw new UploadFailureException("Lỗi khi upload file");
        }
    }

    public String getBlobUrl(String blobName) {
        BlobServiceClient blobServiceClient = (new BlobServiceClientBuilder()).connectionString(this.connectionString).buildClient();
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobName);
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(10L);
        BlobSasPermission permission = (new BlobSasPermission()).setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = (new BlobServiceSasSignatureValues(expiryTime, permission)).setStartTime(OffsetDateTime.now());
        String sasToken = blobClient.generateSas(sasValues);
        String var10000 = blobClient.getBlobUrl();
        return var10000 + "?" + sasToken;
    }

    public List<String> uploadAll(MultipartFile[] files) {
        List<String> result = new ArrayList();
        MultipartFile[] var3 = files;
        int var4 = files.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            MultipartFile file = var3[var5];
            String blobName = this.upload(file);
            result.add(blobName);
        }

        return result;
    }

    public boolean deleteBlob(String blobName) {
        BlobClient blobClient = this.blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobName);
        return blobClient.deleteIfExists();
    }

    public String uploadFileWithProgress(MultipartFile file) throws IOException {
        BlobContainerClient containerClient = this.blobServiceClient.getBlobContainerClient(this.containerName);
        String blobFileName = System.currentTimeMillis()+ "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(blobFileName);
        InputStream inputStream = file.getInputStream();
        long fileSize = file.getSize();
        int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        long uploadedBytes = 0L;
        double previousProgress = 0.0;
        String currentEmail = this.getCurrentEmail();
        this.messagingTemplate.convertAndSendToUser(currentEmail, "/topic/upload-progress", 0.0);

        try {
            OutputStream outputStream = blobClient.getBlockBlobClient().getBlobOutputStream();

            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    uploadedBytes +=  bytesRead;
                    double progress = (double) uploadedBytes / (double) fileSize * 100.0;
                    if (progress > 99.0) {
                        progress = 99.0;
                    }

                    if (Math.abs(progress - previousProgress) >= 1.0) {
                        this.messagingTemplate.convertAndSendToUser(currentEmail, "/topic/upload-progress", progress);
                        System.out.printf("Upload progress: %.2f%%\n", progress);
                        previousProgress = progress;
                    }
                }
            } catch (Throwable var27) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable var26) {
                        var27.addSuppressed(var26);
                    }
                }

                throw var27;
            }

            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException var28) {
            throw new UploadFailureException("Lỗi khi upload file");
        } finally {
            inputStream.close();
        }

        this.messagingTemplate.convertAndSendToUser(currentEmail, "/topic/upload-progress", 100.0);
        return blobClient.getBlobName();
    }

    public Resource getResourceByBlobName(String blobName) {
        BlobClient blobClient = this.blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobName);

        try {
            InputStream inputStream = blobClient.openInputStream();
            return new InputStreamResource(inputStream);
        } catch (Exception var4) {
            Exception e = var4;
            log.error("Lỗi khi lấy resource từ blob", e);
            throw new BadRequestException("Lỗi khi lấy resource từ blob");
        }
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public InputStreamResource downloadBlob(String blobName) {
        BlobClient blobClient = (new BlobClientBuilder()).connectionString(this.connectionString).containerName(this.containerName).blobName(blobName).buildClient();
        byte[] content = blobClient.downloadContent().toBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        return new InputStreamResource(inputStream);
    }

    public BlobClient getBlobClient(String blobName) {
        return this.blobServiceClient.getBlobContainerClient(this.containerName).getBlobClient(blobName);
    }

}
