package com.pofo.backend.common.base;

import com.pofo.backend.domain.skill.service.SkillService;
import com.pofo.backend.domain.tool.service.ToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;
    private final SkillService skillService;
    private final ToolService toolService;

    @Bean
    public ApplicationRunner devInitDataApplicationRunner() {
        return args -> {
            skillService.save();
            toolService.save();
        };
    }
}
