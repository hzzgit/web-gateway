package net.fxft.webgateway.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SsoLoginDto
 * @Author zwj
 * @Description 单点登录Dto
 * @Date 2020/2/17 10:35
 */
public class SsoLoginDto implements Serializable {
    /**
     * 登录名
     */
    private String loginName;

    /**
     * 签名
     */
    private String signature;

    /**
     * 签名时间戳
     */
    private long signatureTime;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getSignatureTime() {
        return signatureTime;
    }

    public void setSignatureTime(long signatureTime) {
        this.signatureTime = signatureTime;
    }
}
