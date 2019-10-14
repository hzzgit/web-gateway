package net.fxft.webgateway.license;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LicenseConfig {
//    private static final String FILE_PATH = "cert";
    public static final String FILE_NAME = "ascs.license";
//    public static final String SUBJECT = "fxft-ascs-license";
//    public static final String ISSUER = "fxft";

    @Value("${license.code}")
    private String code;
    @Value("${license.registerUrl}")
    private String registerUrl;
    @Value("${license.authIp}")
    private String authIp;
    @Value("${license.authPort}")
    private int authPort;
    @Value("${license.authNotifyUrl}")
    private String authNotifyUrl;
    @Value("${license.filePath}")
    private String filePath;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        if (registerUrl != null && registerUrl.length() > 0) {
            this.registerUrl = AESUtil.decrypt(registerUrl);
        }
    }

    public String getAuthIp() {
        return authIp;
    }

    public void setAuthIp(String authIp) {
        this.authIp = authIp;
    }

    public int getAuthPort() {
        return authPort;
    }

    public void setAuthPort(int authPort) {
        this.authPort = authPort;
    }

    public String getAuthNotifyUrl() {
        return authNotifyUrl;
    }

    public void setAuthNotifyUrl(String authNotifyUrl) {
        if(authNotifyUrl != null && authNotifyUrl.length() > 0) {
            this.authNotifyUrl = AESUtil.decrypt(authNotifyUrl);
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
