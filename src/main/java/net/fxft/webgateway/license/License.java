
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
public class License  {
    private static final long serialVersionUID = 1L;

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
    private long issuedAt;

    /**
     * 生效时间 (not before at)
     */
    private long notBeforeAt;

    /**
     * 过期时间 (expiration at)
     */
    private long expirationAt;

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
     * 系统唯一标识码
     */
    private String systemUuId;

    /**
     * 处理器ID
     */
    private String processorId;
}
