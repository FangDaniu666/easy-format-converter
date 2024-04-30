package com.daniu.entity;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadForm {
    private MultipartFile file;

    private String type;

    private String fileType;
}

