package com.save.savetime.controller;

import com.save.savetime.common.AuthMember;
import com.save.savetime.model.dto.SearchYoutubeListReqDTO;
import com.save.savetime.model.dto.YouTubeListApiReqDTO;
import com.save.savetime.model.dto.YoutubeListDTO;
import com.save.savetime.model.entity.Member;
import com.save.savetime.model.entity.YoutubeList;
import com.save.savetime.repository.YoutubeListRepository;
import com.save.savetime.service.YoutubeService;
import com.save.savetime.validator.YoutubeListValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/youtube")
public class YoutubeController {
    private final YoutubeService youTubeService;
    private final YoutubeListValidator listValidator;
    private final YoutubeListRepository youtubeListRepository;

    @Autowired
    public YoutubeController(YoutubeService youTubeService, YoutubeListValidator listValidator, YoutubeListRepository youtubeListRepository) {
        this.youTubeService = youTubeService;
        this.listValidator = listValidator;
        this.youtubeListRepository = youtubeListRepository;
    }


    @PostMapping
    public ResponseEntity getMyPlayListByYouTubeApi(@Valid YouTubeListApiReqDTO requestDTO, Errors errors){
        List<YoutubeListDTO> playlists = new ArrayList<>();

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // YouTube API를 통해 재생목록을 가져옴
            playlists = youTubeService.getMyPlayListByYouTubeApiAndSaveDB(requestDTO.getToken());
            //playlists = youTubeService.getMyPlayListIdForAPI();

            // 유효성 검사 수행
//            listValidator.validateReturnedLists(playlists, errors);

            // 가져온 재생목록이 비어있는지 확인
//            if (errors.hasErrors()) {
//                return ResponseEntity.badRequest().body(errors);
//            }
        } catch (Exception ex) {
            // IOException이 발생한 경우 에러를 생성하여 반환
            errors.reject("500", "YouTube API 호출 중 오류가 발생했습니다 : " + ex.getMessage());
            log.error("YouTube API 호출 중 오류가 발생했습니다 : {}", ex.getMessage());
            return ResponseEntity.badRequest().body(errors);
        }

        // ResponseEntity로 변환하여 반환
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/list")
    public String searchYoutubeList(SearchYoutubeListReqDTO searchFormDTO, Model model, @AuthMember Member member){
        String query = searchFormDTO.getQuery();
        String listId = searchFormDTO.getListId();

        List<YoutubeList> myYouTubeListByListId = youTubeService.getMyYouTubeListByListIdAtDB(listId, member.getIdx());
        model.addAttribute("dbYoutubeLists", myYouTubeListByListId);

        return "index";
    }

    @GetMapping("/info")
    @ResponseBody
    public void getMyYoutubeData() throws GeneralSecurityException, IOException {
        youTubeService.getYoutubeListSample();
    }


}
