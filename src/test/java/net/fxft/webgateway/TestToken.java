package net.fxft.webgateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TestToken {


    @Test
    public void test1() {
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNTY3NTc5OTQ4fQ.2nt45BG-fEvaQJ034NWA_7i1Xn8vCzzPvOQdViI_cSg";
        DecodedJWT decode = JWT.decode(token);
        Date d1 = decode.getExpiresAt();
        String str = decode.getSubject();

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("1PCiNHPvGxSkJSmc1ga90q6FjU2XW4fd")).build();
        DecodedJWT jwt = verifier.verify(token);
        Date exp = jwt.getExpiresAt();
        System.out.println(exp);

    }


    @Test
    public void test2() {
        Map<String, Integer> map = new HashMap<>();
        map.put("aaa", new Integer(10));


        System.out.println(Integer.MAX_VALUE);




    }



}
