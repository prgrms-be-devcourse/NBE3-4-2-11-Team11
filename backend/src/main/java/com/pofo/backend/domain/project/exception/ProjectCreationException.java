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

    // 404 오류 생성
    public static ProjectCreationException notFound(String msg) {

        return new ProjectCreationException("404", msg);
    }

    // 400 오류 생성
    public static ProjectCreationException badRequest(String msg) {
        return new ProjectCreationException("400", msg);
    }

    // 500 오류 생성
    public static ProjectCreationException serverError(String msg) {
        return new ProjectCreationException("500", msg);
    }

    //권한 없음
    public static ProjectCreationException forbidden(String msg){

        return new ProjectCreationException("403", msg);
    }
}
