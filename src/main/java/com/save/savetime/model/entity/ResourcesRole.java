package com.save.savetime.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "resources_role", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"resource_id", "role_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourcesRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id")  // 외래 키 매핑
    @OrderBy("id desc")
    private Resources resources;

    @ManyToOne
    @JoinColumn(name = "role_id")  // 외래 키 매핑
    private Role role;

}
