package com.fjb.sunrise.services.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service  // Annotation này giúp Spring tạo bean của FirebaseStorageService
public class FirebaseStorageService {

    private final Storage storage;

    public FirebaseStorageService() throws IOException {
        // Lấy thông tin xác thực từ Firebase và khởi tạo Storage client
        Credentials credentials = GoogleCredentials.fromStream(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json"))
        );
        storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        // Tạo tên file với định dạng userId và tên file gốc
        String fileName = "avatars/userId_" + userId + "/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        BlobId blobId = BlobId.of("sun-rise-4ebbb.appspot.com", fileName); // Thay thế bằng tên bucket của bạn
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        storage.create(blobInfo, inputStream);

        return "https://storage.googleapis.com/sun-rise-4ebbb.appspot.com/" + fileName;  // URL chính xác
    }
}
