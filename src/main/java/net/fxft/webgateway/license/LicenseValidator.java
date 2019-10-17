package net.fxft.webgateway.license;

import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.util.JacksonUtil;
import net.fxft.webgateway.jwt.JwtDecoder;
import net.fxft.webgateway.jwt.JwtEncoder;
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

    private boolean stopService = false;

    @Autowired
    private LicenseUtil licenseUtil;
    @Autowired
    private JdbcUtil jdbc;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;


    /**
     * 验证证书合法性
     *
     */
    public void verify(){
        //文件读取失败即校验失败
        License bean;
        try {
            bean = licenseUtil.loadLicense();
            log.info("license=" + JacksonUtil.toJsonString(bean));
            long now = System.currentTimeMillis();
//            if (bean.getNotBeforeAt() > (now)) {
//                throw new LicenseException("License未到生效时间！activeTime=" + bean.getNotBeforeAt());
//            }
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
            if (stopService) {
                log.info("验证证书成功，重新开启服务！");
                stopService = false;
            }
            jwtEncoder.updateJwtSecret(bean.getJwtSecretKey());
            jwtDecoder.updateJwtSecret(bean.getJwtSecretKey());
        } catch (LicenseException e) {
            log.error("验证证书失败，停止服务！", e);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            stopService = true;
//            System.exit(0);
        }
    }

    public final boolean isStopService() {
        return stopService;
    }

}