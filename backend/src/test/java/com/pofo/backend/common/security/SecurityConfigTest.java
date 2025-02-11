//package com.pofo.backend.common.security;
//
//import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
//import com.pofo.backend.common.security.jwt.TokenProvider;
//import com.pofo.backend.common.service.CustomUserDetailsService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
//@DisplayName("SecurityConfig 테스트") // ✅ 클래스 레벨에서 디스플레이 네임 지정
//class SecurityConfigTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private TokenProvider tokenProvider;
//
//    @MockBean
//    private RedisTemplate<String, String> redisTemplate;
//
//    @MockBean
//    private JwtSecurityConfig jwtSecurityConfig;
//
//    @MockBean
//    private AdminDetailsService adminDetailsService;
//
//    @InjectMocks
//    private SecurityConfig securityConfig;
//
//    @Mock
//    private AuthenticationConfiguration authenticationConfiguration;
//
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @MockBean
//    private CustomUserDetailsService customUserDetailsService;
//
//    @BeforeEach
//    void setUp() {
//        passwordEncoder = securityConfig.passwordEncoder();
//    }
//
//    @Test
//    @DisplayName("비밀번호 암호화가 정상적으로 동작해야 한다")
//    void passwordEncoder_ShouldEncryptPassword() {
//        String rawPassword = "testPassword";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        assertThat(encodedPassword).isNotEqualTo(rawPassword); // 암호화가 정상적으로 수행됨
//        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue(); // 해싱된 비밀번호가 원본과 일치하는지 확인
//    }
//
//    @Test
//    @DisplayName("AuthenticationManager가 정상적으로 반환되어야 한다")
//    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
//        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
//        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
//
//        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);
//        assertThat(result).isNotNull();
//        assertThat(result).isEqualTo(authenticationManager);
//    }
//
//    @Test
//    @DisplayName("AuthenticationProvider가 올바르게 설정되어야 한다")
//    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
//        //AuthenticationProvider authenticationProvider = securityConfig.authenticationProvider();
//        AuthenticationProvider authenticationProvider = securityConfig.authenticationProvider(customUserDetailsService);
//
//        assertThat(authenticationProvider).isNotNull();
//        assertThat(authenticationProvider).isInstanceOf(AuthenticationProvider.class);
//    }
//
//
//
//}
