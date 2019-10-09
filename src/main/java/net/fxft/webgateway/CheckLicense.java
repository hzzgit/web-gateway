package net.fxft.webgateway;

import net.fxft.cloud.spring.AfterStartedRunner;
import net.fxft.webgateway.license.LicenseUtil;
import net.fxft.webgateway.license.LicenseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckLicense implements AfterStartedRunner {

    private static final Logger log = LoggerFactory.getLogger(CheckLicense.class);

    @Autowired
    private LicenseUtil licenseUtil;
    @Autowired
    private LicenseValidator licenseValidator;


    @Override
    @Scheduled(cron = "0 1 0 * * ?")
    public void run() throws Exception {
        try {
            log.info("开始更新License！");
            long l1 = System.currentTimeMillis();
            licenseUtil.register();
            long l2 = System.currentTimeMillis();
            log.info("完成更新License！耗时=" + (l2 - l1));
        } catch (Exception e) {
            log.error("更新license出错！", e);
        }
        try {
            log.info("开始验证License！");
            long l1 = System.currentTimeMillis();
            licenseValidator.verify();
            long l2 = System.currentTimeMillis();
            log.info("完成验证License！耗时=" + (l2 - l1));
        } catch (Exception e) {
            log.error("验证license出错！", e);
        }
    }
}
