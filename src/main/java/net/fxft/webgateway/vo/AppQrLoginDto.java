package net.fxft.webgateway.vo;



import java.io.Serializable;

/**
 * @Author Lirenhui
 * @Date 2019/8/20 16:25
 */

public class AppQrLoginDto implements Serializable {

    /**
     * 用户名
     * @author Lirenhui
     */
    private String loginName;

    /**
     * 密码
     * @author Lirenhui
     */
    private String password;

    /**
     * 使用该key值获取缓存数据
     * @author Lirenhui
     */
    private String key;

    /**
     * App登录标识
     * @author Lirenhui
     */
    private String appIdentifier;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }
}
