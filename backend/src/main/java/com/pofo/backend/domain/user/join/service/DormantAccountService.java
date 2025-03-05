package com.pofo.backend.domain.user.join.service;


import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DormantAccountService {

    private final UserRepository userRepository;

//    // 테스트용: 매 1분마다 실행하며, 마지막 로그인 시간이 1분 전인 사용자를 휴먼 계정으로 전환
//    @Scheduled(cron = "0 */1 * * * ?")

    // 하루에 한번 자정에 실행: 마지막 로그인 시간이 1년 전인 사용자를 휴먼 계정으로 전환
    @Scheduled(cron = "0 0 0 * * ?")
    public void markDormantAccounts() {
//        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);
        LocalDateTime threshold = LocalDateTime.now().minusYears(1);
        List<User> inactiveUsers = userRepository.findByLastLoginAtBeforeAndDormantFlg(threshold, "N");

        for (User user : inactiveUsers) {
            user.setDormantFlg("Y");
            user.setDormantStartAt(LocalDateTime.now());
            // 휴먼 상태로 전환할 때, 기존의 휴먼 처리 종료 시간이 있으면 초기화
            user.setDormantEndAt(null);
            userRepository.save(user);
        }
    }
}


