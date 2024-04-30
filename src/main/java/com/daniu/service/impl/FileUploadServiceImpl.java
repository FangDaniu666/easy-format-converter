package com.daniu.service.impl;

import com.daniu.service.FileUploadService;
import com.daniu.utils.ConsoleProgressBar;
import com.daniu.utils.FFmpegUtils;
import com.daniu.utils.FileNameUtils;
import com.daniu.utils.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.daniu.constant.Constant.TEMP_PATH;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FFmpegUtils ffmpegUtils;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    private boolean isSuccess;

    @Override
    public String uploadFile(MultipartFile file, String type, String fileType, String fileOriginalFilename) throws IOException {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(fileOriginalFilename, emitter);
        isSuccess = false;

        String savedFilePath = FileUploader.saveMultipartFileToLocalFile(file, TEMP_PATH, fileOriginalFilename);
        String removedFileExtension = FileNameUtils.removeFileExtension(fileOriginalFilename);

        CompletableFuture.supplyAsync(() -> {
            try {
                return ffmpegUtils.formatConversion(removedFileExtension, savedFilePath, type, fileType,
                        progress -> {
                            try {
                                // System.out.println("你好" + progress);
                                if (!(emitters.size() > 1)) {
                                    ConsoleProgressBar.updateProgressBar(progress, 100);
                                }
                                if (progress == 100 && !isSuccess) {
                                    System.out.println();
                                    log.info("Progress complete!");
                                    isSuccess = true;
                                }

                                if (progress == -1) {
                                    System.out.println();
                                    log.info("Progress failed!");
                                    isSuccess = false;
                                }
                                emitter.send(SseEmitter.event().data(progress)); // 发送进度更新
                            } catch (IOException e) {
                                log.error("向 SSE 发送消息时出现错误", e);
                                emitter.completeWithError(e); // 发送错误消息并完成 SSE
                            }
                        });
            } catch (Exception e) {
                log.error("转换过程中出现错误", e);
                return null;
            }
        }).thenAccept(output -> {
            if (output != null && isSuccess) {
                completeEmitter(emitter, "文件保存为：" + output, fileOriginalFilename);
            } else {
                completeEmitter(emitter, "转换失败", fileOriginalFilename);
            }
        });
        return "OK";
    }

    @Override
    public SseEmitter getProgressEmitter(String emitterId) {
        SseEmitter emitter = emitters.get(emitterId);
        if (emitter == null) {
            emitter = createErrorMessageEmitter("无效的进度查询", emitterId);
        }
        return emitter;
    }

    private void completeEmitter(SseEmitter emitter, String message, String emitterId) {
        try {
            emitter.send(SseEmitter.event().data(message));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        } finally {
            emitters.remove(emitterId);
        }
    }

    private SseEmitter createErrorMessageEmitter(String message, String emitterId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        completeEmitter(emitter, message, emitterId);
        return emitter;
    }

}
