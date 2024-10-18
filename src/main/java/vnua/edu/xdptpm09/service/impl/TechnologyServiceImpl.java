//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.TechnologyDTO;
import vnua.edu.xdptpm09.entity.Project;
import vnua.edu.xdptpm09.entity.Technology;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.TechnologyMapping;
import vnua.edu.xdptpm09.repository.TechnologyRepo;
import vnua.edu.xdptpm09.service.ITechnologyService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TechnologyServiceImpl implements ITechnologyService {
    private final TechnologyMapping technologyMapping;
    private final TechnologyRepo technologyRepo;

    public Optional<TechnologyDTO> create(TechnologyDTO technologyDTO) {
        Technology technology = this.technologyMapping.toEntity(technologyDTO);
        technology = this.technologyRepo.saveAndFlush(technology);
        return Optional.of(this.technologyMapping.toDto(technology));
    }

    public void deleteById(Long id) {
        Technology technology = this.technologyRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công nghệ này"));
        for (Project project : technology.getProjects()) {
            project.getTechnologies().remove(technology);
        }
        this.technologyRepo.deleteById(id);
    }

    public PageResponse<?> findByName(Pageable pageable, String name) {
        Page<Technology> page = this.technologyRepo.findByName(pageable, name.trim().toLowerCase());
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int)page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Cacheable({"technologies"})
    public List<TechnologyDTO> findAll() {
        List<Technology> technologies = this.technologyRepo.findAll();
        technologies.sort(Comparator.comparing(Technology::getName, String.CASE_INSENSITIVE_ORDER));
        return technologies.stream().map(this.technologyMapping::toDto).collect(Collectors.toList());
    }

    public List<TechnologyDTO> findAllByIdIn(List<Long> ids) {
        return this.technologyRepo.findAllByIdIn(ids).stream().map(this.technologyMapping::toDto).collect(Collectors.toList());
    }

}
