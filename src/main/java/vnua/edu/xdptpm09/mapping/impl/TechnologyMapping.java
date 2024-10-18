package vnua.edu.xdptpm09.mapping.impl;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.TechnologyDTO;
import vnua.edu.xdptpm09.entity.Technology;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.TechnologyRepo;

@Component
@RequiredArgsConstructor
public class TechnologyMapping implements Mapping<Technology, TechnologyDTO> {
    private final TechnologyRepo technologyRepo;
    private final ModelMapper modelMapper;

    public Technology toEntity(TechnologyDTO dto) {
        return this.modelMapper.map(dto, Technology.class);
    }

    public TechnologyDTO toDto(Technology entity) {
        return this.modelMapper.map(entity, TechnologyDTO.class);
    }

    public Technology updateFromDTO(TechnologyDTO dto) {
        Technology technology = this.technologyRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Technology not found"));
        this.modelMapper.map(dto, technology);
        return technology;
    }

    public List<Technology> toEntity(List<TechnologyDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<TechnologyDTO> toDto(List<Technology> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
