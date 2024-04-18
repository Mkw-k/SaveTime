package com.save.savetime.repository;

import com.save.savetime.model.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {

    Resources findByResourceNameAndResourceType(String resourceName, String resourceType);

    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @Query("select distinct r from Resources r left join fetch r.resourcesRole where r.resourceType = 'url' order by r.orderNum desc")
    List<Resources> findAllResources();

    @Query("select distinct r from Resources r left join fetch r.resourcesRole where r.resourceType = 'method' order by r.orderNum desc")
    List<Resources> findAllMethodResources();

    @Query("select distinct r from Resources r left join fetch r.resourcesRole where r.resourceType = 'pointcut' order by r.orderNum desc")
    List<Resources> findAllPointcutResources();
}