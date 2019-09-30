package net.fxft.webgateway.license;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.PlatformEnum;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.FileUtil;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.platform.windows.WmiUtil;

/**
 * @author Sordors
 * @date 2019\9\20 0020 11:01
 */
public class SystemUtil {
    /**
     * 获取当前系统信息
     *
     * @return
     */
    public static SystemData getSystemData() {
        SystemInfo systemInfo = new SystemInfo();
        SystemData systemData = new SystemData();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        hardware.getComputerSystem();
        hardware.getProcessor();
        hardware.getMemory();
        hardware.getSensors();
        systemData.setOs(operatingSystem);
        systemData.setHardware(hardware);
        CentralProcessor centralProcessor = hardware.getProcessor();
        String processorId = centralProcessor != null ? centralProcessor.getProcessorID() : "";
        systemData.setProcessorId(processorId);
        systemData.setSystemUuId(getSystemUuid());
        return systemData;
    }

    /**
     * 获取系统唯一标识
     *
     * @return
     */
    private static String getSystemUuid() {
        PlatformEnum currentOs = SystemInfo.getCurrentPlatformEnum();
        String systemUuid = "";
        if (currentOs.name().equals(PlatformEnum.WINDOWS.name())) {
            WbemcliUtil.WmiQuery<BaseCsproductProperty> baseCsproductQuery = new WbemcliUtil.WmiQuery("win32_computersystemproduct", BaseCsproductProperty.class);
            WbemcliUtil.WmiResult<BaseCsproductProperty> win32Computersystemproduct = WmiQueryHandler.createInstance().queryWMI(baseCsproductQuery);
            systemUuid = WmiUtil.getString(win32Computersystemproduct, BaseCsproductProperty.UUID, 0);
        }

        if (currentOs.name().equals(PlatformEnum.LINUX.name())) {
            systemUuid = FileUtil.getStringFromFile("/sys/devices/virtual/dmi/id/product_uuid").trim();
        }
        return systemUuid;
    }


}
