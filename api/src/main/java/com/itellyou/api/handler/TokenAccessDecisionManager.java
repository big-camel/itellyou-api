package com.itellyou.api.handler;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.util.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;

@Component
public class TokenAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        if (authentication == null) {
            throw new TokenAccessDeniedException(HttpServletResponse.SC_UNAUTHORIZED,"No Access");
        }
        TokenAuthenticationToken authenticationToken = (TokenAuthenticationToken) authentication;
        Collection<? extends GrantedAuthority> authorities = authenticationToken.getAuthorities();

        UserInfoModel user = authenticationToken.getUser();
        FilterInvocation filterInvocation = (FilterInvocation)object;
        if(user != null){
            HttpServletRequest request = filterInvocation.getRequest();
            request.setAttribute("user",user);
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            SysRoleModel roleModel = (SysRoleModel)iterator.next();
            for (GrantedAuthority authority : authorities) {
                if (StringUtils.equals(authority.getAuthority(), roleModel.getId().toString())) {
                    return;
                }
            }
        }
        throw new TokenAccessDeniedException(HttpServletResponse.SC_UNAUTHORIZED,"No Access");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
