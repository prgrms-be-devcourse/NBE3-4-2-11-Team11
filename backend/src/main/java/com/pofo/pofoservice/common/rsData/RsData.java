package com.pofo.pofoservice.common.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pofo.backend.common.base.Empty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class RsData<T> {
	public static final RsData<Empty> OK = new RsData<>("200", "OK", new Empty());

	@NonNull
	private final String resultCode;

	@NonNull
	private final String msg;

	@NonNull
	private final T data;

	public RsData(String resultCode, String msg) {
		this(resultCode, msg, (T) new Empty());
	}

	@JsonIgnore
	public int getStatusCode() {
		return Integer.parseInt(resultCode);
	}

	@JsonIgnore
	public boolean isSuccess() {
		return getStatusCode() < 400;
	}

	@JsonIgnore
	public boolean isFail() {
		return !isSuccess();
	}
}