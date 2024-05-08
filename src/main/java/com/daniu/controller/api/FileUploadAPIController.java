package com.daniu.controller.api;

import com.daniu.entity.UploadForm;
import com.daniu.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "格式转换")
public class FileUploadAPIController {

    private final FileUploadService fileUploadService;

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "文件上传", description = " file为输入文件,type为输入格式,fileType文件为音频(audio)或视频(video)")
    public void filesUpload(@ModelAttribute UploadForm uploadForm, HttpServletResponse response)
            throws IOException, ExecutionException, InterruptedException {
        MultipartFile file = uploadForm.getFile();
        String type = uploadForm.getType();
        String fileOriginalFilename = file.getOriginalFilename();

        if (file.isEmpty() || type.isBlank() ||
                (fileOriginalFilename != null && fileOriginalFilename.endsWith(type))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("文件上传失败或不符合要求");
            return;
        }

        String filePath = fileUploadService.uploadFile(uploadForm, fileOriginalFilename, true);

        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        response.setContentType("application/octet-stream");
        response.setContentLength(fileBytes.length);

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(fileBytes);
        outputStream.close();

    }

}
