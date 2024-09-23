package com.fjb.sunrise.config.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        // Đọc file JSON cấu hình đã tải về từ Firebase Console
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

        // Cấu hình Firebase với bucket lưu trữ
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("sun-rise-4ebbb.appspot.com") // Chỉ sử dụng tên bucket, không có 'gs://'
                .build();

        // Khởi tạo FirebaseApp
        FirebaseApp.initializeApp(options);
    }
}