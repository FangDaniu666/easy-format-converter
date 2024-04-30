package com.daniu.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileNameUtils {

    public static String removeFileExtension(String fileName) {
        // 查找最后一个点的索引位置
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex == -1) {
            // 如果找不到点，则返回原始文件名
            return fileName;
        }
        return fileName.substring(0, extensionIndex);
    }

}
