package com.pofo.backend.common.security;

import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 테스트에 필요한 빈들(테스트용 컨트롤러, SecurityConfig, 목(mock) 빈들)만 로드하여
 * SecurityConfig의 동작을 검증하는 테스트 예제입니다.
 */
@SpringBootTest(
        classes = {
                SecurityConfigTest.TestController.class,
                SecurityConfig.class,
                SecurityConfigTest.MockConfig.class,
                AdditionalTestConfig.class  // 추가한 설정 클래스
        }
)

@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 테스트용 컨트롤러.
     * – 클래스 레벨에 @RequestMapping("/api/v1")를 지정하여
     *   내부 매핑이 /api/v1/admin/login, /api/v1/test가 되도록 합니다.
     */
    @RestController
    @RequestMapping("/api/v1")
    public static class TestController {
        @GetMapping("/admin/login")
        public String adminLogin() {
            return "Login page";
        }

        @GetMapping("/test")
        public String test() {
            return "Test endpoint";
        }
    }

    /**
     * 테스트 전용 설정: 테스트 컨텍스트에 필요한 목(mock) 빈들을 등록합니다.
     */
    @TestConfiguration
    public static class MockConfig {
        @Bean
        public TokenProvider tokenProvider() {
            return Mockito.mock(TokenProvider.class);
        }

        @Bean
        public RedisTemplate<String, String> redisTemplate() {
            return Mockito.mock(RedisTemplate.class);
        }

        @Bean
        public JwtSecurityConfig jwtSecurityConfig() throws Exception {
            JwtSecurityConfig config = Mockito.mock(JwtSecurityConfig.class);
            // SecurityConfig에서 jwtSecurityConfig.configure(http)를 호출할 때 아무런 동작도 하지 않도록 합니다.
            Mockito.doNothing().when(config).configure(Mockito.any());
            return config;
        }
    }

    /**
     * permitAll로 지정한 로그인 엔드포인트는 인증 없이도 접근되어 200 OK와 "Login page"를 반환해야 합니다.
     */
    @Test
    public void givenPermitAllEndpoint_whenAccessed_thenOk() throws Exception {
        mockMvc.perform(get("/api/v1/admin/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login page"));
    }

    /**
     * 인증 없이 보호된 엔드포인트에 접근 시(기본 설정에 따라) 403 Forbidden이 반환되는지 테스트합니다.
     * (AuthenticationEntryPoint를 별도로 설정하지 않으면 미인증 요청은 403 Forbidden이 될 수 있습니다.)
     */
    @Test
    public void givenProtectedEndpoint_whenNotAuthenticated_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/test"))
                .andExpect(status().isForbidden());
    }

    /**
     * 인증된 사용자로 보호된 엔드포인트에 접근하면 200 OK와 "Test endpoint"가 반환되어야 합니다.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void givenProtectedEndpoint_whenAuthenticated_thenOk() throws Exception {
        mockMvc.perform(get("/api/v1/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test endpoint"));
    }
}
