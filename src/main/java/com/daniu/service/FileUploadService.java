package com.daniu.service;

import com.daniu.entity.UploadForm;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface FileUploadService {

    String uploadFile(UploadForm uploadForm, String fileOriginalFilename, boolean isBlocked) throws IOException, ExecutionException, InterruptedException;

    SseEmitter getProgressEmitter(String emitterId);
}
