package com.itellyou.api.handler;

import org.springframework.security.access.AccessDeniedException;

public class TokenAccessDeniedException extends AccessDeniedException {

    private int status;

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return this.status;
    }

    public TokenAccessDeniedException(){
        super("No Access");
        setStatus(401);
    }

    public TokenAccessDeniedException(int status , String msg) {
        super(msg);
        setStatus(status);
    }

    public TokenAccessDeniedException(String msg, Throwable t) {
        super(msg, t);
    }
}
