package com.daniu.config;

import com.daniu.entity.CodecInfo;
import com.daniu.utils.CodecParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeansConfig {
    @Bean
    public List<CodecInfo> getCodecList() throws IOException {
        List<CodecInfo> codecList = new ArrayList<>();
        ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
        ffmpeg.addArgument("-encoders");
        ffmpeg.execute();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                CodecInfo codec = CodecParser.parseCodecs(line);
                if (codec != null) {
                    codecList.add(codec);
                }
            }
        }
        ffmpeg.close();
        return codecList;
    }
}
