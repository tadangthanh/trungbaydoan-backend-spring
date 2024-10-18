
package vnua.edu.xdptpm09.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.AcademyYearDTO;
import vnua.edu.xdptpm09.mapping.impl.AcademyYearMapping;
import vnua.edu.xdptpm09.repository.AcademyYearRepo;
import vnua.edu.xdptpm09.service.IAcademyYearService;

@Service
@RequiredArgsConstructor
public class AcademyYearServiceImpl implements IAcademyYearService {
    private final AcademyYearMapping academyYearMapping;
    private final AcademyYearRepo academyYearRepo;

    public List<AcademyYearDTO> getAll() {
        return this.academyYearMapping.toDto(this.academyYearRepo.findAll());
    }

}
