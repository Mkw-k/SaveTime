package com.save.savetime.util;

import java.io.IOException;

public class BrowserOpener {

    public static void openBrowser(String url) {
        try {
            // 시스템 명령 실행하여 브라우저 열기
            String osName = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder = new ProcessBuilder();

            if (osName.contains("win")) {
                // Windows 환경에서는 기본 브라우저로 URL 열기
                processBuilder.command("cmd.exe", "/c", "start", url);
            } else if (osName.contains("mac")) {
                // macOS 환경에서는 기본 브라우저로 URL 열기
                processBuilder.command("open", url);
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                // Linux/Unix 환경에서는 x-www-browser 명령을 사용하여 브라우저 열기
                processBuilder.command("x-www-browser", url);
            } else {
                System.out.println("Unsupported operating system.");
                return;
            }

            // 명령 실행
            processBuilder.start();
            System.out.println("Attempting to open browser for URL: " + url);
        } catch (IOException e) {
            System.out.println("Failed to open browser: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String url = "https://www.example.com";
        openBrowser(url);
    }
}
