package com.save.savetime.security.listener;

import com.google.common.collect.Lists;
import com.save.savetime.model.entity.*;
import com.save.savetime.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResourcesRepository resourcesRepository;

    @Autowired
    private RoleHierarchyRepository roleHierarchyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccessIpRepository accessIpRepository;
    @Autowired
    ResourcesRolesetRepository resourcesRolesetRepository;

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        //setupSecurityResources();
        setupAccessIpData();

        alreadySetup = true;
    }



    private void setupSecurityResources() {

        Set<Role> roles = new HashSet<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);

        createResourceIfNotFound("/admin/**", "", roles, "url");
        createUserIfNotFound("admin", "pass", "admin@gmail.com", 10,  roles);
        
        Set<Role> roles1 = new HashSet<>();
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        roles1.add(managerRole);

        createResourceIfNotFound("io.security.corespringsecurity.aopsecurity.method.AopMethodService.methodTest", "", roles1, "method");
        createResourceIfNotFound("io.security.corespringsecurity.aopsecurity.method.AopMethodService.innerCallMethodTest", "", roles1, "method");
        createResourceIfNotFound("execution(* io.security.corespringsecurity.aopsecurity.pointcut.*Service.*(..))", "", roles1, "pointcut");
        createUserIfNotFound("manager", "pass", "manager@gmail.com", 20, roles1);
        createRoleHierarchyIfNotFound(managerRole, adminRole);

        Set<Role> roles3 = new HashSet<>();
        Role childRole1 = createRoleIfNotFound("ROLE_USER", "회원");
        roles3.add(childRole1);

        createResourceIfNotFound("/users/**", "", roles3, "url");
        createUserIfNotFound("user", "pass", "user@gmail.com", 30, roles3);
        createRoleHierarchyIfNotFound(childRole1, managerRole);

    }

    @Transactional
    public Role createRoleIfNotFound(String roleName, String roleDesc) {

        Role role = roleRepository.findByRoleName(roleName);

        if (role == null) {
            role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Member createUserIfNotFound(String userName, String password, String email, int age, Set<Role> roleSet) {

        Member member = loginRepository.findByEmail(userName).orElse(null);

        if (member == null) {
            member = Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(roleSet)
                    .build();
        }
        return loginRepository.save(member);
    }

    @Transactional
    public Resources createResourceIfNotFound(String resourceName, String httpMethod, Set<Role> roleSet, String resourceType) {
        Resources resources = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod);

        if (resources == null) {
            //리소스 저장
            resources = Resources.builder()
                    .resourceName(resourceName)
                    .httpMethod(httpMethod)
                    .resourceType(resourceType)
                    .orderNum(count.incrementAndGet())
                    .build();
            resourcesRepository.save(resources);

            //롤 저장 - 롤은 저장할 필요 없음 이미 존재함 - 거의 안변하는 값임!!
        }

        //중간테이블 엔티티 저장
        for (Role role : roleSet) {
            ResourcesRole resourcesRole = this.givenResourceRole(resources, role);
            //add 메소드 처리
            resources.addResourcesRole(resourcesRole);
            role.addResourcesRole(resourcesRole);
        }


        resourcesRepository.saveAll(Lists.newArrayList(resources));
        roleRepository.saveAll(roleSet);

        return resources;
    }

    private ResourcesRole givenResourceRole(Resources resources, Role role) {
        ResourcesRole resourcesRole = new ResourcesRole();
        resourcesRole.setRole(role);
        resourcesRole.setResources(resources);
        ResourcesRole save = resourcesRolesetRepository.save(resourcesRole);
        return save;
    }

    @Transactional
    public void createRoleHierarchyIfNotFound(Role childRole, Role parentRole) {

        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByChildName(parentRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(parentRole.getRoleName())
                    .build();
        }
        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByChildName(childRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(childRole.getRoleName())
                    .build();
        }

        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);
        childRoleHierarchy.setParentName(parentRoleHierarchy);
    }

    private void setupAccessIpData() {
        AccessIp byIpAddress = accessIpRepository.findByIpAddress("127.0.0.1");
        if (byIpAddress == null) {
        AccessIp accessIp = AccessIp.builder()
                    .ipAddress("127.0.0.1")
                    .build();
        accessIpRepository.save(accessIp);
        }

    }
}