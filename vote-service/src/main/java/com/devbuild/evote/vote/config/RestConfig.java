package com.devbuild.evote.vote.config;

import com.devbuild.evote.voter.model.Voter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Ceci force Spring Data REST à inclure le champ "id" dans le JSON
        config.exposeIdsFor(Voter.class);
    }
}