package com.save.savetime.repository;

import com.save.savetime.model.entity.YoutubeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeListRepository extends JpaRepository<YoutubeList, Long> {
    List<YoutubeList> findByListIdOrderByYoutubeListUpdateDateDesc(String youtubeListId);
    YoutubeList findByListIdAndChannelId(String listId, String channelId);
}
