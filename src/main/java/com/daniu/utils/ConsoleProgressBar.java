package com.daniu.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ConsoleProgressBar {

    // 更新进度条
    public static void updateProgressBar(int current, int total) {
        double percentage = (double) current / total;
        int progress = (int) (percentage * 100);

        // 擦除之前的进度条和百分比
        System.out.print("\rProgress: [");
        drawProgressBar(progress, 100);
        System.out.printf("] %d%%", progress);
    }

    // 绘制进度条
    public static void drawProgressBar(int progress, int total) {
        int numOfBars = 50;
        int numCompleted = (int) (((double) progress / total) * numOfBars);

        for (int i = 0; i < numOfBars; i++) {
            if (i < numCompleted) {
                System.out.print("=");
            } else {
                System.out.print(" ");
            }
        }
    }

}

