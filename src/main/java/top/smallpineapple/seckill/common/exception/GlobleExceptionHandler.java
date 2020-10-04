package top.smallpineapple.seckill.common.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.smallpineapple.seckill.domain.CodeMsg;
import top.smallpineapple.seckill.domain.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局统一异常处理
 *
 * @author zengzhijie
 * @since 2020/10/4 15:46
 * @version 1.0
 */
@RestControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(value = BindException.class)
    public Result<String> bindExceptionHandler(HttpServletRequest request, BindException e) {

        ObjectError error = e.getAllErrors().get(0);
        String msg = error.getDefaultMessage();
        return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));

    }

    @ExceptionHandler(value = GlobleException.class)
    public Result<String> globleExceptionHandler(HttpServletRequest request, GlobleException e) {
        return Result.error(e.getCm());
    }

}
