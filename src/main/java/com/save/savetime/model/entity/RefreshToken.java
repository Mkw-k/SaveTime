package com.save.savetime.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "REFRESH_TOKEN_ID")
    private Long refreshTokenId;

    @Column(name = "REFRESH_TOKEN", length = 1000)
    private String refreshToken;

    @Column(name = "KEY_EMAIL")
    private String keyEmail;

}