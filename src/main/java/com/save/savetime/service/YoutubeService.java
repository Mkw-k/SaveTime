package com.save.savetime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.save.savetime.model.dto.PlaylistDTO;
import com.save.savetime.model.dto.YoutubeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
    public class YoutubeService {

    @Value("${youtube.url}")
    private String youTubeApiUrl;
    @Value("${youtube.key}")
    private String youTubeApiKey;


    public List<PlaylistDTO> getMyPlayListByYouTubeApi(String token) throws IOException {
            // YouTube Data API의 엔드포인트 URL
            String apiUrl = youTubeApiUrl;

            // OAuth 2.0 토큰 (이 부분을 실제 토큰으로 대체해야 합니다)
            String accessToken = token;

            // HTTP 요청 헤더에 OAuth 2.0 토큰을 포함하여 연결 설정
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("key", youTubeApiKey); // API 키 추가

            // GET 요청 보내고 응답 받기
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 응답 데이터를 PlaylistDTO 리스트로 변환
            List<PlaylistDTO> playlists = new ArrayList<>();
            // ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 문자열을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(response.toString());
            // "items" 배열 추출
            JsonNode itemsNode = rootNode.get("items");
            // YouTube API 클라이언트 초기화
            YouTube youtubeService = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                    .setApplicationName("SaveTime")
                    .setYouTubeRequestInitializer(new YouTubeRequestInitializer(youTubeApiKey))
                    .build();

            return playlists;

        }

        public List<YoutubeDTO> getMyYouTubeListById(String id){

            return null;
        }

}
