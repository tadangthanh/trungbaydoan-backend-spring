package vnua.edu.xdptpm09.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor

public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    private final UserHandshakeHandler userHandshakeHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    @Value("${spring.allowed.origin}")
    private String allowedOrigin;

    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setHandshakeHandler(this.userHandshakeHandler).setAllowedOrigins(this.allowedOrigin).withSockJS().setSuppressCors(true).setInterceptors(webSocketAuthInterceptor);
    }

}
