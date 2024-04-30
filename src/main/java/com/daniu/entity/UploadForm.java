package com.daniu.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadForm {
    private MultipartFile file;

    private String type;

    private String fileType;
}

