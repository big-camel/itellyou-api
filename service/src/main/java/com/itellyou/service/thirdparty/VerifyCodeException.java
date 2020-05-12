package com.itellyou.service.thirdparty;

public class VerifyCodeException extends Exception {

    public VerifyCodeException(String message){
        super(message);
    }

    private Long seconds;

    public VerifyCodeException(Long seconds , String message){
        super(message);
        this.seconds = seconds;
    }

    public Long getSeconds(){
        return seconds;
    }
}
