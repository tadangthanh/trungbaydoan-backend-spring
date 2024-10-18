
package vnua.edu.xdptpm09.service;

import java.util.List;
import java.util.Optional;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import vnua.edu.xdptpm09.dto.PageResponse;
import vnua.edu.xdptpm09.dto.ResetPassword;
import vnua.edu.xdptpm09.dto.UpdatePasswordDTO;
import vnua.edu.xdptpm09.dto.UserDTO;
import vnua.edu.xdptpm09.dto.UserRegister;
import vnua.edu.xdptpm09.dto.UserUpdateDTO;

public interface IUserService extends UserDetailsService {
    Optional<UserDTO> register(UserRegister userRegister);

    PageResponse<?> getAllUser(Pageable pageable);

    InputStreamResource downloadById(Long documentId);

    Optional<UserDTO> findByEmail(String email);

    boolean changePassword(Long id, UpdatePasswordDTO updatePasswordDTO);

    void resetPassword(ResetPassword resetPassword);

    List<UserDTO> findTeacherSuggestion(String email, List<String> emailsIgnore);

    List<UserDTO> findStudentSuggestion(String email, List<String> emailsIgnore);

    void forgotPassword(String email);

    Optional<UserDTO> changeAvatar(String email, MultipartFile file);

    Optional<UserDTO> updateProfile(Long id, UserUpdateDTO userUpdateDTO);

    PageResponse<?> getAllUserByAdmin(Pageable pageable, String search, String searchField);

    void activeUserById(Long id);

    void inActiveUserById(Long id);

    List<UserDTO> getAllUserByEmails(List<String> emails);
}
