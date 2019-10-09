package net.fxft.webgateway.license;

import net.fxft.common.jdbc.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LicenseValidator
 *
 * @author Administrator
 */
@Service
public class LicenseValidator {

    private static final Logger log = LoggerFactory.getLogger(LicenseValidator.class);
    
    @Autowired
    private LicenseUtil licenseUtil;
    @Autowired
    private JdbcUtil jdbc;

    /**
     * 验证证书合法性
     *
     */
    public void verify(){
        //文件读取失败即校验失败
        License bean;
        try {
            bean = licenseUtil.loadLicense();
            long now = System.currentTimeMillis();
            if (bean.getNotBeforeAt() > (now)) {
                throw new LicenseException("License未到生效时间！activeTime=" + bean.getNotBeforeAt());
            }
            if (bean.getExpirationState() == 0 && bean.getExpirationAt() < (now)) {
                throw new LicenseException("License已过期！expire=" + bean.getExpirationAt());
            }
            SystemData systemData = SystemUtil.getSystemData();
            //校验系统UUID
            if (!systemData.getSystemUuId().equals(bean.getSystemUuId())) {
                throw new LicenseException("系统UUID不匹配！thisUUID=" + systemData.getSystemUuId());
            }
            //校验系统CPUID
            if (!systemData.getProcessorId().equals(bean.getProcessorId())) {
                throw new LicenseException("ProcessorId不匹配！thisProcessorId=" + systemData.getProcessorId());
            }
            String sql = "select count(*) from vehicle where deleted=false";
            int deviceCount = jdbc.sql(sql).queryOneInt();
            if (deviceCount > bean.getDeviceAmount()) {
                throw new LicenseException("设备数量超过限制！limit=" + bean.getDeviceAmount() + "; real=" + deviceCount);
            }
        } catch (LicenseException e) {
            log.error("验证证书失败，强制退出！", e);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }
    }

}