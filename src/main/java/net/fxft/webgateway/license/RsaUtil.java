package net.fxft.webgateway.license;

import javax.crypto.Cipher;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RsaUtil
 */
public class RsaUtil {
    public static final String RSA_ALGORITHM = "RSA";
    public static final int RSA_KEY_SIZE = 1024;

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;


    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 构建
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.genKeyPair();
    }

    /**
     * 解密
     *
     * @param privateKey
     * @param message
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(PrivateKey privateKey, byte[] message) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        int length = message.length;
        int blockSize = MAX_ENCRYPT_BLOCK;
        int i = 0;

        byte[] cache;

        while (length - i * blockSize > 0) {
            int size = length - i * blockSize < blockSize ? length - i * blockSize : blockSize;
            cache = cipher.doFinal(message, i * blockSize, size);
            stream.write(cache, 0, cache.length);
            i++;
        }

        byte[] encrypted = stream.toByteArray();

        stream.close();

        return encrypted;
    }

    /**
     * 解密
     *
     * @param publicKey
     * @param encrypted
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(PublicKey publicKey, byte[] encrypted) throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        int length = encrypted.length;
        int blockSize = MAX_DECRYPT_BLOCK;
        int i = 0;

        byte[] cache;

        while (length - i * blockSize > 0) {
            int size = length - i * blockSize > blockSize ? blockSize : length - i * blockSize;

            cache = cipher.doFinal(encrypted, i * blockSize, size);
            stream.write(cache, 0, cache.length);
            i++;
        }

        byte[] decrypted = stream.toByteArray();

        stream.close();

        return decrypted;
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = base64Decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = base64Decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存公钥
     *
     * @param publicKey
     * @throws IOException
     */
    public void savePublicKey(PublicKey publicKey) throws IOException {
        // 得到公钥字符串
        String publicKeyString = base64Encode(publicKey.getEncoded());
        FileWriter fw = new FileWriter("publicKey.keystore");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(publicKeyString);
        bw.close();
    }

    /**
     * 保存私钥
     *
     * @param privateKey
     * @throws IOException
     */
    public void savePrivateKey(PrivateKey privateKey) throws IOException {
        // 得到私钥字符串
        String privateKeyString = base64Encode(privateKey.getEncoded());
        BufferedWriter bw = new BufferedWriter(new FileWriter("privateKey.keystore"));
        bw.write(privateKeyString);
        bw.close();
    }

    /**
     * 生成base64
     *
     * @param data
     * @return
     */
    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 解密base64
     *
     * @param data
     * @return
     *
     * @throws IOException
     */
    public static byte[] base64Decode(String data) throws IOException {
        return Base64.getDecoder().decode(data);
    }

}
