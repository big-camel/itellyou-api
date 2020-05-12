package com.itellyou.api.handler;

import com.itellyou.model.common.ResultModel;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

public class ResultMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
    private RequestResponseBodyMethodProcessor delegate;

    public ResultMethodReturnValueHandler(RequestResponseBodyMethodProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if(returnValue instanceof ResultModel){
            ResultModel resultModel = (ResultModel)returnValue;
            delegate.handleReturnValue(resultModel.toResultJson(),returnType,mavContainer,webRequest);
        }else{
            ResultModel resultModel = new ResultModel(returnValue);
            delegate.handleReturnValue(resultModel,returnType,mavContainer,webRequest);
        }
    }
}
