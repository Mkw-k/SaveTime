package com.save.savetime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.*;
import com.save.savetime.model.dto.PlaylistDTO;
import com.save.savetime.model.dto.YouTubeCrollingReqDTO;
import com.save.savetime.model.dto.YouTubeCrollingRespDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class CrollingService {
    public Set<YouTubeCrollingRespDTO> getMyPlayList(YouTubeCrollingReqDTO crollingReqDTO) throws InterruptedException {
        // 크롬 드라이버 설정
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe"); // 윈도우
        // System.setProperty("webdriver.chrome.driver", "./chromedriver"); // 리눅스, 맥

        // 크롬 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        // Upbit 공지사항 페이지 접속
        driver.get("https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fwww.youtube.com%2Fsignin%3Faction_handle_signin%3Dtrue%26app%3Ddesktop%26hl%3Dko%26next%3Dhttps%253A%252F%252Fwww.youtube.com%252F&ec=65620&hl=ko&passive=true&service=youtube&uilel=3&ifkv=ARZ0qKIs85RcwSnDsvJxcMdeTopmZVqh45u0ylZpkp8VChdyD87JOV4sQvjZ6j6Siub8v_B74qiC&theme=mn&ddm=0&flowName=GlifWebSignIn&flowEntry=ServiceLogin");

        // 이메일 입력란에 값 입력
        WebElement emailInput = driver.findElement(By.cssSelector("#identifierId"));
        emailInput.sendKeys(crollingReqDTO.getId()); // crollingReqDTO.getid() 값을 입력

        // 다음 버튼 클릭
        WebElement nextButton1 = driver.findElement(By.cssSelector("#identifierNext > div > button > span"));
        nextButton1.click();
        Thread.sleep(5000); // 5초 대기

        // 비밀번호 입력란에 값 입력
        WebElement passwordInput = driver.findElement(By.cssSelector("#password div input"));
        passwordInput.sendKeys(crollingReqDTO.getPw()); // crollingReqDTO.getPw() 값을 입력

        // 다음 버튼 클릭
        WebElement nextButton2 = driver.findElement(By.cssSelector("#passwordNext > div > button"));
        nextButton2.click();
        Thread.sleep(5000); // 5초 대기
        
        //TODO 여기서 잠시 본인 확인 푸시가 날라갈수 있음

        // #endpoint 요소 클릭
        WebElement endpointLink = driver.findElement(By.cssSelector("#endpoint"));
        endpointLink.click();
        Thread.sleep(2000); // 2초 대기

        // #top-level-buttons-computed > ytd-button-renderer > yt-button-shape > a 요소 클릭
        WebElement buttonElement = driver.findElement(By.cssSelector("#top-level-buttons-computed > ytd-button-renderer > yt-button-shape > a"));
        buttonElement.click();
        Thread.sleep(5000); // 5초 대기

        // #contents 요소 안의 모든 ytd-rich-grid-row 요소를 가져옴
        List<WebElement> richGridRows = driver.findElements(By.cssSelector("#contents > ytd-rich-grid-row"));

        // 각 richGridRows 요소에 대해 반복
        for (WebElement richGridRow : richGridRows) {
            // richGridRow 요소 안의 모든 ytd-rich-item-renderer 요소를 가져옴
            List<WebElement> richItemRenderers = richGridRow.findElements(By.cssSelector("ytd-rich-item-renderer"));

            // 각 richItemRenderers 요소에 대해 반복
            for (WebElement richItemRenderer : richItemRenderers) {
                // richItemRenderer 요소를 클릭하여 내부 정보를 가져옴
                richItemRenderer.click();
                Thread.sleep(2000); // 2초 대기

                // #header-description > h3:nth-child(1) > yt-formatted-string > a 요소의 텍스트 가져오기
                WebElement headerDescription = driver.findElement(By.cssSelector("#header-description > h3:nth-child(1) > yt-formatted-string > a"));
                String headerText = headerDescription.getText();

                // #items 안의 모든 #playlist-items 요소를 가져옴
                List<WebElement> playlistItems = driver.findElements(By.cssSelector("#items #playlist-items"));

                // 각 playlistItems 요소에 대해 반복
                for (WebElement playlistItem : playlistItems) {
                    // #video-title 요소의 텍스트 가져오기
                    WebElement videoTitleElement = playlistItem.findElement(By.cssSelector("#video-title"));
                    String videoTitle = videoTitleElement.getText();

                    // #byline 요소의 텍스트 가져오기
                    WebElement bylineElement = playlistItem.findElement(By.cssSelector("#byline"));
                    String byline = bylineElement.getText();

                    // #button > yt-icon > yt-icon-shape > icon-shape > div 요소 클릭
                    WebElement buttonElement3 = playlistItem.findElement(By.cssSelector("#button > yt-icon > yt-icon-shape > icon-shape > div"));
                    buttonElement3.click();
                    Thread.sleep(5000); // 2초 대기

                    // #items > ytd-menu-service-item-renderer:nth-child(4) > tp-yt-paper-item > yt-formatted-string 요소 클릭
                    WebElement shareButtonElement = playlistItem.findElement(By.cssSelector("#items > ytd-menu-service-item-renderer:nth-child(4) > tp-yt-paper-item > yt-formatted-string"));
                    shareButtonElement.click();
                    Thread.sleep(5000); // 2초 대기

                    // #share-url 인풋창의 value값 가져오기
                    WebElement shareUrlInput = driver.findElement(By.cssSelector("#share-url"));
                    String shareUrl = shareUrlInput.getAttribute("value");

                    // 가져온 정보 출력 또는 저장
                    System.out.println("Header Text: " + headerText);
                    System.out.println("Video Title: " + videoTitle);
                    System.out.println("Byline: " + byline);
                    System.out.println("Share URL: " + shareUrl);
                }


                // 다시 이전 페이지로 돌아가기 (또는 다음 작업을 위해 이전 페이지로 돌아가기)
                driver.navigate().back();
            }
        }

        // WebDriver 종료
        driver.quit();

        return null;
    }



}
