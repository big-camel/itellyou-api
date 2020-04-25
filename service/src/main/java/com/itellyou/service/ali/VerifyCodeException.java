package com.itellyou.service.ali;

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
