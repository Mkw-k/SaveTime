package com.save.savetime.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESOURCES")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resources implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "resource_id")
    private Long id;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "order_num")
    private int orderNum;

    @Column(name = "resource_type")
    private String resourceType;

    //@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //private Set<Role> roleSet = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "resources")
    @ToString.Exclude
    private Set<ResourcesRole> resourcesRole = new HashSet<>();

    public void addResourcesRole(ResourcesRole...resourcesRole){
        Collections.addAll(this.resourcesRole, resourcesRole);
    }

}
