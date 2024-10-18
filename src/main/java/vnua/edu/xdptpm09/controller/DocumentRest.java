//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobRange;
import com.azure.storage.blob.options.BlobInputStreamOptions;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.DocumentDTO;
import vnua.edu.xdptpm09.dto.DocumentRequest;
import vnua.edu.xdptpm09.dto.DocumentResponse;
import vnua.edu.xdptpm09.dto.ResponseCustom;
import vnua.edu.xdptpm09.entity.Document;
import vnua.edu.xdptpm09.repository.DocumentRepo;
import vnua.edu.xdptpm09.service.IAzureService;
import vnua.edu.xdptpm09.service.IDocumentService;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/documents"})
@RequiredArgsConstructor
public class DocumentRest {
    private final IDocumentService documentService;
    private final IAzureService azureService;
    private final DocumentRepo documentRepo;

    @PostMapping({"/upload/{projectId}"})
    public ResponseEntity<ResponseCustom> uploadProject(@RequestParam MultipartFile file, @PathVariable Long projectId) {
        DocumentDTO documentDTO = this.documentService.save(file, projectId).orElse(null);
        return documentDTO == null ? ResponseEntity.badRequest().body(new ResponseCustom("Upload failed", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Upload successfully", HttpStatus.CREATED.value(), documentDTO));
    }

    @PostMapping({"/upload"})
    public ResponseEntity<ResponseCustom> upload(@RequestParam MultipartFile file) {
        DocumentResponse response = this.documentService.upload(file).orElse(null);
        if (response == null) {
            return ResponseEntity.badRequest().body(new ResponseCustom("Upload failed", HttpStatus.BAD_REQUEST.value(), null));
        } else {
            System.out.println("url = " + response.getUrl());
            return ResponseEntity.ok(new ResponseCustom("Upload successfully", HttpStatus.CREATED.value(), response));
        }
    }

    @GetMapping({"/project/{projectId}"})
    public ResponseEntity<ResponseCustom> getAllByProjectId(@PathVariable @Min(1L) Long projectId) {
        List<DocumentDTO> documentDTOS = this.documentService.getAllByProjectId(projectId);
        return ResponseEntity.ok(new ResponseCustom("Get all documents by project id successfully", HttpStatus.OK.value(), documentDTOS));
    }

    @GetMapping("/view/{documentId}")
    public ResponseEntity<InputStreamResource> viewDocument(@PathVariable Long documentId) {
        InputStreamResource resource = documentService.downloadById(documentId);
        DocumentDTO documentDTO = documentService.getDocumentById(documentId).orElse(null);

        if (documentDTO == null) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = URLEncoder.encode(documentDTO.getName(), StandardCharsets.UTF_8);
        String contentDisposition = "inline; filename=" + encodedFileName;

        return ResponseEntity.ok()
                .header("Content-Disposition", contentDisposition)
                .contentType(MediaType.parseMediaType(documentDTO.getMimeType()))
                .body(resource);
    }

    @GetMapping("/blob/{id}")
    public ResponseEntity<InputStreamResource> getUrl(@PathVariable Long id) {
        Document document = documentRepo.findById(id).orElse(null);
        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = azureService.downloadBlob(document.getBlobName());
        DocumentDTO documentDTO = documentService.getDocumentById(id).orElse(null);
        if (documentDTO == null) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = URLEncoder.encode(documentDTO.getName(), StandardCharsets.UTF_8);
        String contentDisposition = "inline; filename=" + encodedFileName;

        return ResponseEntity.ok()
                .header("Content-Disposition", contentDisposition)
                .contentType(MediaType.parseMediaType(documentDTO.getMimeType()))
                .body(resource);
    }

    @GetMapping({"/video/{documentId}"})
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable @Min(1L) Long documentId, @RequestHeader(value = "Range", required = false) String rangeHeader) {
        BlobClient blobClient = this.documentService.getBlobClientWithDocumentId(documentId);
        long fileSize = blobClient.getProperties().getBlobSize();
        BlobRange blobRange;
        long contentLength;
        if (rangeHeader == null) {
            blobRange = new BlobRange(0L);
        } else {
            HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
            long start = range.getRangeStart(fileSize);
            contentLength = range.getRangeEnd(fileSize);
            long length = contentLength - start + 1L;
            blobRange = new BlobRange(start, length);
        }

        BlobInputStreamOptions options = (new BlobInputStreamOptions()).setRange(blobRange);
        InputStream videoStream = blobClient.openInputStream(options);
        InputStreamResource inputStreamResource = new InputStreamResource(videoStream);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "video/mp4");
        headers.set("Accept-Ranges", "bytes");
        if (rangeHeader != null) {
            contentLength = blobRange.getCount();
            long var10002 = blobRange.getOffset();
            headers.set("Content-Range", "bytes " + var10002 + "-" + (blobRange.getOffset() + contentLength - 1L) + "/" + fileSize);
            headers.setContentLength(contentLength);
            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.PARTIAL_CONTENT);
        } else {
            headers.setContentLength(fileSize);
            return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
        }
    }

    @GetMapping({"/download/{documentId}"})
    public ResponseEntity<InputStreamResource> getVideo(@PathVariable Long documentId) {
        String fileName = this.documentService.getFileName(documentId);
        return (ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + fileName)).contentType(MediaType.APPLICATION_OCTET_STREAM).body(this.documentService.downloadById(documentId));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<ResponseCustom> delete(@PathVariable @Min(1L) Long id) {
        this.documentService.delete(id);
        return ResponseEntity.ok(new ResponseCustom("Delete successfully", HttpStatus.OK.value(), null));
    }

    @PostMapping({"/upload-progress"})
    public ResponseEntity<ResponseCustom> uploadFile(@RequestParam("file") MultipartFile file) {
        System.out.println("upload file");
        DocumentResponse response = this.documentService.uploadWithProgress(file).orElse(null);
        return response == null ? ResponseEntity.badRequest().body(new ResponseCustom("Upload failed", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Upload successfully", HttpStatus.CREATED.value(), response));
    }

    @PostMapping({"/uploadAll"})
    public ResponseEntity<ResponseCustom> uploadAll(@RequestParam("files") List<MultipartFile> files) {
        List<DocumentDTO> documentDTOS = this.documentService.saveAll(files);
        return documentDTOS == null ? ResponseEntity.badRequest().body(new ResponseCustom("Upload failed", HttpStatus.BAD_REQUEST.value(), null)) : ResponseEntity.ok(new ResponseCustom("Upload successfully", HttpStatus.CREATED.value(), documentDTOS));
    }

    @GetMapping({"/blob-url/{id}"})
    public ResponseEntity<ResponseCustom> getBlobUrl(@PathVariable @Min(1L) Long id) {
        String url = this.documentService.getBlobUrl(id);
        return ResponseEntity.ok(new ResponseCustom("Get blob url successfully", HttpStatus.OK.value(), url));
    }

    @PostMapping({"/projects"})
    public ResponseEntity<ResponseCustom> getAllDocumentByProjectIds(@RequestBody List<Long> projectIds) {
        List<DocumentDTO> documentDTOS = this.documentService.getAllByProjectIds(projectIds);
        return ResponseEntity.ok(new ResponseCustom("Get all documents by project ids successfully", HttpStatus.OK.value(), documentDTOS));
    }

    @DeleteMapping({"/delete-anonymous"})
    public ResponseEntity<ResponseCustom> deleteDocumentsAnonymous(@RequestBody @Valid DocumentRequest documentRequest) {
        this.documentService.deleteDocumentsAnonymous(documentRequest);
        return ResponseEntity.ok(new ResponseCustom("Delete documents successfully", HttpStatus.OK.value(), null));
    }

    @DeleteMapping
    public ResponseEntity<ResponseCustom> deleteDocumentsByProjectId(@RequestBody @Valid DocumentRequest documentRequest) {
        this.documentService.deleteDocumentsByProjectId(documentRequest);
        return ResponseEntity.ok(new ResponseCustom("Delete documents successfully", HttpStatus.OK.value(), null));
    }


}
