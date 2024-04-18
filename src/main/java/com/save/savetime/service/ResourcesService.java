package com.save.savetime.service;


import com.save.savetime.model.dto.ResourcesDto;
import com.save.savetime.model.entity.Resources;
import com.save.savetime.model.entity.Role;

import java.util.List;

public interface ResourcesService {

    Resources selectResources(long id);

    List<Resources> selectResources();

    void insertResources(Resources Resources);

    void deleteResources(long id);

    Resources findByResourceNameAndResourceType(String resourceName, String httpMethod);

    Resources updateRoleResources(Resources resource, Role role);

    Resources saveResources(ResourcesDto resourcesDto);

}