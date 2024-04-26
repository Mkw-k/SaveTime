package com.save.savetime.model.entity;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "youtube_list", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"list_id", "channel_id"})
})
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

    @Column(name = "channel_id")
    @Comment("해당유저의 채널(유튜브) 아이디")
    private String channelId;

    @Column(name = "owner")
    private String owner;

    @Column(name = "youtube_list_update_date", columnDefinition = "DATETIME")
    private ZonedDateTime youtubeListUpdateDate;

    @Column(name = "thumb_url")
    private String thumbUrl;

    @Column(name = "member_id")
    private String memberId;
}
