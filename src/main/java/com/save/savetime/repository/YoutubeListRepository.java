package com.save.savetime.repository;

import com.save.savetime.model.entity.YoutubeList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YoutubeListRepository extends JpaRepository<YoutubeList, Long> {
    List<YoutubeList> findByListIdOrderByYoutubeListUpdateDateDesc(String youtubeListId);
    YoutubeList findByListIdAndChannelId(String listId, String channelId);

    List<YoutubeList> findByCreatedByIdxOrderByCreatedAtDesc(long memberIdx);
}
