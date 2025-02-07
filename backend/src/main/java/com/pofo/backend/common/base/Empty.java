package com.pofo.backend.common.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonInclude(JsonInclude.Include.NON_EMPTY) // null 값은 직렬화하지 않음
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class Empty {

    public Empty() {
    }
}
