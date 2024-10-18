package vnua.edu.xdptpm09.mapping.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vnua.edu.xdptpm09.dto.CategoryDTO;
import vnua.edu.xdptpm09.entity.Category;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.Mapping;
import vnua.edu.xdptpm09.repository.CategoryRepo;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryMapping implements Mapping<Category, CategoryDTO> {
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public Category toEntity(CategoryDTO dto) {
        return this.modelMapper.map(dto, Category.class);
    }

    public CategoryDTO toDto(Category entity) {
        return this.modelMapper.map(entity, CategoryDTO.class);
    }

    public Category updateFromDTO(CategoryDTO dto) {
        Category categoryExisting = this.categoryRepo.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        this.modelMapper.map(dto, categoryExisting);
        return categoryExisting;
    }

    public List<Category> toEntity(List<CategoryDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }

    public List<CategoryDTO> toDto(List<Category> entity) {
        return entity.stream().map(this::toDto).toList();
    }
}
