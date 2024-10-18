package vnua.edu.xdptpm09.repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.Technology;

@Repository
public interface TechnologyRepo extends JpaRepository<Technology, Long> {
    @Query("SELECT t FROM Technology t WHERE LOWER(t.name) = LOWER(:name)")
    Page<Technology> findByName(Pageable pageable, String name);

    boolean existsTechnologyByName(String name);

    List<Technology> findAllByIdIn(List<Long> ids);
}
