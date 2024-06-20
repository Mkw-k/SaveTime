package com.save.savetime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;
import com.save.savetime.common.Constants;
import com.save.savetime.model.dto.PlaylistDTO;
import com.save.savetime.model.dto.YoutubeDTO;
import com.save.savetime.model.dto.YoutubeListDTO;
import com.save.savetime.model.entity.Member;
import com.save.savetime.model.entity.YoutubeList;
import com.save.savetime.repository.YoutubeListRepository;
import com.save.savetime.util.Auth;
import com.save.savetime.util.YoutubeUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class YoutubeService {

    private final String YOUTUBE_API_URL;
    private final String YOUTUBE_API_KEY;
    private final YoutubeListRepository youtubeListRepository;
    private final YoutubeUtils youtubeUtils;

    private static YouTube youtube;
    private static final String APPLICATION_NAME = "SaveTime";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Autowired
    public YoutubeService(@Value("${youtube.url}") String youtubeApiUrl,
                          @Value("${youtube.key}") String youtubeApiKey,
                          YoutubeListRepository youtubeListRepository,
                          YoutubeUtils youtubeUtils) {
        this.YOUTUBE_API_URL = youtubeApiUrl;
        this.YOUTUBE_API_KEY = youtubeApiKey;
        this.youtubeListRepository = youtubeListRepository;
        this.youtubeUtils = youtubeUtils;
    }


    public List<YoutubeListDTO> getMyPlayListByYouTubeApiAndSaveDB(String token) throws Exception {
        List<YoutubeListDTO> youtubeListDTOS = new ArrayList<>();

        // YouTube Data API의 URL
        String apiUrl = YOUTUBE_API_URL + Constants.YoutubeEndPointUrl.playlist.getUrl();

        // OAuth 2.0 토큰
        String accessToken = token;
        log.debug("token 확인 >>> {}", accessToken);

        // HTTP 요청 헤더에 OAuth 2.0 토큰을 포함하여 연결 설정
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("key", YOUTUBE_API_KEY); // API 키 추가

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

        // YouTube API 클라이언트 초기화 : 사용 X
        YouTube youtubeService = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), null)
                .setApplicationName("SaveTime")
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(YOUTUBE_API_KEY))
                .build();

        ArrayNode itemsArrayNode = (ArrayNode) itemsNode;
        for (JsonNode children : itemsArrayNode) {
            //children.get("id");
            //TODO 널처리 해줘야됨
            YoutubeList youtubeList = YoutubeList.builder()
                    .listId(children.get("id").toString().replaceAll("\"", ""))//재생목록id
                    .channelId(children.get("snippet").get("channelId").toString().replaceAll("\"", ""))//채널 아이디
                    .listTitle(children.get("snippet").get("title").toString().replaceAll("\"", ""))//재생목록명
                    .owner(children.get("snippet").get("channelTitle").toString().replaceAll("\"", "")) //소유자 명?
                    .thumbUrl(getThumbUrl(children.get("snippet").get("thumbnails")))// 썸네일 url
                    .youtubeListUpdateDate(ZonedDateTime.parse(children.get("snippet").get("publishedAt").asText(), DateTimeFormatter.ISO_DATE_TIME)) //업데이트일
                    .memberId(SecurityContextHolder.getContext().getAuthentication().getName())
                    .build();

            saveDbAndAddList(youtubeListDTOS, children.get("id").toString().replaceAll("\"", ""), children.get("snippet").get("channelId").toString().replaceAll("\"", ""), youtubeList);
        }

        return youtubeListDTOS;
    }

    private void saveDbAndAddList(List<YoutubeListDTO> youtubeLists, String listId, String channelId, YoutubeList youtubeList) {
        ModelMapper modelMapper = new ModelMapper();
        //먼저 이미 있는 재생목록인지 확인 : 있으면 리스트에 넣고 건너뜀
        log.debug("이미 있는재생목록인지 확인 >>>");
        YoutubeList byListIdAndChannelId = youtubeListRepository.findByListIdAndChannelId(listId, channelId);
        if(byListIdAndChannelId != null){
            log.debug("있는 목록임!! >> ");
            YoutubeListDTO youtubeListDTO = modelMapper.map(byListIdAndChannelId, YoutubeListDTO.class);
            youtubeLists.add(youtubeListDTO);
        }else{
            log.debug("없는 목록임!! >> ");
            // 저장 시도
            YoutubeList savedYoutubeList = youtubeListRepository.save(youtubeList);
            // 저장에 성공한 경우에만 리스트에 추가
            YoutubeListDTO youtubeListDTO = modelMapper.map(savedYoutubeList, YoutubeListDTO.class);
            youtubeLists.add(youtubeListDTO);
        }
    }

    private String getThumbUrl(JsonNode thumbnailsNode) {
        String thumbUrl = "";

        if(thumbnailsNode.get("maxres") != null){
            thumbUrl = thumbnailsNode.get("maxres").get("url").toString().replaceAll("\"", "");
        }else if(thumbnailsNode.get("high") != null){
            thumbUrl = thumbnailsNode.get("high").get("url").toString().replaceAll("\"", "");
        }else if(thumbnailsNode.get("medium") != null){
            thumbUrl = thumbnailsNode.get("medium").get("url").toString().replaceAll("\"", "");
        }else{ //default
            thumbUrl = thumbnailsNode.get("default").get("url").toString().replaceAll("\"", "");
        }

        return thumbUrl;
    }

    public List<YoutubeDTO> getMyYouTubeByListId(String listId){
        String playlistId = listId;

        try {
            List<YoutubeDTO> playlistItems = fetchVideosFromPlaylist(playlistId);

            /*for (YoutubeDTO playlistItem : playlistItems) {
                log.info("Video ID: {}", playlistItem.getVideoId());
                videoIdList.add(playlistItem.getContentDetails().getVideoId());
            }*/
            return playlistItems;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 재생목록에 속한 동영상 ID 가져오기
    public static List<YoutubeDTO> fetchVideosFromPlaylist(String playlistId) throws GeneralSecurityException, IOException {
        //YouTube youtubeService = getService();
        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        // Authorize the request.
        Credential credential = Auth.authorize(scopes, "myuploads");

        // This object is used to make YouTube Data API requests.
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                "youtube-cmdline-myuploads-sample").build();

        // Define a list to store items in the list of uploaded videos.
        List<PlaylistItem> playlistItemList = new ArrayList<>();

        // 재생목록에 속한 동영상 목록 가져오기
        // Retrieve the playlist of the channel's uploaded videos.
        YouTube.PlaylistItems.List playlistItemRequest =
                youtube.playlistItems().list("id,contentDetails,snippet");
        playlistItemRequest.setPlaylistId(playlistId);

        // Only retrieve data used in this application, thereby making
        // the application more efficient. See:
        // https://developers.google.com/youtube/v3/getting-started#partial
        playlistItemRequest.setFields(
                "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");

        String nextToken = "";

        // Call the API one or more times to retrieve all items in the
        // list. As long as the API response returns a nextPageToken,
        // there are still more items to retrieve.
        do {
            playlistItemRequest.setPageToken(nextToken);
            PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

            playlistItemList.addAll(playlistItemResult.getItems());

            nextToken = playlistItemResult.getNextPageToken();
        } while (nextToken != null);

        // Prints information about the results.
        List<YoutubeDTO> youtubeDTOS = prettyPrintAndPlaylistItemParsingDTO(playlistItemList.size(), playlistItemList.iterator());

        //PlaylistItemListResponse response = request.execute();
        //return response.getItems();
        return youtubeDTOS;
    }

    /*
     * Print information about all of the items in the playlist.
     *
     * @param size size of list
     *
     * @param iterator of Playlist Items from uploaded Playlist
     */
    private static List<YoutubeDTO> prettyPrintAndPlaylistItemParsingDTO(int size, Iterator<PlaylistItem> playlistEntries) {
        List<YoutubeDTO> youtubeDTOS = new ArrayList<>();

        System.out.println("=============================================================");
        System.out.println("\t\tTotal Videos Uploaded: " + size);
        System.out.println("=============================================================\n");

        while (playlistEntries.hasNext()) {
            YoutubeDTO youtubeDTO = new YoutubeDTO();

            PlaylistItem playlistItem = playlistEntries.next();
            System.out.println(" video name  = " + playlistItem.getSnippet().getTitle());
            System.out.println(" video id    = " + playlistItem.getContentDetails().getVideoId());
            System.out.println(" upload date = " + playlistItem.getSnippet().getPublishedAt());
            System.out.println("\n-------------------------------------------------------------\n");

            youtubeDTO.setTitle(playlistItem.getSnippet().getTitle());
            youtubeDTO.setVideoId(playlistItem.getContentDetails().getVideoId());
            youtubeDTO.setPublishedAt(playlistItem.getSnippet().getPublishedAt());
            youtubeDTOS.add(youtubeDTO);
        }

        return youtubeDTOS;
    }

    /**
     * 유튜브 재생목록 불러오기
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public List<YoutubeListDTO> getMyPlayListIdForAPI() throws GeneralSecurityException, IOException {
        List<YoutubeListDTO> youtubeListDTOS = new ArrayList<>();
        YouTube youtubeService = initializeYouTube();

        // 요청할 결과 수 설정
        int maxResults = 50; // 최대 10개의 플레이리스트 가져오기

        // 사용자의 플레이리스트 목록 가져오기
        List<Playlist> playlists = getPlaylists(youtubeService, maxResults);

        // 가져온 플레이리스트 목록 출력
        if (playlists != null) {
            System.out.println("Total Playlists: " + playlists.size());
            for (Playlist playlist : playlists) {
                System.out.println("Playlist ID: " + playlist.getId());
                System.out.println("Playlist Title: " + playlist.getSnippet().getTitle());
                System.out.println("----------------------------------------");

                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(playlist.getSnippet().getPublishedAt().toString(), formatter);

                YoutubeList youtubeList = YoutubeList.builder()
                        .listTitle(playlist.getSnippet().getTitle())
                        .listId(playlist.getId())
                        .thumbUrl(getThumbUrlByObj(playlist.getSnippet().getThumbnails()))
                        .owner(playlist.getSnippet().getChannelTitle())
                        .youtubeListUpdateDate(zonedDateTime)
                        .channelId(playlist.getSnippet().getChannelId())
                        .memberId(null) //TODO 나중에 멤버아이디 추가할것!!
                        .build();

                saveDbAndAddList(youtubeListDTOS, playlist.getId(), playlist.getSnippet().getChannelId(), youtubeList);
            }
        } else {
            System.out.println("No playlists found.");
        }

        return youtubeListDTOS;
    }

    private String getThumbUrlByObj(ThumbnailDetails thumbnailDetails) {
        String thumbUrl = "";

        if(thumbnailDetails.getMaxres() != null){
            thumbUrl = thumbnailDetails.getMaxres().getUrl().toString().replaceAll("\"", "");
        }else if(thumbnailDetails.getHigh() != null){
            thumbUrl = thumbnailDetails.getHigh().getUrl().toString().replaceAll("\"", "");
        }else if(thumbnailDetails.getMedium() != null){
            thumbUrl = thumbnailDetails.getMedium().getUrl().toString().replaceAll("\"", "");
        }else if(thumbnailDetails.getStandard() != null){
            thumbUrl = thumbnailDetails.getStandard().getUrl().toString().replaceAll("\"", "");
        }else{
            thumbUrl = thumbnailDetails.getDefault().getUrl().toString().replaceAll("\"", "");
        }

        return thumbUrl;
    }

    // YouTube Data API 초기화
    private YouTube initializeYouTube() throws GeneralSecurityException, IOException {
        /*return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        */

        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        // Authorize the request.
        Credential credential = Auth.authorize(scopes, "myuploads");

        // This object is used to make YouTube Data API requests.
        /*youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                "youtube-cmdline-myuploads-sample").build();*/
        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
                APPLICATION_NAME).build();

        return youtube;
    }

    // 사용자의 플레이리스트 목록 가져오기
    private static List<Playlist> getPlaylists(YouTube youtubeService, int maxResults) throws IOException {
        YouTube.Playlists.List request = youtubeService.playlists().list("snippet");
        request.setMine(true); // 현재 인증된 사용자의 플레이리스트 목록 요청
        request.setMaxResults((long) maxResults); // 요청할 최대 결과 수 설정

        PlaylistListResponse response = request.execute();
        return response.getItems();
    }

    public List<YoutubeListDTO> getMyYouTubeListByMemberIdxAtDB(Member member) {
        List<YoutubeList> dbYoutubeLists = new ArrayList<>();
        if(member != null){
            dbYoutubeLists = youtubeListRepository.findByCreatedByIdxOrderByCreatedAtDesc(member.getIdx());
        }

        ModelMapper modelMapper = new ModelMapper();
        List<YoutubeListDTO> youtubeListDTOs = dbYoutubeLists.stream()
                .map(youtubeList -> modelMapper.map(youtubeList, YoutubeListDTO.class))
                .collect(Collectors.toList());

        return youtubeListDTOs;
    }

    public List<YoutubeList> getMyYouTubeListByListIdAtDB(String listId, long memberIdx){
        List<YoutubeList> youtubeLists = new ArrayList<>();
        if(listId.equals("all")){
            youtubeLists = youtubeListRepository.findByCreatedByIdxOrderByCreatedAtDesc(memberIdx);
        }else{
            youtubeLists = youtubeListRepository.findByListIdOrderByYoutubeListUpdateDateDesc(listId);
        }
        return youtubeLists;
    }

    public void getYoutubeListSample() throws GeneralSecurityException, IOException {
        youtubeUtils.getYouTubeListSampleLogic();
    }
}
