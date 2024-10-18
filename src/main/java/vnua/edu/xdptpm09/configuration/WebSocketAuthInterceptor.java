package vnua.edu.xdptpm09.configuration;

import lombok.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import vnua.edu.xdptpm09.service.IRedisService;
import vnua.edu.xdptpm09.service.impl.JwtService;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;
    private final IRedisService redisService;

    public WebSocketAuthInterceptor(final JwtService jwtService, final IRedisService redisService) {
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        List<String> queryParams = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().get("token");
        String jwtToken = (queryParams != null && !queryParams.isEmpty()) ? queryParams.get(0) : null;

        if (jwtToken == null) return false;

        String email = jwtService.extractEmail(jwtToken);
        if (email != null && jwtService.tokenIsValid(jwtToken) && redisService.isAccessTokenValid(email, jwtToken)) {
            attributes.put("email", email);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {

    }
}
