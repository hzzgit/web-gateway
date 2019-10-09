package net.fxft.webgateway.license;

import lombok.Data;

@Data
public class SystemDataUpload {
    private String osName ;
    private String osVersion ;
    private String cpuName;
    private int physicalProcessorCount;
    private int logicalProcessorCount;
    private long memoryTotal;
    private long memoryAvaliable;
    /**
     * 系统唯一标识码
     */
    private String systemUuId;
    /**
     * 处理器ID
     */
    private String processorId;


}
