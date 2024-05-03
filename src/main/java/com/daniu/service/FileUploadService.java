package com.daniu.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String type, String fileType, String fileOriginalFilename, boolean isBlocked) throws IOException, ExecutionException, InterruptedException;

    SseEmitter getProgressEmitter(String emitterId);
}
