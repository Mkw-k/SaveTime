package com.save.savetime.controller;

import com.save.savetime.model.dto.YouTubeCrollingReqDTO;
import com.save.savetime.model.dto.YouTubeCrawlingRespDTO;
import com.save.savetime.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 크롤링 컨트롤러
 */
@Deprecated
@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingService crollingService;

    //본인의 유튜브 재생목록을 가져오는 메서드
    public ResponseEntity getMyPlayList(YouTubeCrollingReqDTO crollingReqDTO) throws InterruptedException {
        Set<YouTubeCrawlingRespDTO> myPlayList = crollingService.getMyPlayList(crollingReqDTO);
        if(myPlayList.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(myPlayList);
    }




}
