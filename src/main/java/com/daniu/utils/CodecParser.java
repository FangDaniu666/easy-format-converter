package com.daniu.utils;

import com.daniu.entity.CodecInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class CodecParser {

    public static CodecInfo parseCodecs(String text) {
        String regex = "^\\s*([VASFXBD.]{6})\\s+([\\w-]+)\\s+(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String versionInfo = matcher.group(1); // 第一个捕获组，对应 V..X..
            String codecName = matcher.group(2); // 第二个捕获组，对应 avui
            String description = matcher.group(3); // 第三个捕获组，对应 Avid Meridien Uncompressed

            return new CodecInfo(versionInfo, codecName, description);
        }
        return null;
    }

}


