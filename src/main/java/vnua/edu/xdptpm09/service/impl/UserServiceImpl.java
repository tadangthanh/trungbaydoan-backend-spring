//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import jakarta.mail.internet.InternetAddress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vnua.edu.xdptpm09.dto.*;
import vnua.edu.xdptpm09.entity.Avatar;
import vnua.edu.xdptpm09.entity.Role;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.mapping.impl.UserMapping;
import vnua.edu.xdptpm09.repository.AvatarRepo;
import vnua.edu.xdptpm09.repository.RoleRepo;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.IAzureService;
import vnua.edu.xdptpm09.service.IEmailService;
import vnua.edu.xdptpm09.service.IRedisService;
import vnua.edu.xdptpm09.service.IUserService;
import vnua.edu.xdptpm09.util.UrlUtil;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserMapping userMapping;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final IRedisService redisTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IEmailService emailService;
    private final JwtService jwtService;
    private final IAzureService azureService;
    private final AvatarRepo avatarRepo;
    @Value("${api.version}")
    private String apiVersion;
    @Value("${api.path.view.avatar}")
    private String pathViewAvatar;

    @Caching(
            evict = {@CacheEvict(
                    value = {"users"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"user"},
                    allEntries = true
            )}
    )
    public Optional<UserDTO> register(UserRegister userRegister) {
        User user = this.userRepo.findByEmailAndStatus(userRegister.getEmail(), false).orElse(new User());
        String email = userRegister.getEmail();
        if (user.getVerifyCode() != null && !user.isStatus()) {
            throw new BadRequestException("Email đã được đăng ký, vui lòng kiểm tra email để xác thực tài khoản");
        } else if (this.userRepo.existsByEmail(email)) {
            throw new BadRequestException("Email đã tồn tại");
        } else if (!this.isEmail(email)) {
            throw new BadRequestException("Email không hợp lệ, chỉ chấp nhận email của sinh viên vnua");
        } else if (!this.isMatchPassword(userRegister.getPassword(), userRegister.getPasswordConfirm())) {
            throw new BadRequestException("Mật khẩu không khớp");
        } else {
            user.setEmail(email);
            user.setPassword(this.bCryptPasswordEncoder.encode(userRegister.getPassword()));
            user.setStatus(false);
            user.setRole(this.roleRepo.findByName("ROLE_STUDENT").orElseThrow(() -> new BadRequestException("Không tìm thấy role")));
            user.setVerifyCode(String.valueOf(this.jwtService.generateVerifyCode()));
            this.emailService.sendMailVerification(user.getEmail(), user.getVerifyCode());
            user = this.userRepo.saveAndFlush(user);
            return Optional.of(this.userMapping.toDto(user));
        }
    }

    private boolean isEmail(String email) {
        boolean isValid = true;

        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (Exception var4) {
            isValid = false;
        }

        return isValid && email.contains("vnua.edu.vn");
    }

    @Cacheable(
            value = {"users"},
            key = "#pageable.pageSize + #pageable.pageNumber+ #pageable.sort"
    )
    public PageResponse<?> getAllUser(Pageable pageable) {
        Page<UserDTO> page = this.userRepo.findAll(pageable).map(this.userMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    public InputStreamResource downloadById(Long avatarId) {
        Avatar avatar = this.avatarRepo.findById(avatarId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy document"));
        return this.azureService.downloadBlob(avatar.getBlobName());
    }

    @Cacheable(
            value = {"user"},
            key = "#email"
    )
    public Optional<UserDTO> findByEmail(String email) {
        User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        return Optional.of(this.userMapping.toDto(user));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"user"},
                    allEntries = true
            )}
    )
    public boolean changePassword(Long id, UpdatePasswordDTO updatePasswordDTO) {
        User userExisting = this.userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String currentPassword = updatePasswordDTO.getCurrentPassword();
        String newPassword = updatePasswordDTO.getNewPassword();
        String confirmPassword = updatePasswordDTO.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Mật khẩu mới không khớp");
        } else if (!currentPassword.equals(newPassword) && !this.bCryptPasswordEncoder.matches(this.bCryptPasswordEncoder.encode(currentPassword), userExisting.getPassword())) {
            if (!this.bCryptPasswordEncoder.matches(updatePasswordDTO.getCurrentPassword(), userExisting.getPassword())) {
                throw new BadRequestException("Mật khẩu cũ không đúng");
            } else if (!userExisting.getVerifyCode().equals(updatePasswordDTO.getCode())) {
                throw new BadRequestException("Mã xác thực không đúng");
            } else {
                userExisting.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                userExisting.setVerifyCode(null);
                this.redisTokenService.deleteTokens(userExisting.getEmail());
                this.emailService.sendSimpleMessage(userExisting.getEmail(), "Thay đổi mật khẩu!", "Mật khẩu của bạn đã được thay đổi thành công!");
                return true;
            }
        } else {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
    }

    public void resetPassword(ResetPassword resetPassword) {
        if (!this.isEmail(resetPassword.getEmail())) {
            throw new BadRequestException("Email không hợp lệ, kiểm tra lại");
        } else if (!this.userRepo.existsByEmail(resetPassword.getEmail())) {
            throw new ResourceNotFoundException("Không tìm thấy email");
        } else {
            User user = this.userRepo.findByEmailAndVerifyCodeAndStatus(resetPassword.getEmail(), resetPassword.getVerifyCode(), true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu reset mật khẩu hoặc mã xác nhận không đúng"));
            user.setVerifyCode(null);
            String newPassword = String.valueOf(this.jwtService.generateVerifyCode());
            user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            System.out.println("new password: " + newPassword);
            this.userRepo.saveAndFlush(user);
            this.emailService.sendSimpleMessage(resetPassword.getEmail(), "Reset mật khẩu!", "Mật khẩu mới của bạn là: " + newPassword);
        }
    }

    public List<UserDTO> findTeacherSuggestion(String email, List<String> emailsIgnore) {
        List<User> users = this.userRepo.findSuggestionTeacher(email, emailsIgnore, this.getCurrentEmail());
        return this.userMapping.toDto(users);
    }

    private String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public List<UserDTO> findStudentSuggestion(String email, List<String> emailsIgnore) {
        List<User> users = this.userRepo.findSuggestionStudent(email, emailsIgnore, this.getCurrentEmail());
        return this.userMapping.toDto(users);
    }

    public void forgotPassword(String email) {
        if (!this.isEmail(email)) {
            throw new BadRequestException("Email không hợp lệ, kiểm tra lại");
        } else if (!this.userRepo.existsByEmail(email)) {
            throw new ResourceNotFoundException("Không tìm thấy email");
        } else {
            User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
            String code = this.jwtService.generateVerifyCode();
            user.setVerifyCode(code);
            this.emailService.sendSimpleMessage(email, "Quên mật khẩu!", "Mã xác thực của bạn là: " + code);
            this.userRepo.saveAndFlush(user);
        }
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"users"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"user"},
                    key = "#email"
            )}
    )
    public Optional<UserDTO> changeAvatar(String email, MultipartFile file) {
        if (file.getSize() <= 10000000L && !file.isEmpty() && Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            if (!email.equals(this.getCurrentEmail())) {
                throw new BadRequestException("Không thể thay đổi ảnh đại diện của người khác");
            } else {
                User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
                Avatar avatar = user.getAvatar();
                if (avatar != null) {
                    this.azureService.deleteBlob(avatar.getBlobName());
                } else {
                    avatar = new Avatar();
                }
                String blobName = this.azureService.upload(file);
                avatar.setBlobName(blobName);
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                String avatarUrl = baseUrl + "/api/" + this.apiVersion + "/" + this.pathViewAvatar + "/" + avatar.getId();
                avatar.setUrl(avatarUrl);
                avatar.setName(file.getOriginalFilename());
                avatar = this.avatarRepo.saveAndFlush(avatar);
                user.setAvatar(avatar);
                this.userRepo.saveAndFlush(user);
                return Optional.of(this.userMapping.toDto(user));
            }
        } else {
            throw new BadRequestException("Ảnh không hợp lệ, vui lòng chọn Ảnh nhỏ hơn 10 MB");
        }
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"users"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"user"},
                    allEntries = true
            )}
    )
    public Optional<UserDTO> updateProfile(Long id, UserUpdateDTO userUpdateDTO) {
        User userExisting = this.userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        userExisting.setGithubUrl(userUpdateDTO.getGithubUrl());
        userExisting.setFacebookUrl(userUpdateDTO.getFacebookUrl());
        userExisting = this.userRepo.saveAndFlush(userExisting);
        return Optional.of(this.userMapping.toDto(userExisting));
    }

    @Cacheable(
            value = {"usersByAdmin"},
            key = "#pageable.pageSize + #pageable.pageNumber + #search + #searchField +#pageable.sort"
    )
    public PageResponse<?> getAllUserByAdmin(Pageable pageable, String search, String searchField) {
        Specification<User> spec = Specification.where((Specification) null);
        if (searchField == null || searchField.isEmpty()) {
            searchField = "fullName";
        }
        if (search != null && !search.isEmpty()) {
            String finalSearchField = searchField;
            if (searchField.equals("status")) {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(finalSearchField), Boolean.parseBoolean(Integer.parseInt(search) == 1 ? "true" : "false")));
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(finalSearchField), "%" + search + "%"));
            }
        }

        Page<UserDTO> page = this.userRepo.findAll(spec, pageable).map(this.userMapping::toDto);
        return PageResponse.builder().pageSize(page.getSize()).currentPage(page.getNumber() + 1).hasNext(page.hasNext()).totalItems((int) page.getTotalElements()).totalPages(page.getTotalPages()).items(page.getContent()).build();
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"users"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"user"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"usersByAdmin"},
                    allEntries = true
            )}
    )
    public void activeUserById(Long id) {
        User user = this.userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        user.setStatus(true);
        this.redisTokenService.deleteTokens(user.getEmail());
        this.userRepo.saveAndFlush(user);
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"users"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"user"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"usersByAdmin"},
                    allEntries = true
            )}
    )
    public void inActiveUserById(Long id) {
        User user = this.userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
        user.setStatus(false);
        this.redisTokenService.deleteTokens(user.getEmail());
        this.userRepo.saveAndFlush(user);
    }

    public List<UserDTO> getAllUserByEmails(List<String> emails) {
        List<User> users = this.userRepo.findAllUserByEmailIn(emails);
        return this.userMapping.toDto(users);
    }

    private boolean isMatchPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new UsernameNotFoundException("không tìm thấy user"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), this.getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Role role = this.roleRepo.findByName(user.getRole().getName()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy role"));
        return List.of(new SimpleGrantedAuthority(role.getName()));
    }

}
