package com.daniu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodecInfo {
    private String versionInfo;

    private String codecName;

    private String description;
}
