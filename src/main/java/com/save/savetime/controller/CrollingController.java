package com.save.savetime.controller;

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
import com.save.savetime.service.CrollingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 크롤링 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class CrollingController {

    private final CrollingService crollingService;

    //본인의 유튜브 재생목록을 가져오는 메서드
    public ResponseEntity getMyPlayList(YouTubeCrollingReqDTO crollingReqDTO) throws InterruptedException {
        Set<YouTubeCrollingRespDTO> myPlayList = crollingService.getMyPlayList(crollingReqDTO);
        if(myPlayList.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(myPlayList);
    }




}
