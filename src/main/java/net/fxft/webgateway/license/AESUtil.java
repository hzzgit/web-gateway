package net.fxft.webgateway.license;


import net.fxft.common.util.BasicUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class AESUtil {
    
    private static final Logger log = LoggerFactory.getLogger(AESUtil.class);
    
    private static final String UTF_8 = "utf-8";
    private static final String AES = "AES";

    private static String SECRET_PWD = "fH4rs67p8e3n6mWhb7cBZFQYM4Od6Rf4alhoJ9eoymGF1HV9";

//    static {
//        // 获取密钥
//        try {
//            String str =
//            byte[] b = str.getBytes(UTF_8);
//            SECRET_PWD = new String(new Base64().encode(b));
//            System.out.println("加密后的密钥：" + SECRET_PWD);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        String estr = AESUtil.encrypt("中俄abdre112434^&*(");
        System.out.println(estr);

        String decrypt = AESUtil.decrypt(estr);
        System.out.println(decrypt);
    }

    /**
     * AES加密
     * <p>返回 Base64 加密结果 code</p>
     * @param content 待加密的内容
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String encrypt(String content) {
        try {
            // 用AES算法加密的密钥
            SecretKeySpec key = getKey();
            // 对加密内容进行编码，并转化为字节数组
            byte[] byteContent = content.getBytes(UTF_8);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES);
            // 以加密的方式用密钥初始化此 Cipher
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 加密算法对象对明文字节数组进行加密
            byte[] byteEncrypt = cipher.doFinal(byteContent);
            // 对加密结果进行Base64在加密后返回
            return base64Encode(byteEncrypt);
        } catch (Exception e) {
            log.error("encrypt出错！", e);
            return null;
        }
    }

    /**
     * AES解密
     * <p>Base64结果解密 </p>
     * @param content 待解密的base 64 code
     * @return 解密后的string
     * @throws Exception
     */
    public static String decrypt(String content) {
        try {
            // 用AES算法加密的密钥
            SecretKeySpec key = getKey();
            // 对加密内容先进行Base64解密
            byte[] byteContent = base64Decode(content);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES);
            // 以加密的方式用密钥初始化此 Cipher
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 加密算法对象对加密内容字节数组进行解密
            byte[] byteDecrypt = cipher.doFinal(byteContent);
            return new String(byteDecrypt);
        } catch (Exception e) {
            log.error("decrypt出错！", e);
            return null;
        }
    }

    /**
     * 生成密钥
     *
     * @return SecretKeySpec 用AES算法加密的密钥
     * @throws NoSuchAlgorithmException
     */
    public static SecretKeySpec getKey() throws NoSuchAlgorithmException {
        //实例化一个用AES加密算法的密钥生成器
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
		// AES算法在windows下可以正常加密、解密，在Linux下随机生成加密的结果，因为加密的密钥是随机的，所以需要这样处理</span>
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(SECRET_PWD.getBytes());
                //使用用户提供的密钥明文（SECRET_PWD）初始化此密钥生成器，使其具有确定的密钥大小128字节长
                kgen.init(128, secureRandom);
        //生成一个密钥
        SecretKey secretKey = kgen.generateKey();
        //返回基本编码格式的密钥，如果此密钥不支持编码，则返回 null
        byte[] enCodeFormat = secretKey.getEncoded();
        //根据给定的enCodeFormat字节数组构造一个用AES算法加密的密钥
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);
        return key;
    }

    /**
     * base 64 encode
     *
     * @param bytes
     *            待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return new String(new Base64().encode(bytes));
    }

    /**
     * base 64 decode
     *
     * @param base64Code
     *            待解码的base 64 code
     * @return 解码后的byte[]
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws UnsupportedEncodingException {
        return BasicUtil.isEmpty(base64Code) ? null : new Base64().decode(base64Code.getBytes(UTF_8));
    }

}
