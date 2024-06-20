let listTitle = [];
let listId = [];
let APIKey = 'AIzaSyB9elHDUCpcwAEcotBCdlZjm1jcNHThlKM';
let channelId = 'UCSQn6N6WVsutizfFCjmeTwA';

// IFrame Player API 코드 로드
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

// YouTube IFrame Player API 로드가 완료되었을 때 호출되는 함수
function onYouTubeIframeAPIReady() {
    let urlParameters = getUrlParameters();
    let listId = urlParameters['listId'];
    let listIdList = [];
    listIdList.push(listId);
    let accessTokenFromCookie = getAccessTokenFromCookie();

    getVideos(APIKey, listIdList, accessTokenFromCookie);
}

/**
 * 사용 X
 * @param channelId
 * @param APIKey
 */
function getChannelLists(channelId, APIKey) {
    $.ajax({
        type: "GET",
        url: "https://www.googleapis.com/youtube/v3/playlists",
        data: {
            part: "snippet",
            channelId: channelId,
            key: APIKey,
            type: "playlist",
            maxResults: 100,
        },
        success: function (response) {
            let playListLength = response.pageInfo.totalResults;
            for (let i = 0; i < playListLength; i++) {
                listTitle.push(response.items[i].snippet.title);
                listId.push(response.items[i].id);
            }
            console.log(listTitle);
            console.log(listId);
        },
        error: function (xhr, status, error) {
            console.error("AJAX Error:", error);
        }
    });
}

// 플레이어가 준비되었을 때 호출되는 콜백 함수
function onPlayerReady(event) {
    //event.target.playVideo();
}

function getUrlParameters() {
    // 현재 URL을 가져오기
    var currentUrl = window.location.href;

    // URL에서 파라미터 부분만 추출하기
    var paramsString = currentUrl.split('?')[1];

    // 파라미터를 객체로 변환하기
    var params = {};
    paramsString.split('&').forEach(function(param) {
        var keyValue = param.split('=');
        params[keyValue[0]] = keyValue[1];
    });

    return params;
}

let videoId = [];
/**
 * 임시 사용 나중에 JAVA로 변경예정
 * @param APIKey
 */
function getVideos(APIKey, listId, accessToken) {
    videoId = [];
    for (let i = 0; i < listId.length; i++) {
        $.ajax({
            type: "GET",
            url: "https://www.googleapis.com/youtube/v3/playlistItems",
            headers: {
                "Authorization": "Bearer " + accessToken
            },
            data: {
                part: "snippet",
                playlistId: listId[i],
                maxResults: 100
            },
            success: function(response) {
                let temp_listTitle = listTitle[i];
                console.log(temp_listTitle);
                for (let j = 0; j < response.items.length; j++) {
                    videoId.push(response.items[j].snippet.resourceId.videoId);
                }
                console.log(videoId);

                // getVideos 함수의 모든 비동기 작업이 완료된 후에 createPlayers 함수를 호출합니다.
                if (i === listId.length - 1) {
                    createPlayers(videoId);
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX Error:", error);
            }
        });
    }
}

// 동영상 플레이어 생성 함수
function createPlayers(videoIdList) {
    // 동영상 ID 목록
    //var videoIdList = ['cn9bS6OAWxY', 'opF3gIz7_v4', 'M7lc1UVf-VE']; // 동영상 ID를 여기에 추가하세요.

    // 동영상 플레이어 컨테이너에 HTML 요소 추가
    var playerContainer = document.getElementById('player-container');
    for (var i = 0; i < videoIdList.length; i++) {
        var containerId = 'player' + (i + 1);
        var playerDiv = document.createElement('div');
        playerDiv.id = containerId;
        playerContainer.appendChild(playerDiv);
    }

    // 각 동영상 플레이어 생성
    for (var i = 0; i < videoIdList.length; i++) {
        var containerId = 'player' + (i + 1);
        createYouTubePlayer(videoIdList[i], containerId);
    }
}

// 동영상 플레이어를 생성하는 함수
function createYouTubePlayer(videoId, containerId) {
    new YT.Player(containerId, {
        height: '360',
        width: '640',
        videoId: videoId,
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });
}

// 동영상 플레이어의 상태가 변경되었을 때 호출되는 함수
function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.PLAYING && !done) {
        setTimeout(stopVideo, 6000);
        done = true;
    }
}

// 동영상 정지 함수
function stopVideo() {
    player.stopVideo();
}

// 쿠키에서 액세스 토큰 가져오기
function getAccessTokenFromCookie() {
    var name = "access_token=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var cookieArray = decodedCookie.split(';');
    for (var i = 0; i < cookieArray.length; i++) {
        var cookie = cookieArray[i].trim();
        if (cookie.indexOf(name) == 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return null;
}