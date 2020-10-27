package com.itellyou.api.handler;

import com.itellyou.model.common.ResultModel;
import com.itellyou.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class ExceptionHandle {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理请求对象属性不满足校验规则的异常信息
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultModel exception(HttpServletRequest request, MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder builder = new StringBuilder();

        for (FieldError error : fieldErrors) {
            builder.append(error.getField() + " " + error.getDefaultMessage() + "\n");
        }
        return new ResultModel(500, "数据校验失败：" + builder.toString());
    }

    /**
     * 处理请求单个参数不满足校验规则的异常信息
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResultModel constraintViolationExceptionHandler(HttpServletRequest request, ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("数据校验失败");
        if(!CollectionUtils.isEmpty(constraintViolations)){
            msgBuilder.append(":");
            for(ConstraintViolation constraintViolation :constraintViolations){
                String path = constraintViolation.getPropertyPath().toString();
                String[] actions = path.split("\\.");
                String paramName = path;
                if(actions.length > 1){
                    paramName = actions[actions.length - 1];
                }
                msgBuilder.append(paramName + " " + constraintViolation.getMessage() + "\n");
            }
        }
        return new ResultModel(500,msgBuilder.toString());
    }

    /**
     * 处理无权限异常
     *
     * @param response
     * @param exception
     * @return
     */
    @ExceptionHandler(value = TokenAccessDeniedException.class)
    public ResultModel accessException(HttpServletResponse response, TokenAccessDeniedException exception) {
        String message = exception.getMessage();
        if(StringUtils.isEmpty(message)) message = "系统错误";
        logger.error(message);
        response.setStatus(exception.getStatus());
        return new ResultModel(exception.getStatus(), message);
    }

    /**
     * 处理未定义的其他异常信息
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResultModel exceptionHandler(Exception exception) {
        exception.printStackTrace();
        String message = exception.getMessage();
        if(message == null)
            message = "系统错误";
        logger.error(message);
        return new ResultModel(500, message);
    }

}
