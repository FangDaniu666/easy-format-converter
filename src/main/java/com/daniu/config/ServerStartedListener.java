package com.daniu.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
public class ServerStartedListener implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String host;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host = "localhost";
        }
         String url = String.format("http://%s:%d", host, port) + "/upload";
        String localUrl = String.format("http://%s:%d", "localhost", port) + "/upload";

        log.info("Service started. Access URL: {}; {}", localUrl, url);

        // 自动跳转到服务地址
        try {
            redirectToUrl(localUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void redirectToUrl(String url) throws IOException {
        // 在 Windows
        Runtime.getRuntime().exec("cmd /c start " + url);
        // 在 macOS  Runtime.getRuntime().exec("open " + url);
        // 在 Linux  Runtime.getRuntime().exec("xdg-open " + url);
    }
}
