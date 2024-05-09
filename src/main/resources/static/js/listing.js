// VideoIdPager 클래스 정의
class VideoIdPager {
    constructor(videoIds, itemsPerPage) {
        this.videoIds = videoIds; // 전체 비디오 ID 목록
        this.itemsPerPage = itemsPerPage; // 페이지당 표시할 비디오 수
        this.currentPage = 1; // 현재 페이지
    }

    // 전체 페이지 수 계산
    getTotalPages() {
        return Math.ceil(this.videoIds.length / this.itemsPerPage);
    }

    // 현재 페이지의 비디오 ID 목록 가져오기
    getCurrentPageVideoIds() {
        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        const endIndex = startIndex + this.itemsPerPage;
        return this.videoIds.slice(startIndex, endIndex);
    }

    // 다음 페이지로 이동
    nextPage() {
        if (this.currentPage < this.getTotalPages()) {
            this.currentPage++;
        }
    }

    // 이전 페이지로 이동
    prevPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
        }
    }

    // 특정 페이지로 이동
    setCurrentPage(pageNumber) {
        if (pageNumber >= 1 && pageNumber <= this.getTotalPages()) {
            this.currentPage = pageNumber;
        }
    }
}

let listTitle = [];
let listId = [];
let APIKey = 'AIzaSyB9elHDUCpcwAEcotBCdlZjm1jcNHThlKM';
let channelId = 'UCSQn6N6WVsutizfFCjmeTwA';
let gVideoIdPager;

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

// 비디오 ID 목록
let videoIds = [];
/**
 * 임시 사용 나중에 JAVA로 변경예정
 * @param APIKey
 */
function getVideos(APIKey, listId, accessToken) {
    videoIds = [];
    for (let i = 0; i < listId.length; i++) {
        $.ajax({
            type: "GET",
            url: "https://www.googleapis.com/youtube/v3/playlistItems",
            headers: {
                "Authorization": "Bearer " + accessToken
            },
            async : true,
            data: {
                part: "snippet",
                playlistId: listId[i],
                maxResults: 100
            },
            success: function(response) {
                let temp_listTitle = listTitle[i];
                console.log(temp_listTitle);
                for (let j = 0; j < response.items.length; j++) {
                    videoIds.push(response.items[j].snippet.resourceId.videoId);
                }
                console.log(videoIds);

                // getVideos 함수의 모든 비동기 작업이 완료된 후에 createPlayers 함수를 호출합니다.
                if (i === listId.length - 1) {
                    //createPlayers(videoIds);
                }

                // 페이지당 표시할 비디오 수
                const itemsPerPage = 7;

                // VideoIdPager 객체 생성
                let videoIdPager = new VideoIdPager(videoIds, itemsPerPage);
                gVideoIdPager = videoIdPager;

                //페이징 처리
                let totalPages = gVideoIdPager.getTotalPages();
                let pagingHtml = ``;
                for (let j = 0; j < totalPages; j++) {
                    // 페이지 번호는 j+1부터 시작해야 하므로 j+1로 사용
                    let isActive = '';
                    if(j == gVideoIdPager.currentPage){
                        isActive = 'active';
                    }
                    pagingHtml += `<li class="page-item ${isActive}"><a class="page-link" href="javascript:void(0);" onclick="goToPage(${j})">${j + 1}</a></li>`;
                }
                pagingHtml += `<li class="page-item"><a class="page-link" href="javascript:nextPage()"><span class="ti-angle-right"></span></a></li>`;

                // 페이징 아이템 업데이트
                $('#paging_box').append(pagingHtml);
                //updatePagingItems(pagingHtml);

                // 페이지 로드 시 초기 비디오 표시
                displayVideos();
            },
            error: function(xhr, status, error) {
                console.error("AJAX Error:", error);
            }
        });
    }
}

function updatePagingItems(items) {
    const pagingItemsContainer = document.getElementById('paging_box');
    pagingItemsContainer.innerHTML = ''; // 이전에 추가된 요소 모두 제거

    items.forEach((item, index) => {
        const activeClass = index === 0 ? 'active' : ''; // 첫 번째 아이템은 active 클래스 추가
        const itemHtml = `<div class="carousel-item ${activeClass}">
                            <ul class="pagination justify-content-start">
                                ${item}
                            </ul>
                         </div>`;
        pagingItemsContainer.innerHTML += itemHtml;
    });
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
    // 새로운 div 요소 생성 (iframe을 포함할 컨테이너)
    var playerContainer = document.createElement('div');
    playerContainer.id = containerId;
    playerContainer.style.marginTop = '13px'; // 위쪽 마진 설정
    playerContainer.style.marginBottom = '13px'; // 아래쪽 마진 설정

    // 동영상 플레이어의 높이와 너비 설정
    //playerContainer.style.width = '640px';
    //playerContainer.style.height = '600px';

    // 플레이어 컨테이너를 페이지에 추가
    var playerWrapper = document.getElementById('player-container');
    playerWrapper.appendChild(playerContainer);

    // YouTube 플레이어 생성
    new YT.Player(containerId, {
        height: '360', // 컨테이너의 높이를 100%로 설정하여 크기 조정
        width: '640', // 컨테이너의 너비를 100%로 설정하여 크기 조정
        videoId: videoId,
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });
}

// 플레이어가 준비되었을 때 호출되는 콜백 함수
function onPlayerReady(event) {
    //event.target.playVideo();
}

// 동영상 플레이어의 상태가 변경되었을 때 호출되는 함수
function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.PLAYING && !done) {
        setTimeout(stopVideo, 6000);
        done = true;
    }
}



// 이전 페이지로 이동
function prevPage() {
    gVideoIdPager.prevPage();
    displayVideos();
}

// 다음 페이지로 이동
function nextPage() {
    gVideoIdPager.nextPage();
    displayVideos();
}

// 특정 페이지로 이동
function goToPage(pageNumber) {
    gVideoIdPager.setCurrentPage(pageNumber);
    displayVideos();
}

// 현재 페이지의 비디오 표시
function displayVideos() {
    const currentPageVideoIds = gVideoIdPager.getCurrentPageVideoIds();

    const playerContainer = document.getElementById('player-container');
    playerContainer.innerHTML = ''; // 이전에 생성된 iframe 제거

    currentPageVideoIds.forEach((videoId, index) => {
        const containerId = 'player' + (index + 1);
        const playerDiv = document.createElement('div');
        playerDiv.id = containerId;
        playerContainer.appendChild(playerDiv);

        // YouTube 비디오 iframe 생성
        createYouTubePlayer(videoId, containerId);
    });
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

/**
 * 유저 아이디 클릭시 드롭다운
 */
function toggleDropdown() {
    var dropdownMenu = document.getElementById("dropdownMenu");
    if (dropdownMenu.style.display === "block") {
        dropdownMenu.style.display = "none";
    } else {
        dropdownMenu.style.display = "block";
    }
}

document.getElementById('user_drop_down').addEventListener('touchstart', toggleDropdown);