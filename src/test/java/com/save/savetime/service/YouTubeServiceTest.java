package com.save.savetime.service;

import com.save.savetime.config.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *      // 1. 구글 Autorize 처리
 *
 *     // 2. 유튜브 리스트Id 불러오기
 *
 *
 *
 *     // 4. 나중에 볼 동영상
 *
 *     // 5. 특정 채널 안에 있는 리스트 Id 불러오기
 */
class YouTubeServiceTest extends BaseTest {
    @Autowired
    YoutubeService youTubeService;

    // 3. 유튜브 리스트 안에 있는 재생목록 정보 불러오기(파라미터 : listId)
    /*@Test
    @DisplayName("유튜브 재생목록에 들어있는 videoId들을 불러온다.")
    public void getMyYouTubeListByIdTest(){

        //given
        String listId = "PLb6eUvuqyLCGqOrKsNiwal5aqPu-QaKBH";

        //when
        List<YoutubeDTO> myYouTubeListById = youTubeService.getMyYouTubeByListId(listId);

        //then : 내용물이 있다!
        Assertions.assertTrue(!myYouTubeListById.isEmpty(), "리스트가 있습니다.");
    }*/

    /*@Test
    @DisplayName("유튜브 재생목록 불러오기 By All JAVA Logic")
    public void getYoutubeListSampleTest() throws GeneralSecurityException, IOException {

        //given
        //none

        //when
        youTubeService.getYoutubeListSample();

        //then
        //none
    }*/

    /*@Test
    @DisplayName("유튜브 재생목록 불러오기 By All JAVA Logic2")
    public void getYoutubeListSampleTest2() throws GeneralSecurityException, IOException {

        //given
        //none

        //when
        List<YoutubeListDTO> myPlayListIdForAPI = youTubeService.getMyPlayListIdForAPI();

        //then : 내용물이 있다!
        Assertions.assertTrue(!myPlayListIdForAPI.isEmpty(), "리스트가 있습니다.");
    }*/
}