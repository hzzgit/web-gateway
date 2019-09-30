package net.fxft.webgateway;

import net.fxft.cloud.spring.AfterStartedRunner;
import net.fxft.webgateway.license.LicenseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckLicense implements AfterStartedRunner {

    private static final Logger log = LoggerFactory.getLogger(CheckLicense.class);

    @Autowired
    private LicenseUtil licenseUtil;

    @Override
    public void run() throws Exception {
        log.info("开始检查License！");
        long l1 = System.currentTimeMillis();
        licenseUtil.register();
        long l2 = System.currentTimeMillis();
        log.info("完成检查License！耗时=" + (l2 - l1));
    }
}
