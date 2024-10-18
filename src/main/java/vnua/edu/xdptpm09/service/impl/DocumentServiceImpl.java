//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import com.azure.storage.blob.BlobClient;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.DocumentDTO;
import vnua.edu.xdptpm09.dto.DocumentRequest;
import vnua.edu.xdptpm09.dto.DocumentResponse;
import vnua.edu.xdptpm09.entity.BaseEntity;
import vnua.edu.xdptpm09.entity.Document;
import vnua.edu.xdptpm09.entity.DocumentType;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.DocumentMapping;
import vnua.edu.xdptpm09.repository.DocumentRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;
import vnua.edu.xdptpm09.service.IAzureService;
import vnua.edu.xdptpm09.service.IDocumentService;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements IDocumentService {
    private final DocumentRepo documentRepo;
    private final DocumentMapping documentMapping;
    private final ProjectRepo projectRepo;
    private final IAzureService azureService;

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            )}
    )
    public Optional<DocumentDTO> save(MultipartFile file, Long projectId) {
        this.checkFileSize(file);
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        Document document = this.convertFileToDocument(file);
        document.setProject(project);
        return Optional.of(this.documentMapping.toDto(this.documentRepo.save(document)));
    }

    public Optional<DocumentResponse> upload(MultipartFile file) {
        Document document = this.convertFileToDocument(file);
        document = this.documentRepo.saveAndFlush(document);
        DocumentResponse documentResponse = new DocumentResponse();
        documentResponse.setId(document.getId());
        String url = this.azureService.getBlobUrl(document.getBlobName());
        documentResponse.setUrl(url);
        return Optional.of(documentResponse);
    }

    private Document convertFileToDocument(MultipartFile file) {
        this.checkFileSize(file);
        Document document = new Document();
        document.setMimeType(file.getContentType());
        document.setName((Objects.requireNonNull(file.getOriginalFilename())).split("\\.")[0]);
        document.setBlobName(this.azureService.upload(file));
        document.setSize(this.formatFileSize(file));
        document.setType(this.getDocumentType(file));
        return document;
    }

    private Document uploadProgress(MultipartFile file) throws IOException {
        this.checkFileSize(file);
        Document document = new Document();
        document.setMimeType(file.getContentType());
        document.setName((Objects.requireNonNull(file.getOriginalFilename())).split("\\.")[0]);
        document.setBlobName(this.azureService.uploadFileWithProgress(file));
        document.setSize(this.formatFileSize(file));
        document.setType(this.getDocumentType(file));
        return document;
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            )}
    )
    public List<DocumentDTO> saveAll(List<MultipartFile> files, Long projectId) {
        Project project = this.projectRepo.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy project"));
        this.checkFileVideo(files);
        List<Document> documents = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            Document document;
            try {
                document = this.uploadProgress(multipartFile);
            } catch (IOException e) {
                throw new BadRequestException("Lỗi khi upload file");
            }
            document.setProject(project);
            Document apply = document;
            documents.add(apply);
        }
        return this.documentMapping.toDto(this.documentRepo.saveAllAndFlush(documents));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            )}
    )
    public List<DocumentDTO> saveAll(List<MultipartFile> files) {
        this.checkFileVideo(files);
        files.forEach(this::checkFileSize);
        List<Document> documents = files.stream().map(this::convertFileToDocument).toList();
        return this.documentMapping.toDto(this.documentRepo.saveAllAndFlush(documents));
    }

    private void checkFileVideo(List<MultipartFile> files) {
        AtomicInteger dem = new AtomicInteger();
        files.forEach((file) -> {
            if ((Objects.requireNonNull(file.getContentType())).contains("video")) {
                dem.getAndIncrement();
            }
        });
        if (dem.get() > 1) {
            throw new BadRequestException("Chỉ được tải lên 1 video");
        }
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    key = "#id"
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"fileName"},
                    key = "#id"
            ), @CacheEvict(
                    value = {"blobUrl"},
                    key = "#id"
            )}
    )
    public void delete(Long id) {
        Document document = this.documentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        if (this.azureService.deleteBlob(document.getBlobName())) {
            this.documentRepo.delete(document);
        }

    }

    public InputStreamResource downloadById(Long documentId) {
        Document document = this.documentRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        return this.azureService.downloadBlob(document.getBlobName());
    }

    @Cacheable(
            value = {"fileName"},
            key = "#documentId"
    )
    public String getFileName(Long documentId) {
        Document document = this.documentRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        String extension = this.getExtension(document.getBlobName());
        String var10000 = document.getName();
        return var10000 + "." + extension;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Cacheable(
            value = {"document"},
            key = "#documentId"
    )
    public Optional<DocumentDTO> getDocumentById(Long documentId) {
        Document document = this.documentRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        return Optional.of(this.documentMapping.toDto(document));
    }

    public BlobClient getBlobClientWithDocumentId(Long documentId) {
        Document document = this.documentRepo.findById(documentId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        return this.azureService.getBlobClient(document.getBlobName());
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    key = "#projectId"
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            )}
    )
    public void deleteAllByProjectId(Long projectId) {
        List<Document> documents = this.documentRepo.findAllByProjectId(projectId);
        List<Long> ids = documents.stream().map(BaseEntity::getId).toList();
        Map<Long, String> blobNames = new HashMap<>();
        documents.forEach((document) -> blobNames.put(document.getId(), document.getBlobName()));
        ids.forEach((id) -> {
            if (this.documentRepo.deleteDocumentById(id) > 0) {
                this.azureService.deleteBlob(blobNames.get(id));
            }

        });
    }

    @Cacheable(
            value = {"documents"},
            key = "#projectId"
    )
    public List<DocumentDTO> getAllByProjectId(Long projectId) {
        List<Document> documents = this.documentRepo.findAllByProjectId(projectId);
        return this.documentMapping.toDto(documents);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            )}
    )
    public Optional<DocumentResponse> uploadWithProgress(MultipartFile file) {
        try {
            Document document = this.uploadProgress(file);
            document = this.documentRepo.saveAndFlush(document);
            DocumentResponse documentResponse = new DocumentResponse();
            documentResponse.setId(document.getId());
            String url = this.azureService.getBlobUrl(document.getBlobName());
            documentResponse.setUrl(url);
            return Optional.of(documentResponse);
        } catch (IOException var5) {
            throw new BadRequestException("Lỗi khi upload file");
        }
    }

    @Cacheable(
            value = {"blobUrl"},
            key = "#id"
    )
    public String getBlobUrl(Long id) {
        Document document = this.documentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        return this.azureService.getBlobUrl(document.getBlobName());
    }

    @Cacheable(
            value = {"documentsByProjectIds"},
            key = "#projectIds"
    )
    public List<DocumentDTO> getAllByProjectIds(List<Long> projectIds) {
        List<Document> documents = this.documentRepo.findAllByProjectIdIn(projectIds);
        return this.documentMapping.toDto(documents);
    }

    public void deleteDocumentsAnonymous(DocumentRequest documentRequest) {
        List<Long> ids = documentRequest.getIds();
        ids.forEach((id) -> {
            if (this.documentRepo.isContainProject(id) != null) {
                throw new BadRequestException("Không thể xóa document thuộc đồ án nào đó");
            } else {
                Document document = this.documentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
                if (document.getCreatedBy().equals(this.getCurrentEmail()) && this.azureService.deleteBlob(document.getBlobName())) {
                    this.documentRepo.delete(document);
                }

            }
        });
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            )}
    )
    public void deleteDocumentsByProjectId(DocumentRequest documentRequest) {
        List<Long> ids = documentRequest.getIds();
        ids.forEach((id) -> {
            Document document = this.documentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
            if (this.azureService.deleteBlob(document.getBlobName())) {
                this.documentRepo.delete(document);
            }

        });
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"documents"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"documentsByProjectIds"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"document"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projects"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsPending"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsActiveAndApproved"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"project"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"mentorsProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"membersProject"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsMentor"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsAdmin"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"projectsCategory"},
                    allEntries = true
            )}
    )
    public void deleteVideoByProjectId(Long projectId) {
        Document document = this.documentRepo.getDocumentByProjectIdAndType(projectId, DocumentType.VIDEO);
        if (document != null && this.azureService.deleteBlob(document.getBlobName())) {
            this.documentRepo.deleteVideoByProjectId(projectId);
        }

    }

    private void checkFileSize(MultipartFile file) {
        if (file.getSize() > 104857600L) {
            throw new BadRequestException("Kích thước file không được vượt quá 100MB");
        }
    }

    private DocumentType getDocumentType(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null) {
            return DocumentType.OTHER;
        } else if (mimeType.startsWith("image/")) {
            return DocumentType.IMAGE;
        } else if (mimeType.startsWith("video/")) {
            return DocumentType.VIDEO;
        } else if (mimeType.startsWith("application/") && (Objects.requireNonNull(file.getOriginalFilename())).endsWith(".pdf")) {
            return DocumentType.PDF;
        } else if (mimeType.startsWith("application/") && (Objects.requireNonNull(file.getOriginalFilename())).endsWith(".docx")) {
            return DocumentType.DOCX;
        } else if (mimeType.startsWith("application/") && (Objects.requireNonNull(file.getOriginalFilename())).endsWith(".pptx")) {
            return DocumentType.PPTX;
        } else {
            return mimeType.startsWith("application/") && (Objects.requireNonNull(file.getOriginalFilename())).endsWith(".xlsx") ? DocumentType.XLSX : DocumentType.OTHER;
        }
    }

    private String formatFileSize(MultipartFile file) {
        double fileSize = (double) file.getSize();
        String[] units = new String[]{"bytes", "KB", "MB", "GB"};

        int unitIndex;
        for (unitIndex = 0; fileSize >= 1024.0 && unitIndex < units.length - 1; ++unitIndex) {
            fileSize /= 1024.0;
        }

        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }

    @Scheduled(
            cron = "0 0 * * * ?"
    )
    protected void scheduleDeleteDocumentAnonymous() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1L);
        List<Document> documentsAnonymous = this.documentRepo.findAllDocumentByCreatedDate(oneHourAgo);
        documentsAnonymous.forEach((document) -> {
            if (this.azureService.deleteBlob(document.getBlobName())) {
                this.documentRepo.delete(document);
            }
        });
    }

}
