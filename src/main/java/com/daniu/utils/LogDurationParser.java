package com.daniu.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class LogDurationParser {

    private static final Pattern DURATION_PATTERN = Pattern.compile("Duration: (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2})");

    /**
     * 解析日志中的持续时间并将其转换为秒数（包括毫秒）。
     * 如果日志行中包含有效的持续时间字符串，则返回对应的秒数；否则返回 -1。
     *
     * @param logLine 日志行
     * @return 持续时间的总秒数（包括毫秒），如果解析失败则返回 -1
     */
    public static double parseDurationInSeconds(String logLine) {
        Matcher matcher = DURATION_PATTERN.matcher(logLine);
        return matcherTime(matcher);
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("time=(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2})");

    /**
     * 解析日志中的时间并将其转换为秒数（包括毫秒）。
     * 如果日志行中包含有效的时间字符串，则返回对应的秒数；否则返回 -1。
     *
     * @param logLine 日志行
     * @return 时间的总秒数（包括毫秒），如果解析失败则返回 -1
     */
    public static double parseTimeInSeconds(String logLine) {
        Matcher matcher = TIME_PATTERN.matcher(logLine);
        return matcherTime(matcher);
    }

    private static double matcherTime(Matcher matcher) {
        if (matcher.find()) {
            try {
                int hours = Integer.parseInt(matcher.group(1));
                int minutes = Integer.parseInt(matcher.group(2));
                int seconds = Integer.parseInt(matcher.group(3));
                int milliseconds = Integer.parseInt(matcher.group(4));

                int totalSeconds = hours * 3600 + minutes * 60 + seconds;
                return totalSeconds + milliseconds / 100.0;
            } catch (NumberFormatException e) {
                // 解析失败，返回 -1
                return -1;
            }
        } else {
            // 没有找到有效的时间字符串
            return -1;
        }
    }

}

