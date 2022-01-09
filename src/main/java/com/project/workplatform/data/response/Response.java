package com.project.workplatform.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author MercerJR
 * @Data 2022/1/8 10:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private Integer code;
    private String message;
    private Object data;

    public Response success(Object data) {
        Response response = new Response();
        response.setCode(0);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    public Response success() {
        Response response = new Response();
        response.setCode(0);
        response.setMessage("操作成功");
        return response;
    }

    public Response validateFailed(String message) {
        Response response = new Response();
        response.setCode(CustomExceptionType.NORMAL_ERROR.getCode());
        response.setMessage(message);
        return response;
    }

    public Response error(CustomException e) {
        Response response = new Response();
        response.setCode(e.getCode());
        response.setMessage(e.getMessage());
        return response;
    }
}
