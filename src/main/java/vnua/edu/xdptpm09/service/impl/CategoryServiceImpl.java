
package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.CategoryDTO;
import vnua.edu.xdptpm09.entity.Category;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.CategoryMapping;
import vnua.edu.xdptpm09.repository.CategoryRepo;
import vnua.edu.xdptpm09.service.ICategoryService;

@Transactional
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryMapping categoryMapping;
    private final CategoryRepo categoryRepo;

    public Optional<CategoryDTO> create(CategoryDTO categoryDTO) {
        if (this.categoryRepo.existsCategoryByName(categoryDTO.getName().toLowerCase())) {
            throw new RuntimeException("Category name already exists");
        } else {
            Category category = this.categoryMapping.toEntity(categoryDTO);
            category = this.categoryRepo.saveAndFlush(category);
            return Optional.of(this.categoryMapping.toDto(category));
        }
    }

    public Optional<CategoryDTO> update(CategoryDTO categoryDTO) {
        Category category = this.categoryMapping.updateFromDTO(categoryDTO);
        category = this.categoryRepo.saveAndFlush(category);
        return Optional.of(this.categoryMapping.toDto(category));
    }

    public void delete(Long id) {
        Category category =this.categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (!category.getProjects().isEmpty()) {
            throw new BadRequestException("Category has projects, cannot delete");
        } else {
            this.categoryRepo.delete(category);
        }
    }

    public List<CategoryDTO> getAll() {
        return this.categoryMapping.toDto(this.categoryRepo.findAll());
    }


}
