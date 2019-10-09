package net.fxft.webgateway.license;


import net.fxft.common.util.JacksonUtil;

import java.security.PublicKey;
import java.util.Base64;

/**
 * @author Sordors
 * @date 2019\9\20 0020 14:03
 */
public class LicenseDecoder {

    /**
     * 解密
     *
     * @param string
     * @return
     * @throws LicenseException
     */
    public static License decode(String string) throws LicenseException {
        License license;
        try {
            String[] array = string.split("\\.");
            if (array.length != 2) {
                throw new LicenseException("error license format.");
            }
            PublicKey publicKey = RsaUtil.loadPublicKey(array[0]);
            byte[] encrypted = Base64.getDecoder().decode(array[1]);
            byte[] decrypted = RsaUtil.decrypt(publicKey, encrypted);
            String jsonstr = new String(decrypted, "UTF-8");
            license = JacksonUtil.parseJsonString(jsonstr, License.class);
        } catch (Exception e) {
            throw new LicenseException("license content error.");
        }
        return license;
    }
}