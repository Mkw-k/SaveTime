package com.save.savetime.model.dto;

import com.google.api.client.util.DateTime;
import lombok.Data;

@Data
public class YoutubeDTO {
    private long idx;

    private String title;

    private String videoUrl;

    private String videoId;
    private DateTime publishedAt;
}
