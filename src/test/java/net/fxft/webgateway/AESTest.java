package net.fxft.webgateway;

import net.fxft.webgateway.license.AESUtil;
import org.junit.Test;

public class AESTest {

    @Test
    public void encode() {
        String encstr = AESUtil.encrypt("http://112.124.202.93:8012/license/updateLicense");
        System.out.println(encstr);
        System.out.println(AESUtil.decrypt(encstr));
        String 位置 = "12111";
        System.out.println(位置);
    }

}
