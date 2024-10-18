package vnua.edu.xdptpm09.repository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vnua.edu.xdptpm09.entity.User;

@Repository
public interface UserRepo  extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailAndStatus(String email, boolean status);

    @Query("select u from User u where u.email = ?1 and u.status = ?2 and u.verifyCode is not null")
    Optional<User> findByEmailAndStatusAndVerifyCodeNotNull(String email, boolean status);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndVerifyCodeAndStatus(String email, String verifyCode, boolean status);

    Optional<User> findByVerifyCode(String verifyCode);

    void deleteUserByVerifyCode(String verifyCode);

    List<User> findByIdIn(List<Long> ids);

    @Modifying
    @Transactional
    @Query("update User u set u.password = ?2 where u.id = ?1")
    int changePassword(Long id, String password);

    @Query("select u from User u where u.role.name = 'ROLE_STUDENT'")
    List<User> findStudents();

    @Query("select u from User u where u.role.name= 'ROLE_TEACHER'")
    List<User> findTeachers();

    @Query("select u from User u where u.email like %?1% and u.role.name = 'ROLE_TEACHER' and (?2 is null or u.email not in ?2) and u.email != ?3")
    List<User> findSuggestionTeacher(String email, List<String> emailsIgnore, String currentEmail);

    @Query("select u from User u where u.email like %?1% and u.role.name = 'ROLE_STUDENT' and (?2 is null or u.email not in ?2) and u.email != ?3")
    List<User> findSuggestionStudent(String email, List<String> emailsIgnore, String currentEmail);

    @Query("select u from User u where u.id in ?1 and u.status = ?2")
    List<User> findAllBydIdInAndStatus(List<Long> ids, boolean status);

    @Query("select u from User u where u.role.name = 'ROLE_TEACHER' and u.status = true")
    List<User> findAllTeacher();

    @Query("select u from User u where u.email in ?1")
    List<User> findAllUserByEmailIn(List<String> emails);
}
