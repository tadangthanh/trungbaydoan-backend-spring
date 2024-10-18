
package vnua.edu.xdptpm09.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.dto.AuthenticationRequest;
import vnua.edu.xdptpm09.dto.AuthenticationResponse;
import vnua.edu.xdptpm09.dto.UserVNUADTO;
import vnua.edu.xdptpm09.entity.AcademyYear;
import vnua.edu.xdptpm09.entity.User;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.exception.ResourceNotFoundException;
import vnua.edu.xdptpm09.exception.UnauthorizedException;
import vnua.edu.xdptpm09.repository.AcademyYearRepo;
import vnua.edu.xdptpm09.repository.RoleRepo;
import vnua.edu.xdptpm09.repository.UserRepo;
import vnua.edu.xdptpm09.service.IAuthenticationService;
import vnua.edu.xdptpm09.service.IEmailService;
import vnua.edu.xdptpm09.service.IRedisService;
import vnua.edu.xdptpm09.util.VnuaUtil;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final VnuaUtil vnuaUtil;
    private final AcademyYearRepo academyYearRepo;
    private final RoleRepo roleRepo;
    private final AuthenticationManager authenticationManager;
    private final IRedisService redisTokenService;
    private final IEmailService emailService;
    private final ModelMapper modelMapper;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest request) {
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (Exception var7) {
            throw new BadRequestException("Tài khoản hoặc mật khẩu không chính xác!");
        }

        String email = authenticationRequest.getEmail();
        User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("không tìm thấy người dùng"));
        if (user.getFullName() == null) {
            UserVNUADTO userVnua = this.vnuaUtil.getInfoUserFromVNUA(email.split("@")[0]);
            if (userVnua != null) {
                this.modelMapper.map(this.vnuaUtil.getInfoUserFromVNUA(email.split("@")[0]), user);
            }
            this.userRepo.saveAndFlush(user);
        }

        String token = this.jwtService.generateToken(user);
        String refreshToken = this.jwtService.generateRefreshToken(this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("không tìm thấy người dùng")), request);
        this.redisTokenService.removeTokensBlacklistByEmail(email);
        this.redisTokenService.deleteTokens(email);
        this.redisTokenService.saveTokens(authenticationRequest.getEmail(), token, refreshToken);
        return AuthenticationResponse.builder().token(token).refreshToken(refreshToken).build();
    }

    public AuthenticationResponse refreshToken(String refreshToken, HttpServletRequest request) {
        String email = this.jwtService.extractEmail(refreshToken);
        if (this.redisTokenService.isBlacklisted(email, refreshToken)) {
            this.redisTokenService.deleteTokens(this.jwtService.extractEmail(refreshToken));
            throw new UnauthorizedException("Refresh token is blacklisted, please login again");
        } else if (!request.getRemoteAddr().equals(this.jwtService.extractIp(refreshToken))) {
            this.redisTokenService.blacklistToken(email, refreshToken);
            this.redisTokenService.deleteTokens(email);
            throw new UnauthorizedException("Your IP address has changed. Please log in again to continue.");
        } else if (this.jwtService.tokenIsValid(refreshToken) && this.redisTokenService.isRefreshTokenValid(email, refreshToken)) {
            User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            String newAccessToken = this.jwtService.generateToken(user);
            String newRefreshToken = this.jwtService.generateRefreshToken(user, request);
            this.redisTokenService.blacklistToken(email, refreshToken);
            this.redisTokenService.saveTokens(email, newAccessToken, newRefreshToken);
            return AuthenticationResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).build();
        } else {
            throw new UnauthorizedException("Invalid refresh token");
        }
    }

    private boolean isTimeValid(LocalDateTime createTime) {
        LocalDateTime now = LocalDateTime.now();
        return !now.isAfter(createTime.plusMinutes(5L));
    }

    @Caching(
            evict = {@CacheEvict(
                    value = {"user"},
                    allEntries = true
            ), @CacheEvict(
                    value = {"users"},
                    allEntries = true
            )}
    )
    public boolean verifyAccount(String code) {
        User user = this.userRepo.findByVerifyCode(code).orElseThrow(() -> new ResourceNotFoundException("Mã xác thực không hợp lệ"));
        if (!this.isTimeValid(user.getLastModifiedDate())) {
            this.userRepo.deleteUserByVerifyCode(code);
            return false;
        } else {
            user.setStatus(true);
            String studentCode = user.getEmail().split("@")[0];
            UserVNUADTO userVnua = this.vnuaUtil.getInfoUserFromVNUA(studentCode);
            if (userVnua != null) {
                this.modelMapper.map(this.vnuaUtil.getInfoUserFromVNUA(studentCode), user);
            }

            if (!this.vnuaUtil.isNumeric(studentCode) && this.isTeacher(user.getEmail())) {
                user.setRole(this.roleRepo.findByName("ROLE_TEACHER").orElseThrow(() -> new ResourceNotFoundException("Role not found")));
            }

            this.createAcademyYear(user);
            user.setVerifyCode(null);
            this.userRepo.saveAndFlush(user);
            return true;
        }
    }

    public void resendCode(String email) {
        User user = this.userRepo.findByEmailAndStatus(email, false).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        if (user.getVerifyCode() == null) {
            throw new BadRequestException("Tài khoản đã được xác thực");
        } else {
            user.setVerifyCode(this.jwtService.generateVerifyCode());
            this.userRepo.saveAndFlush(user);
            this.emailService.sendMailVerification(user.getEmail(), user.getVerifyCode());
        }
    }

    public void logout(String token, String refreshToken) {
        String email = this.jwtService.extractEmail(token);
        this.redisTokenService.blacklistToken(email, refreshToken);
        this.redisTokenService.deleteTokens(email);
    }

    public boolean verifyToken(String token) {
        return this.jwtService.tokenIsValid(token) && this.redisTokenService.isAccessTokenValid(this.jwtService.extractEmail(token), token);
    }

    public void sendCodeChangePassword(String email) {
        User user = this.userRepo.findByEmailAndStatus(email, true).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        if (user.getVerifyCode() == null && this.isTimeValid(user.getLastModifiedDate())) {
            throw new BadRequestException("Vui lòng đợi 5 phút sau khi đổi mật khẩu");
        } else {
            user.setVerifyCode(this.jwtService.generateVerifyCode());
            this.userRepo.saveAndFlush(user);
            this.emailService.sendMailVerification(user.getEmail(), user.getVerifyCode());
        }
    }

    public boolean verifyTokenAdmin(String token) {
        return this.jwtService.tokenIsValid(token) && this.redisTokenService.isAccessTokenValid(this.jwtService.extractEmail(token), token) && (this.jwtService.extractRole(token).equals("ROLE_ADMIN") || this.jwtService.extractRole(token).equals("ROLE_TEACHER"));
    }

    private boolean isTeacher(String email) {
        return !email.contains("sv");
    }

    private void createAcademyYear(User user) {
        int year = user.getAcademicYear();
        AcademyYear academyYear = new AcademyYear();
        academyYear.setNumber(year);
        if (year != 0) {
            this.academyYearRepo.findByNumber(year).orElseGet(() -> this.academyYearRepo.saveAndFlush(this.academyYearRepo.save(academyYear)));
        }

    }

}
