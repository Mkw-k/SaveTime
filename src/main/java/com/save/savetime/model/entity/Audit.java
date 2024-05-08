package com.save.savetime.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "*", exclude = {"createdBy", "modifiedBy"})
@MappedSuperclass
public class Audit {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    //FIXME 비밀번호 숨기기
    @CreatedBy
    @ManyToOne
    private Member createdBy;

    //FIXME 비밀번호 숨기기
    @LastModifiedBy
    @ManyToOne
    private Member modifiedBy;
}
