package com.itellyou.api.handler;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.user.UserRoleService;
import com.itellyou.service.user.UserTokenService;
import com.itellyou.util.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final UserTokenService tokenService;
    private final UserRoleService roleService;

    public TokenAuthenticationProvider(UserTokenService tokenService, UserRoleService roleService) {
        this.tokenService = tokenService;
        this.roleService = roleService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        TokenAuthenticationToken authenticationToken = (TokenAuthenticationToken) authentication;

        String token = authenticationToken.getToken();
        UserInfoModel user = null;
        if(StringUtils.isNotEmpty(token)){
            user = tokenService.find(token);
            if(user != null){
                List<SysRoleModel> roleModels = roleService.findRoleByUserId(user.getId(),true);
                for (SysRoleModel roleModel : roleModels){
                    grantedAuthorities.add(new SimpleGrantedAuthority(roleModel.getId().toString()));
                }
                // 登录用户默认都有 user 角色
                grantedAuthorities.add(new SimpleGrantedAuthority("2"));
            }
        }
        // 默认都拥有游客 角色
        grantedAuthorities.add(new SimpleGrantedAuthority("1"));
        TokenAuthenticationToken authenticationResult = new TokenAuthenticationToken(token,user,grantedAuthorities);
        authenticationResult.setAuthenticated(true);
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
