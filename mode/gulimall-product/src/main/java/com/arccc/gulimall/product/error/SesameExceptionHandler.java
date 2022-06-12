package com.arccc.gulimall.product.error;


import com.arccc.common.error.BizCodeEnume;
import com.arccc.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一异常处理
 */
@RestControllerAdvice
@Slf4j
public class SesameExceptionHandler {


    @ExceptionHandler(Exception.class)
    public R error(Exception e){
        e.printStackTrace();
        return R.error(BizCodeEnume.UNKONW_EXCEPTION.getCode(), BizCodeEnume.UNKONW_EXCEPTION.getMsg()).put("error",e.getMessage());
    }

//
//    @ExceptionHandler(SesameException.class)
//    @ResponseBody
//    public R error(SesameException e){
//
//        log.error(e.getMessage());
//        return R.error().message(e.getMessage()).code(e.getCode());
//    }


    //sql异常
    @ExceptionHandler(BadSqlGrammarException.class)
    public R error(BadSqlGrammarException e){
        e.printStackTrace();
        return R.error().put("error",e);
    }

    //数据校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R error(MethodArgumentNotValidException e){
        e.printStackTrace();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String,String > msg = new HashMap<>();
        fieldErrors.forEach(i-> msg.put(i.getField(),i.getDefaultMessage()));
//        //多个错误，取第一个
//        FieldError error = fieldErrors.get(0);
//        String msg = error.getDefaultMessage();
//        return R.setResult(ResultCodeEnum.PARAM_ERROR).message(msg);
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",msg);
    }



}
