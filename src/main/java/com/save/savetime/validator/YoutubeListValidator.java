package com.save.savetime.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.save.savetime.model.entity.YoutubeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;

@Component
public class YoutubeListValidator {

    @Autowired
    ObjectMapper objectMapper;

    public void validateReturnedLists(List<YoutubeList> playlists, Errors errors){
        if(playlists.isEmpty()){
            errors.reject("500", "반환된 재생목록이 비어 있습니다");
        }
    }
}
