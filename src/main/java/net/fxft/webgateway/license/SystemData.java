package net.fxft.webgateway.license;

import lombok.Data;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

/**
 * @author Sordors
 * @date 2019\9\20 0020 11:07
 */
@Data
public class SystemData implements BytesAble {
    /**
     * 操作系统
     */
    private OperatingSystem os = null;
    /**
     * 硬件
     */
    private HardwareAbstractionLayer hardware = null;

    /**
     * 系统唯一标识码
     */
    private String systemUuId;

    /**
     * 处理器ID
     */
    private String processorId;

}
