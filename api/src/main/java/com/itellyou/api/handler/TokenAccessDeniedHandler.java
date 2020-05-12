package com.itellyou.api.handler;

import com.itellyou.model.common.ResultModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        if(accessDeniedException instanceof TokenAccessDeniedException){
            TokenAccessDeniedException tokenAccessDeniedException = (TokenAccessDeniedException)accessDeniedException;
            status = tokenAccessDeniedException.getStatus();
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(status);
        response.getWriter().print(new ResultModel(status,accessDeniedException.getLocalizedMessage()));
        response.getWriter().flush();
    }
}
