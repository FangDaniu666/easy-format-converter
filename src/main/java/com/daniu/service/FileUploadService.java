package com.daniu.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String type, String fileType, String fileOriginalFilename) throws IOException;

    SseEmitter getProgressEmitter(String emitterId);
}
