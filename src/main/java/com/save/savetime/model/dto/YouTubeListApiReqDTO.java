package com.save.savetime.model.dto;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YouTubeListApiReqDTO {
    @NotNull
    private String token;
}
