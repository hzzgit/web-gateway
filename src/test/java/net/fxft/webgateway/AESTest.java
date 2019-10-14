package net.fxft.webgateway;

import net.fxft.webgateway.license.AESUtil;
import org.junit.Test;

public class AESTest {

    @Test
    public void encode() {
        String encstr = AESUtil.encrypt("http://118.31.5.186:9200/api/register");
        System.out.println(encstr);
        System.out.println(AESUtil.decrypt(encstr));
    }

}
