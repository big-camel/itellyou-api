package com.itellyou.util.argument;

import com.itellyou.util.annotation.MultiRequestBody;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private Class clazz;
    public UserArgumentResolver(Class clazz){
        this.clazz = clazz;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(
                parameter.hasParameterAnnotation(MultiRequestBody.class) ||
                parameter.hasParameterAnnotation(RequestBody.class) ||
                        parameter.hasParameterAnnotation(RequestParam.class) ||
                        parameter.hasParameterAnnotation(PathVariable.class)
        ) return false;

        return parameter.getParameterType() == clazz;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Object user = request.getAttribute("user");
        if(user != null){
            mavContainer.addAttribute("user",user);
            return user;
        }
        return null;
    }
}
