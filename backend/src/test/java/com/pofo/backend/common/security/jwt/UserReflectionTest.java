package com.pofo.backend.common.security.jwt;

import com.pofo.backend.domain.user.join.entity.User;
import java.lang.reflect.Constructor;

public class UserReflectionTest {
    public static void main(String[] args) {
        try {
            // User 클래스의 Constructor 객체를 가져옵니다.
            Constructor<User> constructor = User.class.getDeclaredConstructor();

            // 생성자가 protected로 선언되어 있으므로 접근을 허용합니다.
            constructor.setAccessible(true);

            // User 인스턴스를 생성합니다.
            User user = constructor.newInstance();

            // 생성된 인스턴스를 사용하여 필요한 작업을 수행합니다.
            System.out.println("User 인스턴스가 성공적으로 생성되었습니다: " + user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
