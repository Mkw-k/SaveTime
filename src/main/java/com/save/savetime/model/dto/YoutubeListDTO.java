package com.save.savetime.model.dto;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeListDTO {
    private long idx;

    private String listTitle;

    private String listId;

    private String channelId;

    private String owner;

    private ZonedDateTime youtubeListUpdateDate;

    private String thumbUrl;

    private String memberId;
}
