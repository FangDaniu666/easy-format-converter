package com.daniu.utils;

import com.daniu.entity.CodecInfo;
import com.daniu.intfs.RatioCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.daniu.constant.Constant.ASSETS_PATH;

@Component
@Slf4j
@RequiredArgsConstructor
public class FFmpegUtils {

    private final List<CodecInfo> codecList;

    public String formatConversion(String fileName, String inputFilePath, String format, String fileType, RatioCallback callback) throws IOException, InterruptedException, ExecutionException {
        String path = ASSETS_PATH + "/" + fileType + "/";
        File directory = new File(path);
        if (!directory.exists()) {
            boolean mkdirs = directory.mkdirs();
            if (!mkdirs) {
                throw new IOException("Failed to create directory: " + directory);
            }
        }
        String outputFilePath = path + fileName + "." + format;
        Files.deleteIfExists(Path.of(outputFilePath));

        ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();

        return getErrorStreamString(inputFilePath, outputFilePath, ffmpeg, fileType, callback);
    }

    private String getErrorStreamString(String inputFilePath, String outputFilePath, ProcessWrapper ffmpeg, String fileType, RatioCallback callback) throws IOException, InterruptedException, ExecutionException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            setArgument(ffmpeg, inputFilePath, outputFilePath, fileType);
            try {
                ffmpeg.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(br, callback, inputFilePath, outputFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        future.get();
        ffmpeg.close();
        Files.deleteIfExists(Path.of(inputFilePath));
        return outputFilePath;
    }

    private void blockFfmpeg(BufferedReader br, RatioCallback callback, String inputFilePath, String outputFilePath) throws IOException {
        String line;
        double duration = 0;

        while ((line = br.readLine()) != null) {
            // System.out.println(fileName + "你好："+line);
            if (line.equals("Conversion failed!")) {
                callback.onRatioUpdated(-1);
                Files.deleteIfExists(Path.of(inputFilePath));
                Files.deleteIfExists(Path.of(outputFilePath));
            }

            double durationInSeconds = LogDurationParser.parseDurationInSeconds(line);
            if (durationInSeconds != -1) {
                duration = durationInSeconds;
            }

            double timeInSeconds = LogDurationParser.parseTimeInSeconds(line);
            if (timeInSeconds != -1) {
                double ratio = (timeInSeconds / duration) * 100;
                int currentRatio = (int) Math.round(ratio);

                // 每次计算出新的 formattedRatio 后，调用回调函数通知 Controller
                callback.onRatioUpdated(currentRatio);
            }
        }
    }

    private void setArgument(ProcessWrapper ffmpeg, String inputFilePath, String outputFilePath, String fileType) {
        ffmpeg.addArgument("-i");
        ffmpeg.addArgument(inputFilePath);
        if (fileType.equals("video")) {
            ffmpeg.addArgument("-c:v");
            setVideoCodec(ffmpeg);
            /*ffmpeg.addArgument("-crf");
            ffmpeg.addArgument("19");*/
            ffmpeg.addArgument("-strict");
            ffmpeg.addArgument("experimental");
        } else if (fileType.equals("audio")) {
            /*ffmpeg.addArgument("-c:a");
            ffmpeg.addArgument("libmp3lame");*/
            /*ffmpeg.addArgument("-q:a");
            ffmpeg.addArgument("4");*/
        } else {
            ffmpeg.close();
        }
        ffmpeg.addArgument(outputFilePath);
    }

    private void setVideoCodec(ProcessWrapper ffmpeg) {
        Optional<CodecInfo> nvencCodec = getCodecByName("h264_nvenc");
        if (nvencCodec.isPresent()) {
            ffmpeg.addArgument("h264_nvenc");
            ffmpeg.addArgument("-preset");
            ffmpeg.addArgument("fast");
            log.info(nvencCodec.get().toString());
        } else {
            Optional<CodecInfo> amfCodec = getCodecByName("h264_amf");
            if (amfCodec.isPresent()) {
                ffmpeg.addArgument("h264_amf");
                ffmpeg.addArgument("-preset");
                ffmpeg.addArgument("fast");
                log.info(amfCodec.get().toString());
            } else {
                ffmpeg.addArgument("libx264");
                log.warn("Neither h264_nvenc nor h264_amf was found. Falling back to libx264.");
            }
        }
    }

    public Optional<CodecInfo> getCodecByName(String codecName) {
        return codecList.stream()
                .filter(codec -> codecName.equals(codec.getCodecName()))
                .findFirst();
    }

}
