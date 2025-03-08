package com.pofo.backend.common.security;

import static org.springframework.security.config.Customizer.withDefaults;

import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.OAuth2AuthenticationSuccessHandler;
import com.pofo.backend.common.security.jwt.TokenProvider;
import com.pofo.backend.common.service.CustomUserDetailsService;
import com.pofo.backend.domain.user.login.service.CustomOAuth2UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtSecurityConfig jwtSecurityConfig;
    private final AdminDetailsService adminDetailsService;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 관리자용 SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain adminSecurityFilterChain(
            HttpSecurity http,
            AuthenticationProvider adminAuthenticationProvider
    ) throws Exception {
        jwtSecurityConfig.configure(http);

        http
                .securityMatcher("/api/v1/admin/**", "/api/v1/token/**","/api/v1/common/**")
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin/login","/api/v1/token/refresh").permitAll()
                        .requestMatchers("/api/v1/admin/me").authenticated() // ✅ 관리자 정보 조회는 인증 필요
                        .anyRequest().authenticated()
                )
                .authenticationProvider(adminAuthenticationProvider); // 관리자 전용 provider 사용


        return http.build();
    }


    /**
     * 유저용 SecurityFilterChain - '/api/v1/user/**' 경로에 대해 별도의 보안 설정 적용
     */
    @Bean
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        jwtSecurityConfig.configure(http);

        http
                // '/api/v1/user/**' 경로에만 적용
                //.cors(withDefaults())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/v1/user/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 유저 로그인, OAuth2 로그인은 인증 없이 접근 가능하도록 허용
                        .requestMatchers(
                                "/api/v1/user/join",
                                "/api/v1/user/join/force",
                                "/api/v1/user/login",
                                "/api/v1/user/last-login-provider",
                                "/api/v1/user/naver/login",
                                "/api/v1/user/naver/login/naver/callback",
                                "/api/v1/user/naver/login/process",
                                "/api/v1/user/kakao/login",
                                "/api/v1/user/kakao/login/kakao/callback",
                                "/api/v1/user/kakao/login/process",
                                "/api/v1/user/google/login",
                                "/api/v1/user/google/login/google/callback",
                                "/api/v1/user/google/login/process",
                                "/api/v1/user/logout",
                                "/api/v1/user/send-verification/**",
                                "/api/v1/user/verify-code",
                                "/api/v1/token/refresh",
                                "/api/v1/user/oauth2/**",
                                "/api/v1/user/mypage",
                                "/api/v1/user/resign"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * 관리자용 DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }



    @Bean

    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider adminProvider = new DaoAuthenticationProvider();
        adminProvider.setUserDetailsService(adminDetailsService);
        adminProvider.setPasswordEncoder(passwordEncoder());

        DaoAuthenticationProvider userProvider = new DaoAuthenticationProvider();
        userProvider.setUserDetailsService(customUserDetailsService);
        userProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(List.of(adminProvider, userProvider));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // ✅ 프론트엔드 주소 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));

        configuration.setExposedHeaders(List.of("Authorization", "Refresh-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}