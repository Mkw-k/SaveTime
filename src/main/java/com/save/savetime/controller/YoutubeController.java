package com.save.savetime.controller;

import com.save.savetime.model.entity.YoutubeList;
import com.save.savetime.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeController {
    private final YoutubeService youTubeService;

    @GetMapping
    public ResponseEntity<List<YoutubeList>> getMyPlayListByYouTubeApi(@RequestParam String token) throws IOException {
        List<YoutubeList> playlists = youTubeService.getMyPlayListByYouTubeApi(token);
        // ResponseEntity로 변환하여 반환
        return ResponseEntity.ok(playlists);
    }


}
