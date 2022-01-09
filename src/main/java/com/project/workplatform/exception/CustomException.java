package com.project.workplatform.exception;

/**
 * @author Mercer JR
 */
public class CustomException extends RuntimeException {

    private Integer code;
    private String message;

    public CustomException(CustomExceptionType exceptionType,String message){
        this.code = exceptionType.getCode();
        this.message = message;
    }

    @Override
    public String getMessage(){
        return this.message;
    }

    public Integer getCode(){
        return this.code;
    }
}