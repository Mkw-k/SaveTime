package com.save.savetime.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YouTubeCrollingReqDTO {
    private String id;
    private String pw;

}
