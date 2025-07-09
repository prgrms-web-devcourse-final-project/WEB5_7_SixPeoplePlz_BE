package me.jinjjahalgae.global.init;

import lombok.RequiredArgsConstructor;
import me.jinjjahalgae.domain.user.User;
import me.jinjjahalgae.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Profile("dev") // 'dev' 프로필에서만 실행되도록 설정
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // JWT 테스트용
        Optional<User> user = userRepository.findById(1L);

        if(user.isEmpty()){
            userRepository.save(User.builder()
                    .name("테스트")
                    .nickname("테스트닉네임")
                    .email("test@test.com")
                    .build()
            );
        }
    }
}