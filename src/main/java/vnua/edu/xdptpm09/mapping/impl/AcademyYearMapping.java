package vnua.edu.xdptpm09.mapping.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.AcademyYearDTO;
import vnua.edu.xdptpm09.entity.AcademyYear;
import vnua.edu.xdptpm09.mapping.Mapping;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AcademyYearMapping implements Mapping<AcademyYear, AcademyYearDTO> {
    private final ModelMapper modelMapper;

    public AcademyYear toEntity(AcademyYearDTO dto) {
        return  this.modelMapper.map(dto, AcademyYear.class);
    }

    public AcademyYearDTO toDto(AcademyYear entity) {
        return  this.modelMapper.map(entity, AcademyYearDTO.class);
    }

    public AcademyYear updateFromDTO(AcademyYearDTO dto) {
        return  this.modelMapper.map(dto, AcademyYear.class);
    }

    public List<AcademyYear> toEntity(List<AcademyYearDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<AcademyYearDTO> toDto(List<AcademyYear> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
