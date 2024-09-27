package com.fjb.sunrise.controllers;

import com.fjb.sunrise.dtos.responses.MediaResponse;
import com.fjb.sunrise.exceptions.BadRequestException;
import com.fjb.sunrise.models.Media;
import com.fjb.sunrise.services.MediaService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/medias")
public class MediaController {

    private final MediaService mediaService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("media/index");
    }

    @PostMapping("/upload")
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            mediaService.store(file);
        } catch (Exception ignored) {
            throw new BadRequestException("Error when upload file");
        }
        return new ModelAndView("redirect:/medias");
    }

    @GetMapping("/files")
    public ModelAndView getListFiles() {
        List<MediaResponse> files = mediaService.getAllMedias().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/medias/files/")
                .path(dbFile.getFileCode())
                .toUriString();

            return new MediaResponse(
                dbFile.getName(),
                dbFile.getFileCode(),
                fileDownloadUri,
                dbFile.getType(),
                dbFile.getData().length);
        }).toList();

        ModelAndView modelAndView = new ModelAndView("media/index");

        modelAndView.addObject("files", files);
        return modelAndView;
    }

    @GetMapping("/files/{fileCode}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileCode) {
        Media fileDB = mediaService.getMedia(fileCode);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
            .body(fileDB.getData());
    }

    @GetMapping("/media/{fileCode}")
    public ResponseEntity<ByteArrayResource> getMedia(@PathVariable String fileCode) {
        Media media = mediaService.getMedia(fileCode);
        if (media == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ByteArrayResource resource = new ByteArrayResource(media.getData());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.getName() + "\"")
            .contentType(MediaType.parseMediaType(media.getType()))
            .contentLength(media.getData().length)
            .body(resource);
    }
}
