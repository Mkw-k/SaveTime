package com.save.savetime.service;

import com.save.savetime.config.BaseTest;
import com.save.savetime.model.dto.YouTubeCrollingReqDTO;
import com.save.savetime.model.dto.YouTubeCrollingRespDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

class CrollingServiceTest extends BaseTest {
    @Autowired
    CrollingService crollingService;

    /*
    @Test
    @DisplayName("본인의 유튜브 재생목록을 가져온다")
    void getMyPlayList() throws InterruptedException {
        //given : 본인의 유튜브 계정 아이디&비번
        YouTubeCrollingReqDTO crollingReqDTO = YouTubeCrollingReqDTO.builder()
                .id("rhauddn111@gmail.com")
                .pw("")
                .build();

        //when : 크롤링해서 불러오면
        Set<YouTubeCrollingRespDTO> myPlayList = crollingService.getMyPlayList(crollingReqDTO);
        myPlayList.forEach(e -> System.out.println(e.toString()));

        //then : 유튜브 재생목록 > 영상제목 & URL링크
        Assertions.assertNull(myPlayList);
    }
    */

}