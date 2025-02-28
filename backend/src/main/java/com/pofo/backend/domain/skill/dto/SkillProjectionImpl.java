package com.pofo.backend.domain.skill.dto;

public class SkillProjectionImpl implements SkillProjection {
    private final Long id;
    private final String name;

    public SkillProjectionImpl(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
