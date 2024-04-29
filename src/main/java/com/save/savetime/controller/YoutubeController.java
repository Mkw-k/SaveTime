package com.save.savetime.controller;

import com.save.savetime.model.dto.YouTubeListApiReqDTO;
import com.save.savetime.model.entity.YoutubeList;
import com.save.savetime.repository.YoutubeListRepository;
import com.save.savetime.service.YoutubeService;
import com.save.savetime.validator.YoutubeListValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeController {
    private final YoutubeService youTubeService;
    private final YoutubeListValidator listValidator;
    private final YoutubeListRepository youtubeListRepository;


        @GetMapping
        public ResponseEntity getMyPlayListByYouTubeApi(@Valid YouTubeListApiReqDTO requestDTO, Errors errors){
            List<YoutubeList> playlists = new ArrayList<>();

            if(errors.hasErrors()){
                return ResponseEntity.badRequest().body(errors);
            }

            try {
                // YouTube API를 통해 재생목록을 가져옴
                playlists = youTubeService.getMyPlayListByYouTubeApi(requestDTO.getToken());
                // 유효성 검사 수행
                listValidator.validateReturnedLists(playlists, errors);

                // 가져온 재생목록이 비어있는지 확인
                if (errors.hasErrors()) {
                    return ResponseEntity.badRequest().body(errors);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // IOException이 발생한 경우 에러를 생성하여 반환
                errors.reject("500", "YouTube API 호출 중 오류가 발생했습니다 : " + ex.getMessage());
                return ResponseEntity.badRequest().body(errors);
            }

            // ResponseEntity로 변환하여 반환
            return ResponseEntity.ok(playlists);
        }


}
