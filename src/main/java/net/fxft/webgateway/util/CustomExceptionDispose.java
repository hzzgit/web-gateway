package net.fxft.webgateway.util;

import net.fxft.webgateway.vo.JsonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @ClassName: CustomExceptionDispose
 * @Description: 自定义异常拦截类
 * @Author: wenjian.zhang
 * @Date: 2019/5/5 16:02
 **/

@ControllerAdvice
public class CustomExceptionDispose {
    private static final Logger log = LoggerFactory.getLogger(CustomExceptionDispose.class);

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public JsonMessage throwCustomException(CustomException c){
        return new JsonMessage(c.isCode(), c.getMessage(), c.getData());
    }
}
