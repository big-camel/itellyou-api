package com.itellyou.api.handler;

import com.itellyou.model.user.UserInfoModel;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class TokenAuthenticationToken extends AbstractAuthenticationToken {

    private String token;
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }

    private UserInfoModel user;

    public void setUser(UserInfoModel user){
        this.user = user;
    }

    public UserInfoModel getUser(){
        return this.user;
    }

    public TokenAuthenticationToken(String token){
        super(null);
        setToken(token);
    }

    public TokenAuthenticationToken(String token,UserInfoModel user,Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        setToken(token);
        setUser(user);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
