package vnua.edu.xdptpm09.security;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Value("${spring.allowed.origin}")
    private String allowedOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/ws/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((config) ->
            config.requestMatchers("/api/v1/academy-year", "/api/v1/auth/login", "/ws/**",
                            "/api/v1/auth/verify-admin", "/api/v1/users/register", "/api/v1/auth/refresh",
                            "/api/v1/users/email/**", "api/v1/auth/verify", "/api/v1/auth/resend-code",
                            "/api/v1/documents/view/**", "/api/v1/users/avatar/view/**",
                            "api/v1/users/forgot-password", "/api/v1/users/reset-password",
                            "/api/v1/auth/verify-token", "/api/v1/users/email/**",
                            "/api/v1/projects/user/**", "/api/v1/projects/*/mentors",
                            "/api/v1/projects/*/members", "/api/v1/projects/mentor/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/categories", "/api/v1/projects/*/reject", "/api/v1/projects/*/inactivate", "/api/v1/projects/*/activate", "/api/v1/projects/*/approve").hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.POST, "/api/v1/technologies/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/projects/**", "/api/v1/technologies/**",
                            "/api/v1/documents/**", "/api/v1/members/project/**", "/api/v1/categories/**",
                            "/api/v1/comments/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/projects/**", "/api/v1/categories/*").hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.GET,"/api/v1/users").hasRole("ADMIN")
                    .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "TEACHER")
                    .anyRequest().authenticated()
        );
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers((headers) -> {
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
        });
        http.cors((cors) -> {
            cors.configurationSource(this.corsConfigurationSource());
        });
        http.sessionManagement((session) -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return  http.build();
    }

}
