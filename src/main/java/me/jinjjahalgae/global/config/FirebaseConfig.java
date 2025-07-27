package me.jinjjahalgae.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
    private final ResourceLoader resourceLoader;

    @Value("${firebase.key-path}")
    private String firebaseKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            Resource resource = resourceLoader.getResource(firebaseKeyPath);
            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);

                log.info("파이어베이스 초기화 성공. secret key 경로: {}", firebaseKeyPath);
            }
        } catch (IOException e) {
            log.error("파이어베이스 초기화 실패함. secret key 경로:" + firebaseKeyPath, e);
        }
    }
}