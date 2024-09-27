package com.fjb.sunrise.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fjb.sunrise.exceptions.BadRequestException;
import com.fjb.sunrise.models.Media;
import com.fjb.sunrise.repositories.MediaRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaService mediaService;

    private Media mockMedia;
    private MultipartFile mockFile;

    void mockFile() throws IOException {
        mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("testfile.jpg");
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getBytes()).thenReturn("mock data".getBytes());
    }

    void initData() {
        mockMedia = Media.builder()
            .id(1L)
            .name("testfile.jpg")
            .type("image/jpeg")
            .fileCode(UUID.randomUUID().toString())
            .data("mock data".getBytes())
            .build();
    }

    @Test
    void testStore() throws IOException {
        mockFile();
        initData();
        when(mediaRepository.save(any(Media.class))).thenReturn(mockMedia);

        Media savedMedia = mediaService.store(mockFile);

        assertNotNull(savedMedia);
        assertEquals("testfile.jpg", savedMedia.getName());
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    @Test
    void testStore_whenFileIsNull_thenThrowException() throws IOException {
        mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(null);

        assertThrows(BadRequestException.class, () -> mediaService.store(mockFile));
    }

    @Test
    void testGetMedia() {
        initData();
        when(mediaRepository.findByFileCode(any(String.class))).thenReturn(mockMedia);
        String uuid = UUID.randomUUID().toString();

        Media retrievedMedia = mediaService.getMedia(uuid);

        assertNotNull(retrievedMedia);
        assertEquals(mockMedia.getName(), retrievedMedia.getName());
        verify(mediaRepository, times(1)).findByFileCode(uuid);
    }

    @Test
    void testGetAllMedias() {
        initData();
        when(mediaRepository.findAll()).thenReturn(Collections.singletonList(mockMedia));

        Stream<Media> allMedias = mediaService.getAllMedias();

        assertNotNull(allMedias);
        assertEquals(1, allMedias.count());
        verify(mediaRepository, times(1)).findAll();
    }

    @Test
    void testStoreWithLenient() throws IOException {
        mockFile();
        initData();
        when(mediaRepository.save(any(Media.class))).thenReturn(mockMedia);

        Media savedMedia = mediaService.store(mockFile);

        assertNotNull(savedMedia);
        assertEquals("testfile.jpg", savedMedia.getName());
    }

    @Test
    void testGetMediaWithLenient() {
        initData();
        when(mediaRepository.findByFileCode(any(String.class))).thenReturn(mockMedia);

        Media retrievedMedia = mediaService.getMedia(mockMedia.getFileCode());

        assertNotNull(retrievedMedia);
        assertEquals(mockMedia.getName(), retrievedMedia.getName());
    }
}
