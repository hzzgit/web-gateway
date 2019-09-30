
package net.fxft.webgateway.license;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class License implements BytesAble {
    private static final long serialVersionUID = 2755024287326915954L;

    /**
     * 主题 (subject)
     */
    private String subject;

    /**
     * 签发人 (issuer)
     */
    private String issuer;

    /**
     * 受众 (audience)
     */
    private String audience;

    /**
     * 签发时间 (issued at)
     */
    private Date issuedAt;

    /**
     * 生效时间 (not before at)
     */
    private Date notBeforeAt;

    /**
     * 过期时间 (expiration at)
     */
    private Date expirationAt;

    /**
     * 是否不限制过期时间，0限制1不限制
     */
    private int expirationState = 0;

    /**
     * 设备数 (device amount)
     */
    private int deviceAmount = 0;

    /**
     * jwt秘钥
     */
    private String jwtSecretKey;

    /**
     * 扩展信息 (extra info)
     */
    private SystemData extraInfo;
}
