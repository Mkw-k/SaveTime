package com.save.savetime.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "idx")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeList extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idx")
    private long idx;

    @Column(name = "list_title")
    private String listTitle;

    @Column(name = "list_id")
    private String listId;

    @Column(name = "owner")
    private String owner;

    @Column(name = "youtube_list_update_date", columnDefinition = "DATETIME")
    private ZonedDateTime youtubeListUpdateDate;

    @Column(name = "thumb_url")
    private String thumbUrl;
}
