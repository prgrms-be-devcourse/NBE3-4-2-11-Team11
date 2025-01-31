package com.pofo.backend.domain.user.join.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class UserJoinControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("회원가입 테스트")
    void userJoinSuccessTest() throws Exception {

        ResultActions resultActions = mvc.perform(
                post("/api/v1/user/join")
                        .content("""
                                    {
                                        "provider": "KAKAO",
                                        "identify": "21MK6tmNTPQtX2SUtAnvoJX0gtZrG9nCtrQ1nDqUGuDSI",
                                        "email": "testuser312@example.com",
                                        "name": "테스트 사용자312",
                                        "nickname": "testnickname312",
                                        "sex": "FEMALE",
                                        "age": "1993-03-03"
                                    }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(handler().handlerType(UserJoinController.class))
                .andExpect(status().isOk());
    }
}
