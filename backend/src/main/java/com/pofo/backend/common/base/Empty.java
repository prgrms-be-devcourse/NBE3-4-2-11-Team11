package com.pofo.backend.common.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 직렬화하지 않음
@JsonSerialize
public class Empty {
}
