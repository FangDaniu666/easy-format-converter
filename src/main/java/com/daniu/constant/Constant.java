package com.daniu.constant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Component
@Slf4j
public class Constant {
    private static Properties properties = new Properties();

    // 默认值
    public static String DEFAULT_ASSETS_PATH = System.getProperty("user.dir");
    public static String DEFAULT_TEMP_PATH = System.getProperty("java.io.tmpdir");

    private static final String CONFIG_PATH = DEFAULT_ASSETS_PATH + "/config/config.properties";

    static {
        try {
            // 加载配置文件
            FileInputStream fis = new FileInputStream(CONFIG_PATH);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            log.warn("Failed to load config.properties. Using default configuration...");
            generateConfigFromTemplate();
        }
    }

    // 从配置文件中获取或使用默认值
    public static String ASSETS_PATH = getPropertyValue("assets.path", DEFAULT_ASSETS_PATH);
    public static String TEMP_PATH = getPropertyValue("temp.path", DEFAULT_TEMP_PATH) + "/format-converter";

    // 从 properties 中获取属性值，如果为空则使用默认值
    private static String getPropertyValue(String key, String defaultValue) {
        String value = properties.getProperty(key);
        // 如果值为空或未设置，则使用默认值
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static void generateConfigFromTemplate() {
        try {
            InputStream templateStream = Constant.class.getClassLoader().getResourceAsStream("config.properties.template");
            if (templateStream == null) {
                throw new FileNotFoundException("Settings template not found");
            }

            File configDir = new File(DEFAULT_ASSETS_PATH + "/config");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(CONFIG_PATH);
            if (configFile.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(configFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = templateStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                fos.close();
            }

            FileInputStream fis = new FileInputStream(configFile);
            properties.load(fis);
            fis.close();

            templateStream.close();
        } catch (IOException e) {
            log.error("Failed to generate config.properties from template: {}", e.getMessage());
        }
    }

}

