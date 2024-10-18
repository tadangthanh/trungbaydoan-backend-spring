
package vnua.edu.xdptpm09.service;

public interface IRedisService {
    void saveTokens(String email, String accessToken, String refreshToken);

    boolean isAccessTokenValid(String email, String token);

    boolean isRefreshTokenValid(String email, String refreshToken);

    boolean isBlacklisted(String email, String token);

    void blacklistToken(String email, String refreshToken);

    void deleteTokens(String email);

    void removeTokensBlacklistByEmail(String email);
}
