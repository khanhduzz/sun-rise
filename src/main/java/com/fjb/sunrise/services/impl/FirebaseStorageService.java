package com.fjb.sunrise.services.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



@Service
public class FirebaseStorageService {

    private final Storage storage;
    private static final Logger logger = LoggerFactory.getLogger(FirebaseStorageService.class);

    public FirebaseStorageService() throws IOException {
        Credentials credentials = GoogleCredentials.fromStream(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json"))
        );
        storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public String uploadFile(MultipartFile file, Long userId) throws IOException {
        // Generate a unique file name
        String fileName = "avatars/userId_" + userId + "/" + UUID.randomUUID().toString() + "-"
                + file.getOriginalFilename();

        // Ensure the file is not empty
        if (file.isEmpty()) {
            logger.error("File is empty. Upload failed.");
            throw new IOException("Cannot upload an empty file.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            // Create the BlobId and BlobInfo
            BlobId blobId = BlobId.of("sun-rise-4ebbb.appspot.com", fileName); // Replace with your bucket name
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

            // Upload the file to Firebase Storage
            storage.create(blobInfo, inputStream);
            logger.info("File uploaded to Firebase Storage: {}", fileName);
        }

        // Return the publicly accessible URL
        return String.format("https://storage.googleapis.com/%s/%s", "sun-rise-4ebbb.appspot.com", fileName);  // Correct URL
    }
}
