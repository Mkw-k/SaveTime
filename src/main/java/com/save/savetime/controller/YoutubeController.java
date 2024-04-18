package com.save.savetime.controller;

import com.save.savetime.model.dto.PlaylistDTO;
import com.save.savetime.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller("/youtube")
@RequiredArgsConstructor
public class YoutubeController {
    private final YoutubeService youTubeService;

    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getMyPlayListByYouTubeApi(@RequestParam String token) throws IOException {
        List<PlaylistDTO> playlists = youTubeService.getMyPlayListByYouTubeApi(token);
        // ResponseEntity로 변환하여 반환
        return ResponseEntity.ok(playlists);
    }


}
