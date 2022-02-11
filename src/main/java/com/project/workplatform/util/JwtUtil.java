package com.project.workplatform.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.workplatform.data.Constant;
import com.project.workplatform.exception.CustomException;
import com.project.workplatform.exception.CustomExceptionType;
import com.project.workplatform.exception.ExceptionMessage;
import com.project.workplatform.pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/8 10:19
 */
public class JwtUtil {

    private static final long EXPIRE_DATE = 24 * 60 * 60 * 1000;

    public static String createToken(User user) {
        String token = "";
        Date nowDate = new Date();
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_DATE);
        Algorithm algorithm = Algorithm.HMAC256(user.getPassword());

        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        token = JWT.create()
                .withHeader(header)
                .withIssuer(Constant.JWT_ISSUER)
                .withAudience(user.getPhoneNumber())
                .withIssuedAt(nowDate)
                .withExpiresAt(expireDate)
                .withClaim(Constant.JWT_CLAIM_ID, user.getId())
                .sign(algorithm);

        return token;
    }

    public static boolean verifyToken(String token,String userIdentify,String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(password);
            JWTVerifier jwtVerifier = JWT.require(algorithm).withAudience(userIdentify).build();
            jwtVerifier.verify(token);
            return true;
        } catch (TokenExpiredException e){
            throw new CustomException(CustomExceptionType.LOGIN_ERROR, ExceptionMessage.TOKEN_EXPIRE);
        } catch (Exception e) {
            return false;
        }
    }

    public static Integer getId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(Constant.JWT_CLAIM_ID).asInt();
        } catch (JWTDecodeException | NullPointerException e) {
            throw new CustomException(CustomExceptionType.LOGIN_ERROR,ExceptionMessage.TOKEN_INVALID);
        }
    }

    public static Integer getId(HttpServletRequest request){
        return JwtUtil.getId(request.getHeader(Constant.JWT_TOKEN));
    }

}
