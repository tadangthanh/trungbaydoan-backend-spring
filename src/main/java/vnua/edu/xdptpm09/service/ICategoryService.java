
package vnua.edu.xdptpm09.service;

import java.util.List;
import java.util.Optional;
import vnua.edu.xdptpm09.dto.CategoryDTO;

public interface ICategoryService {
    Optional<CategoryDTO> create(CategoryDTO categoryDTO);

    Optional<CategoryDTO> update(CategoryDTO categoryDTO);

    void delete(Long id);

    List<CategoryDTO> getAll();
}
