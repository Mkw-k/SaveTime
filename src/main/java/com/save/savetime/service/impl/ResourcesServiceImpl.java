package com.save.savetime.service.impl;

import com.save.savetime.model.dto.ResourcesDto;
import com.save.savetime.model.entity.Resources;
import com.save.savetime.model.entity.ResourcesRole;
import com.save.savetime.model.entity.Role;
import com.save.savetime.repository.ResourcesRepository;
import com.save.savetime.repository.ResourcesRolesetRepository;
import com.save.savetime.repository.RoleRepository;
import com.save.savetime.security.metadatasource.UrlFilterInvocationSecurityMetadatsSource;
import com.save.savetime.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourcesServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;
    private final ResourcesRolesetRepository resourcesRolesetRepository;
    private final RoleRepository roleRepository;
    private final UrlFilterInvocationSecurityMetadatsSource urlFilterInvocationSecurityMetadatsSource;

    public Resources selectResources(long id) {
        Resources resources = resourcesRepository.findById(id).orElse(new Resources());

        return resources;
    }

    @Transactional
    public List<Resources> selectResources() {
        return resourcesRepository.findAll();
    }

    @Transactional
    public void insertResources(Resources resources){
        resourcesRepository.save(resources);
    }

    @Transactional
    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }

    @Transactional
    public Resources findByResourceNameAndResourceType(String resourceName, String resourceType) {
        return resourcesRepository.findByResourceNameAndResourceType(resourceName, resourceType);
    }

    /*@Transactional
    public Resources updateRoleResources(Resources resources, Role newRole) {
        if(resources.getRoleSet() != null && !resources.getRoleSet().isEmpty()){
            resources.getRoleSet().clear();
            resources.getRoleSet().add(newRole);
        }else{
            HashSet<Role> newRoleSet = new HashSet<>();
            newRoleSet.add(newRole);
            resources.setRoleSet(newRoleSet);
        }
        resourcesRepository.save(resources);
        return resources;
    }*/

    public Resources updateRoleResources(Resources resources, Role newRole) {
        ResourcesRole resourcesRole = new ResourcesRole();

        //아닐경우에는 인서트
        if(resources.getResourcesRole().isEmpty()){
            resources.getResourcesRole().clear();

            resourcesRole.setResources(resources);
            resourcesRole.setRole(newRole);

            resources.addResourcesRole(resourcesRole);
        }
        //resourcesRole이 존재할경우에는 업데이트
        else{
            resourcesRole = resources.getResourcesRole()
                    .stream()
                    .findFirst()
                    .orElse(null)
                    ;

            resourcesRole.setResources(resources);
            resourcesRole.setRole(newRole);

            resources.addResourcesRole(resourcesRole);
        }

        // resources 저장 (CascadeType.ALL로 resourcesRole도 함께 저장됨)
        Resources savedResources = resourcesRepository.save(resources);

        // 저장된 resources로부터 ResourcesRole 다시 가져오기 (필요에 따라)
        ResourcesRole savedResourcesRole = savedResources.getResourcesRole().iterator().next();

        return resources;
    }

    @Override
    @Transactional
    public Resources saveResources(ResourcesDto resourcesDto) {
        ModelMapper modelMapper = new ModelMapper();

        //새로 설정할 권한
        Role role = roleRepository.findByRoleName(resourcesDto.getRoleName());

        if(role == null){
            throw new RuntimeException("에러발생 >>> 권한 이름을 확인해주세요.");
        }

        //기존 존재하는 리소스인지 확인 위하여 get
        Resources resource = this.resourcesRepository.findByResourceNameAndHttpMethod(resourcesDto.getResourceName(), resourcesDto.getHttpMethod());

        //신규 리소스일경우
        if(resource == null){
            resource = modelMapper.map(resourcesDto, Resources.class);
        }

        //save 처리
        Resources resources = this.updateRoleResources(resource, role);
        urlFilterInvocationSecurityMetadatsSource.reload();;

        return resources;
    }

}