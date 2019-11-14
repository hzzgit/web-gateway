package net.fxft.webgateway.license;

import net.fxft.common.log.RestExecuter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/license")
public class LicenseController {

    private static final Logger log = LoggerFactory.getLogger(LicenseController.class);
    
    @Autowired
    private LicenseUtil licenseUtil;

    @RequestMapping("/getVehicleLimitCount")
    public String getVehicleLimitCount() {
        return RestExecuter.build(log, "getVehicleLimitCount")
                .run(alog -> {
//                    License bean = licenseUtil.loadLicense();
//                    return bean.getDeviceAmount();
                    return Integer.MAX_VALUE;
                });
    }

}
