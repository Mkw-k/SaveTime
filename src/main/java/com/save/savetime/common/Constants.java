package com.save.savetime.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Constants {

    @Getter
    @RequiredArgsConstructor
    public enum YoutubeEndPointUrl {
        playlist("v3/playlists?part=snippet,contentDetails&mine=true&maxResults=50");
        //playlist("v3/playlists?part=snippet,contentDetails");

        private final String url;

    }

}
