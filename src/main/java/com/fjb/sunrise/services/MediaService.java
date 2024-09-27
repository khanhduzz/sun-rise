package com.fjb.sunrise.services;

import com.fjb.sunrise.exceptions.BadRequestException;
import com.fjb.sunrise.models.Media;
import com.fjb.sunrise.repositories.MediaRepository;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaService {

    private final MediaRepository mediaRepository;

    @Transactional
    public Media store(MultipartFile file){
        if (file == null || file.getOriginalFilename() == null) {
            throw new BadRequestException("File is null");
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileCode = UUID.randomUUID().toString();
        Media media;
        try {
            media = Media.builder()
                .name(fileName)
                .type(file.getContentType())
                .data(file.getBytes())
                .fileCode(fileCode)
                .build();
        } catch (IOException e) {
            throw new BadRequestException("Error when save file");
        }

        return mediaRepository.save(media);
    }

    @Transactional
    public Media getMedia(String fileCode) {
        return mediaRepository.findByFileCode(fileCode);
    }

    @Transactional
    public Stream<Media> getAllMedias() {
        return mediaRepository.findAll().stream();
    }
}
