
package com.save.savetime.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", exclude = {"members", "resourcesRole"})
@Builder
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @ManyToOne
    @ToString.Exclude
    private Member members;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "role")
    @ToString.Exclude
    private Set<ResourcesRole> resourcesRole = new HashSet<>();

    public void addResourcesRole(ResourcesRole...resourcesRole){
        Collections.addAll(this.resourcesRole, resourcesRole);
    }

}


