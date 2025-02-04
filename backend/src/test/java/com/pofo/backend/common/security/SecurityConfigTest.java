package com.pofo.backend.common.security;

import com.pofo.backend.common.security.jwt.JwtSecurityConfig;
import com.pofo.backend.common.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SecurityConfig의 동작을 검증하는 통합 테스트 예제
 */
@SpringBootTest(
        classes = {
                SecurityConfig.class,
                SecurityConfigTest.TestController.class,
                SecurityConfigTest.MockConfig.class,
                AdditionalTestConfig.class   // 추가!
        }
)
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 테스트용 컨트롤러.
     * – /api/v1/admin/login, /api/v1/user/login: permitAll 처리된 엔드포인트
     * – /api/v1/protected: 인증이 필요한 보호된 엔드포인트
     */
    @RestController
    @RequestMapping("/api/v1")
    public static class TestController {

        @PostMapping("/admin/login")
        public String adminLogin() {
            return "Admin login";
        }

        @PostMapping("/user/login")
        public String userLogin() {
            return "User login";
        }

        @GetMapping("/protected")
        public String protectedEndpoint() {
            return "Protected content";
        }
    }

    /**
     * 테스트 전용 설정: SecurityConfig에서 요구하는 빈들을 Mock으로 등록합니다.
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
            // SecurityConfig에서 jwtSecurityConfig.configure(http)를 호출할 때 아무런 동작도 하지 않도록 설정합니다.
            Mockito.doNothing().when(config).configure(Mockito.any());
            return config;
        }
    }

    /**
     * permitAll로 지정한 로그인 엔드포인트는 인증 없이 접근 가능해야 합니다.
     */
    @Test
    public void whenAccessLoginEndpointsWithoutAuth_thenOk() throws Exception {
        // /api/v1/admin/login 테스트
        mockMvc.perform(post("/api/v1/admin/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin login"));

        // /api/v1/user/login 테스트
        mockMvc.perform(post("/api/v1/user/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("User login"));
    }

    /**
     * 보호된 엔드포인트에 인증 없이 접근하면 인증 실패 응답(예: 401 Unauthorized 또는 403 Forbidden)이 반환되어야 합니다.
     */
    @Test
    public void whenAccessProtectedWithoutAuth_thenUnauthorizedOrForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/protected"))
                .andExpect(status().isForbidden());
    }


    /**
     * 인증된 사용자로 보호된 엔드포인트에 접근하면 정상적으로 접근되어야 합니다.
     */
    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    public void whenAccessProtectedWithAuth_thenOk() throws Exception {
        mockMvc.perform(get("/api/v1/protected"))
                .andExpect(status().isOk())
                .andExpect(content().string("Protected content"));
    }
}
