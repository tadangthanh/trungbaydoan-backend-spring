
package vnua.edu.xdptpm09.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.TechnologyDTO;

public interface ITechnologyService {
    Optional<TechnologyDTO> create(TechnologyDTO technologyDTO);

    void deleteById(Long id);

    PageResponse<?> findByName(Pageable pageable, String name);

    List<TechnologyDTO> findAll();

    List<TechnologyDTO> findAllByIdIn(List<Long> ids);
}
