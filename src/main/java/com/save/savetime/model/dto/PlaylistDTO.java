package com.save.savetime.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDTO {
    private String playListName;
    private List<YoutubeDTO> videos;
}