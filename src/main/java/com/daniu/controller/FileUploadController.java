package com.daniu.controller;

import com.daniu.entity.UploadForm;
import com.daniu.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @GetMapping("/upload")
    public String uploadForm(Model model) {
        model.addAttribute("uploadForm", new UploadForm());
        model.addAttribute("formattedRatio", "0");
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("type") String type, @RequestParam("fileType") String fileType)
            throws IOException, ExecutionException, InterruptedException {
        if (file.isEmpty()) return "请选择一个文件上传";
        if (type.isBlank()) return "请输入转换后的格式";

        String fileOriginalFilename = file.getOriginalFilename();
        if (fileOriginalFilename != null && fileOriginalFilename.endsWith(type)) {
            return "文件已经为" + type + "格式";
        }

        return fileUploadService.uploadFile(file, type, fileType, fileOriginalFilename, false);
    }

    @GetMapping(value = "/progress/{emitterId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter getProgress(@PathVariable String emitterId) {
        if (emitterId == null) return null;
        return fileUploadService.getProgressEmitter(emitterId);
    }

}

