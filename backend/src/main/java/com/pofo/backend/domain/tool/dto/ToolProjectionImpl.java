package com.pofo.backend.domain.tool.dto;

public class ToolProjectionImpl implements ToolProjection {
    private final Long id;
    private final String name;

    public ToolProjectionImpl(Long id, String name) {
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
