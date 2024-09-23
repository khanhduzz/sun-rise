package com.fjb.sunrise.config.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;



@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("sun-rise-4ebbb.appspot.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        } else {
            FirebaseApp.getApps();
        }
    }
}