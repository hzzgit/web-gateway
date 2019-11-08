package net.fxft.webgateway.license;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LicenseConfig {
//    private static final String FILE_PATH = "cert";
    public static final String FILE_NAME = "ascs.license";
//    public static final String SUBJECT = "fxft-ascs-license";
//    public static final String ISSUER = "fxft";

    @Value("${license.code:}")
    private String code;
    @Value("${license.registerUrl:}")
    private String registerUrl;
    @Value("${license.authIp:}")
    private String authIp;
    @Value("${license.authPort:}")
    private String authPort;
    @Value("${license.authNotifyUrl:}")
    private String authNotifyUrl;
    @Value("${license.filePath:}")
    private String filePath;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegisterUrl() {
        if (registerUrl != null && registerUrl.length() > 0) {
            return AESUtil.decrypt(registerUrl);
        }
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getAuthIp() {
        return authIp;
    }

    public void setAuthIp(String authIp) {
        this.authIp = authIp;
    }

    public String getAuthPort() {
        return authPort;
    }

    public void setAuthPort(String authPort) {
        this.authPort = authPort;
    }

    public String getAuthNotifyUrl() {
        if(authNotifyUrl != null && authNotifyUrl.length() > 0) {
            return AESUtil.decrypt(authNotifyUrl);
        }
        return authNotifyUrl;
    }

    public void setAuthNotifyUrl(String authNotifyUrl) {
        this.authNotifyUrl = authNotifyUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
