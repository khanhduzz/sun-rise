package com.fjb.sunrise.controllers;

import com.fjb.sunrise.services.StorageService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class FileUploadController {

    private final StorageService storageService;

    @GetMapping
    public ModelAndView listUploadedFiles() {
        ModelAndView modelAndView = new ModelAndView("images/index");
        modelAndView.addObject("files", storageService.loadAll()
            .map(path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile",
                path.getFileName().toString()).build().toUri().toString()).collect(Collectors.toList()));
        return modelAndView;
    }

    @GetMapping("files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file,
                                         RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "Uploaded " + file.getOriginalFilename());
        modelAndView.setViewName("redirect:/images");
        return modelAndView;
    }
}
