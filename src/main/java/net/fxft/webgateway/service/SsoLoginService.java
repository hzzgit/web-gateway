package net.fxft.webgateway.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * @ClassName SsoLoginService
 * @Author zwj
 * @Description 单点登录逻辑类
 * @Date 2020/2/17 11:23
 */
@Service
public class SsoLoginService {


    /**
     * 验证签名
     * @param loginName
     * @param pwd
     * @param signatureTime
     * @param signature
     * @return
     */
    public boolean verifySsoSignature(String loginName, String pwd, long signatureTime, String signature) {
        String ssoLoginSignature = getSsoLoginSignature(loginName, pwd, signatureTime);
        return ssoLoginSignature.equals(signature);
    }

    /**
     * 获取单点登录签名
     *
     * @param loginName
     * @param pwd
     * @param signatureTime
     * @return
     */
    private static String getSsoLoginSignature(String loginName, String pwd, long signatureTime) {
        return DigestUtils.md5Hex(loginName + pwd + signatureTime);
    }

}
