package net.fxft.webgateway.util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;

/**
 * 身份证验证码
 * @author Lirenhui
 * @date 2019/3/26 11:20
 */
public final class AuthenticationCodeUtil {

    /** 时间前后偏移量 */
    private static final int timeExcursion = 3;

    /**
     * 校验方法
     * @param secretKey 密钥
     * @param code      用户输入的 TOTP 验证码
     */
    public static boolean verify(String secretKey, String code) {
        long time = System.currentTimeMillis() / 1000 / 30;
        for (int i = -timeExcursion; i <= timeExcursion; i++) {

            String totp = getTOTP(secretKey, time + i);

            if (code.equals(DigestUtils.md5Hex(totp))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据密钥获取验证码
     * 返回字符串是因为验证码有可能以 0 开头
     * @param secretKey 密钥
     * @param time      第几个 30 秒 System.currentTimeMillis() / 1000 / 30
     */
    public static String getTOTP(String secretKey, long time) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey.toUpperCase());
        String hexKey = Hex.encodeHexString(bytes);
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }

    /**
     * 随机生成一个密钥
     */
    public static String createSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);
        return secretKey.toLowerCase();
    }

    public static void main(String[] args) {
//        for(int i=0 ; i<20 ; i++) {
//            System.out.println(createSecretKey());
//        }
        genSecretTest();
    }

    public static void genSecretTest() {// 生成密钥
        String secret=createSecretKey();
        // 把这个qrcode生成二维码，用google身份验证器扫描二维码就能添加成功
        String format = "otpauth://totp/%s?secret=%s";
        String format1 = String.format(format, "2816661736@qq.com", secret);
        System.out.println("qrcode:" + format1 + ",key:" + secret);
    }
}
