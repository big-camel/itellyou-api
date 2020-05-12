package com.itellyou.api.handler;

import com.itellyou.model.sys.*;
import com.itellyou.service.sys.SysPermissionService;
import com.itellyou.service.sys.SysRolePermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class TokenSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SysPermissionService permissionService;
    private final SysRolePermissionService rolePermissionService;

    public TokenSecurityMetadataSource(SysPermissionService permissionService, SysRolePermissionService rolePermissionService) {
        this.permissionService = permissionService;
        this.rolePermissionService = rolePermissionService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getHttpRequest();

        String method = request.getMethod();
        List<SysPermissionModel> permissionModelList = permissionService.search(null,null, SysPermissionType.URL, SysPermissionMethod.valueOf(method.toUpperCase()),null,null,null,null);
        for (SysPermissionModel permissionModel : permissionModelList){
            RequestMatcher requestMatcher = new AntPathRequestMatcher(permissionModel.getData());
            if (requestMatcher.matches(request)) {
                Collection<ConfigAttribute> configAttributes = new ArrayList<>();

                List<SysRoleModel> roleModels = rolePermissionService.findRoleByName(permissionModel.getName());
                if(roleModels.size() > 0){
                    configAttributes.addAll(roleModels);
                    return configAttributes;
                }
            }
        }
        throw new TokenAccessDeniedException(HttpServletResponse.SC_FORBIDDEN,"No Access");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
