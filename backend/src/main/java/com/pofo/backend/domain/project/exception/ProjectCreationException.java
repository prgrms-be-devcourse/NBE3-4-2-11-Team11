package com.pofo.backend.domain.project.exception;

import com.pofo.backend.common.rsData.RsData;

public class ProjectCreationException extends RuntimeException{
    private final String resultCode;
    private final String msg;

    public ProjectCreationException(String resultCode,String msg){
        super(resultCode + " : " + msg );
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public RsData<Void> getRsData() {
        return new RsData<>(resultCode, msg);
    }
}
