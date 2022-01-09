package com.project.workplatform.exception;

/**
 * @author Mercer JR
 */

public enum CustomExceptionType {
    /**
     * 系统内部错误
     */
    SYSTEM_ERROR(5,"系统内部错误"),
    /**
     * 参数校验错误
     */
    NORMAL_ERROR(1,"普通错误"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(9,"未知错误");

    private Integer code;
    private String typeDesc;


    CustomExceptionType(Integer code, String typeDesc){
        this.code = code;
        this.typeDesc = typeDesc;
    }

    public String getTypeDesc(){
        return this.typeDesc;
    }

    public Integer getCode(){
        return this.code;
    }
}