package com.fjb.sunrise.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fjb.sunrise.exceptions.BadRequestException;
import com.fjb.sunrise.exceptions.NotFoundException;
import com.fjb.sunrise.models.Media;
import com.fjb.sunrise.models.User;
import com.fjb.sunrise.repositories.MediaRepository;
import com.fjb.sunrise.repositories.UserRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MediaService mediaService;

    private Media mockMedia;
    private MultipartFile mockFile;
    private User user;

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

    void initUserData() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        user = new User();
        user.setEmail("test@example.com");
        user.setFileCode("file123");
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
    void testStore_whenFileIsNull_thenThrowException() {
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

    @Test
    public void testGetMediaOfUser_UserExistsWithFileCode() {
        initUserData();
        when(userRepository.findByEmailOrPhone("test@example.com")).thenReturn(user);
        Media expectedMedia = new Media();

        when(mediaService.getMedia(user.getFileCode())).thenReturn(expectedMedia);

        Media actualMedia = mediaService.getMediaOfUser();

        assertNotNull(actualMedia);
        assertEquals(expectedMedia, actualMedia);
        verify(userRepository).findByEmailOrPhone("test@example.com");
    }

    @Test
    public void testGetMediaOfUser_UserExistsWithoutFileCode() {
        initUserData();
        user.setFileCode(null);
        Media media = new Media();
        when(userRepository.findByEmailOrPhone("test@example.com")).thenReturn(user);

        Media actualMedia = mediaService.getMediaOfUser();

        assertNotNull(actualMedia);
    }

    @Test
    public void testGetMediaOfUser_UserNotFound() {
        when(userRepository.findByEmailOrPhone("test@example.com")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> mediaService.getMediaOfUser());
    }
}
