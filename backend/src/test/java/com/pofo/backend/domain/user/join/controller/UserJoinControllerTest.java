package com.pofo.backend.domain.user.join.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.*;

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
    @DisplayName("1.  회원가입 테스트")
    void userJoinSuccessTest() throws Exception {

        ResultActions resultActions = mvc.perform(
                post("/api/v1/user/join")
                        .content("""
                                    {
                                        "provider": "NAVER",
                                        "identify": "123",
                                        "email": "test@test.com",
                                        "name": "testman",
                                        "nickname": "testnickname",
                                        "sex": "MALE",
                                        "age": "1993-03-03"
                                    }
                                """)
                .contentType(MediaType.APPLICATION_JSON)
        );

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains("회원 가입 성공");
    }

    @Test
    @DisplayName("2.  기존 유저에 신규 Oauths 연동 테스트 - provider & idenify는 다르지만, email은 같은 경우")
    void userJoinLinkTest() throws Exception {

        ResultActions resultActions = mvc.perform(
                post("/api/v1/user/join")
                        .content("""
                                    {
                                        "provider": "KAKAO",
                                        "identify": "234",
                                        "email": "test@test.com",
                                        "name": "testman",
                                        "nickname": "testnickname",
                                        "sex": "MALE",
                                        "age": "1993-03-03"
                                    }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains("기존 유저에 새로운 소셜 로그인 연동 성공");
    }

    @Test
    @DisplayName("3.  기존 유저에 신규 Oauths 연동 테스트 - provider & identify 및 email 까지 다른 경우")
    void userJoinLinkTest2() throws Exception {

        ResultActions resultActions = mvc.perform(
                post("/api/v1/user/join")
                        .content("""
                                    {
                                        "provider": "GOOGLE",
                                        "identify": "345",
                                        "email": "test1@test.com",
                                        "name": "testman",
                                        "nickname": "testnickname",
                                        "sex": "MALE",
                                        "age": "1993-03-03"
                                    }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //  여기서는 연동 여부를 물어봐야 하는것이기 때문에 Oauths 테이블에 데이터 저장은 되지 않음.
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains("이전에 이용한 소셜 로그인이 있을 가능성이 있습니다. 연동을 진행하겠습니까? ");
    }

    @Test
    @DisplayName("Uses 테이블에 유사한 정보를 가진 유저 데이터가 다수 존재할 때 - 예외 핸들러 테스트 ")
    void userJoinFailTest() throws Exception {
        //    f/e가 아직 미개발이기 때문에
        //    postman 혹은 테스트 코드 통해서 데이터를 주입 하려 하면 3번 시나리오 때문에 주입이 되지 않기 때문에,
        //    테스트 진행 전 users 테이블에 동일 이름, 성별, 생년월일의 데이터를 수기로 적재 하고 진행 해야 함.

        //  예외 테스트용 실데이터 입력
        ResultActions resultActions = mvc.perform(
                post("/api/v1/user/join")
                        .content("""
                                    {
                                        "provider": "NAVER",
                                        "identify": "789",
                                        "email": "test5@test.com",
                                        "name": "testman",
                                        "nickname": "testnickname",
                                        "sex": "MALE",
                                        "age": "1993-03-03"
                                    }
                                """)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //  에러 핸들러 동작 여부 확인
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains("동일 정보의 계정이 다수 존재합니다.");
    }
}
