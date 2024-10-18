package vnua.edu.xdptpm09.mapping.impl;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.DocumentDTO;
import vnua.edu.xdptpm09.entity.Document;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.DocumentRepo;
import vnua.edu.xdptpm09.repository.ProjectRepo;
import vnua.edu.xdptpm09.service.IAzureService;

@Component
@RequiredArgsConstructor
public class DocumentMapping implements Mapping<Document, DocumentDTO> {
    private final DocumentRepo documentRepo;
    private final ProjectRepo projectRepo;
    private final ModelMapper modelMapper;
    private final IAzureService azureService;

    public Document toEntity(DocumentDTO dto) {
        Document document = this.modelMapper.map(dto, Document.class);
        document.setProject(this.projectRepo.findById(dto.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Không tim thấy project với id: " + dto.getProjectId())));
        return document;
    }

    public DocumentDTO toDto(Document entity) {
        DocumentDTO documentDTO = this.modelMapper.map(entity, DocumentDTO.class);
        if (entity.getProject() == null) {
            return documentDTO;
        } else {
            documentDTO.setUrl(this.azureService.getBlobUrl(entity.getBlobName()));
            documentDTO.setProjectId(entity.getProject().getId());
            return documentDTO;
        }
    }

    public Document updateFromDTO(DocumentDTO dto) {
        Document documentExisting = this.documentRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Không tim thấy document với id: " + dto.getId()));
        this.modelMapper.map(dto, documentExisting);
        return documentExisting;
    }

    public List<Document> toEntity(List<DocumentDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<DocumentDTO> toDto(List<Document> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
