//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.entity.User;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String SECRET = "daotao.vnua.edu.vn";
    private static final long EXPIRATION_TIME = 3600000L;
    private static final long EXPIRATION_TIME_REFRESH = 2592000000L;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
        String role = user.getRole().getName();
        return JWT.create().withSubject(user.getEmail()).withClaim("role", role).withClaim("id", user.getId()).withExpiresAt(new Date(System.currentTimeMillis() + 3600000L)).sign(algorithm);
    }

    public String generateRefreshToken(User user, HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
        String role = user.getRole().getName();
        return JWT.create().withSubject(user.getEmail()).withClaim("role", role).withClaim("ip", request.getRemoteAddr()).withExpiresAt(new Date(System.currentTimeMillis() + 2592000000L)).sign(algorithm);
    }

    public String extractEmail(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
            return JWT.require(algorithm).build().verify(token).getSubject();
        } catch (Exception var3) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
            return JWT.require(algorithm).build().verify(token).getClaim("role").asString();
        } catch (Exception var3) {
            return null;
        }
    }

    public boolean tokenIsValid(String token) {
        Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());

        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception var4) {
            return false;
        }
    }

    public long getExpirationTimeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt == null) {
                throw new RuntimeException("Token không hợp lệ: không có thời gian hết hạn.");
            } else {
                return expiresAt.getTime();
            }
        } catch (Exception var5) {
            Exception e = var5;
            throw new RuntimeException("Token không hợp lệ: " + e.getMessage(), e);
        }
    }

    public Map<String, Claim> extractAllClaims(String token) {
        Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
        return JWT.require(algorithm).build().verify(token).getClaims();
    }

    public String extractIp(String token) {
        Algorithm algorithm = Algorithm.HMAC256("daotao.vnua.edu.vn".getBytes());
        return JWT.require(algorithm).build().verify(token).getClaim("ip").asString();
    }

    public String generateVerifyCode() {
        Random random = new Random();
        int verifyCode = 100000 + random.nextInt(900000);
        return String.valueOf(verifyCode);
    }


}
