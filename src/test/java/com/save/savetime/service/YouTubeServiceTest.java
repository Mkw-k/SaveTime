package com.save.savetime.service;

import com.save.savetime.config.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

class YouTubeServiceTest extends BaseTest {
    @Autowired
    YoutubeService youTubeService;

    @Test
    @DisplayName("유튜브 재생목록에 들어있는 videoId들을 불러온다.")
    public void getMyYouTubeListByIdTest(){

        //given
        String listId = "PLb6eUvuqyLCGqOrKsNiwal5aqPu-QaKBH";

        //when
        List<String> myYouTubeListById = youTubeService.getMyYouTubeByListId(listId);

        //then
        Assertions.assertTrue(myYouTubeListById.isEmpty(), "리스트가 비어 있습니다.");
    }

    @Test
    @DisplayName("유튜브 재생목록 불러오기 By All JAVA Logic")
    public void getYoutubeListSampleTest() throws GeneralSecurityException, IOException {

        //given
        //none

        //when
        youTubeService.getYoutubeListSample();

        //then
        //none
    }
}