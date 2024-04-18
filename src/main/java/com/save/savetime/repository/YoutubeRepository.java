package com.save.savetime.repository;

import com.google.api.services.youtube.YouTube;
import com.save.savetime.model.entity.Youtube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YoutubeRepository extends JpaRepository<Youtube, Long> {
    List<YouTube> findByVideoId(String videoId);
}
