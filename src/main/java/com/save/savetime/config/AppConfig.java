package com.save.savetime.config;

import com.save.savetime.repository.AccessIpRepository;
import com.save.savetime.repository.ResourcesRepository;
import com.save.savetime.security.service.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository, AccessIpRepository accessIpRepository){
        SecurityResourceService securityResourceService = new SecurityResourceService(resourcesRepository, accessIpRepository);
        return securityResourceService;
    }

}
