package com.loocc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理类，拦截controller中出现的异常
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 此方法处理exception的异常(所有异常，也可以写它的子类，使它范围变小)
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception exception)throws Exception{
        //{}是占位符，可以传入参数
        logger.error("Request URL：{}，Exception : {}",request.getRequestURL(),exception);
        //findAnnotation找exception实际的类的上面有没有ResponseStatus注解，有就not null，无就null
        if(AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class) != null){
                throw exception;
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("url",request.getRequestURL());
        mv.addObject("exception",exception);
        mv.setViewName("error/error");
        return mv;
    }
}
