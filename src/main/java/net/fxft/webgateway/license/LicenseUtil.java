package net.fxft.webgateway.license;

import net.fxft.common.util.JacksonUtil;
import net.fxft.webgateway.CheckLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystemVersion;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sordors
 * @date 2019\9\20 0020 14:03
 */
@Service
@RestController
@RequestMapping("/license")
public class LicenseUtil {

    private static final Logger log = LoggerFactory.getLogger(LicenseUtil.class);

    @Autowired
    private LicenseConfig licenseConfig;
    @Autowired
    private CheckLicense checkLicense;


    /**
     * 注册
     *
     * @throws LicenseException
     */
    public  void register() throws LicenseException {
        String url = licenseConfig.getRegisterUrl();
        String authIp = licenseConfig.getAuthIp();
        Integer authPort = licenseConfig.getAuthPort();
        String code = licenseConfig.getCode();
        String notifyUrl = licenseConfig.getAuthNotifyUrl();
        register(url, code, authIp, authPort, notifyUrl);
    }

//    /**
//     * @param code
//     * @throws LicenseException
//     */
//    public void register(String code) throws LicenseException {
//        String url = licenseConfig.getRegisterUrl();
//        String authIp = licenseConfig.getAuthIp();
//        Integer authPort = licenseConfig.getAuthPort();
//        String notifyUrl = licenseConfig.getAuthNotifyUrl();
//        register(url, code, authIp, authPort, notifyUrl);
//    }

//    /**
//     * 注册
//     *
//     * @param authIp
//     * @param code
//     * @throws LicenseException
//     */
//    public void register(String code, String authIp) throws LicenseException {
//        String url = licenseConfig.getRegisterUrl();
//        Integer authPort = licenseConfig.getAuthPort();
//        String notifyUrl = licenseConfig.getAuthNotifyUrl();
//        register(url, code, authIp, authPort, notifyUrl);
//    }

//    /**
//     * 注册
//     *
//     * @param authIp
//     * @param authPort
//     * @param code
//     * @throws LicenseException
//     */
//    public void register(String code, String authIp, Integer authPort) throws LicenseException {
//        String url = licenseConfig.getRegisterUrl();
//        String notifyUrl = licenseConfig.getAuthNotifyUrl();
//        register(url, code, authIp, authPort, notifyUrl);
//    }

//    /**
//     * 注册
//     *
//     * @param code
//     * @param authIp
//     * @param authPort
//     * @param notifyUrl
//     * @throws LicenseException
//     */
//    public void register(String code, String authIp, Integer authPort, String notifyUrl) throws LicenseException {
//        String url = licenseConfig.getRegisterUrl();
//        register(url, code, authIp, authPort, notifyUrl);
//    }

    /**
     * 注册
     *
     * @param url
     * @param code
     * @param authIp
     * @param authPort
     * @param notifyUrl
     * @throws LicenseException
     */
    private void register(String url, String code, String authIp, Integer authPort, String notifyUrl) throws LicenseException {
        if (url == null || url.equals("")) {
            log.info("没有配置授权服务器地址，更新License失败！");
            return;
        }
        SystemData systemData = SystemUtil.getSystemData();
        if (systemData.getSystemUuId() == null || systemData.getSystemUuId().equals("")) {
            throw new LicenseException("Not found SystemUuId, Please use administrator privileges to run the program.");
        }
        SystemDataUpload sdup = new SystemDataUpload();
        sdup.setSystemUuId(systemData.getSystemUuId());
        sdup.setProcessorId(systemData.getProcessorId());
        sdup.setOsName(Optional.of(systemData).map(SystemData::getOs).map(OperatingSystem::getFamily).orElse(""));
        sdup.setOsVersion(Optional.of(systemData).map(SystemData::getOs).map(OperatingSystem::getVersion).map(OperatingSystemVersion::getVersion).orElse(""));
        sdup.setCpuName(Optional.of(systemData).map(SystemData::getHardware).map(HardwareAbstractionLayer::getProcessor)
                .map(CentralProcessor::getName).orElse(""));
        sdup.setPhysicalProcessorCount(Optional.of(systemData).map(SystemData::getHardware).map(HardwareAbstractionLayer::getProcessor)
                .map(CentralProcessor::getPhysicalProcessorCount).orElse(0));
        sdup.setLogicalProcessorCount(Optional.of(systemData).map(SystemData::getHardware).map(HardwareAbstractionLayer::getProcessor)
                .map(CentralProcessor::getLogicalProcessorCount).orElse(0));
        sdup.setMemoryTotal(Optional.of(systemData).map(SystemData::getHardware).map(HardwareAbstractionLayer::getMemory)
                .map(GlobalMemory::getTotal).orElse(0L));
        sdup.setMemoryAvaliable(Optional.of(systemData).map(SystemData::getHardware).map(HardwareAbstractionLayer::getMemory)
                .map(GlobalMemory::getAvailable).orElse(0L));

        log.info("系统UUID=" + systemData.getSystemUuId() + "; code=" + code);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
//        map.add("systemData", Base64.getEncoder().encodeToString(systemData.toBytes()));
        map.put("systemData", sdup);

        if (code != null && !"".equals(code)) {
            map.put("code", code);
        }

        if (authIp != null && !"".equals(authIp)) {
            map.put("authIp", authIp);
        }

        if (notifyUrl != null && !"".equals(notifyUrl)) {
            map.put("notifyUrl", notifyUrl);
        }

        if (authPort != null && authPort > 0) {
            map.put("authPort", authPort);
        }
        MultiValueMap<String, Object> postParams = new LinkedMultiValueMap();
        String json = JacksonUtil.toJsonString(map);
        postParams.add("value", AESUtil.encrypt(json));

        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.postForObject(url, postParams, String.class);
            log.info("register返回值：" + response);
            saveLicenseFile(response);
        } catch (HttpClientErrorException e) {
            throw new LicenseException("the activity server response an error.", e);
        } catch (HttpServerErrorException e) {
            throw new LicenseException(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new LicenseException("register error!", e);
        }
    }

    @RequestMapping("updateLicense")
    public void updateLicense(String license) {
        try {
            log.info("服务端更新License！"+license);
            saveLicenseFile(license);
        } catch (Exception e) {
            log.error("服务端updateLicense出错！", e);
        }
        //再去服务端下载个License，然后验证
        try {
            checkLicense.run();
        } catch (Exception e) {
            log.error("checkLicense出错！", e);
        }
    }

    /**
     * 保存证书
     *
     * @param license
     * @throws IOException
     */
    private void saveLicenseFile(String license) throws Exception{
        String filePath = licenseConfig.getFilePath();
        File licenseFileDir = new File(filePath);
        //递归创建目录
        if (!licenseFileDir.exists()) {
            licenseFileDir.mkdirs();
        }
        String fileName = licenseFileDir.getAbsolutePath() + "/" + licenseConfig.FILE_NAME;
        File licenseFile = new File(fileName);
        if (!licenseFile.exists()) {
            try {
                licenseFile.createNewFile();
            } catch (IOException e) {
                throw e;
            }
        }

        //写入文件
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            license = license == null ? "" : license;
            bw.write(license);
            bw.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 读取证书
     *
     * @return
     * @throws LicenseException
     */
    public License loadLicense() throws LicenseException {
        String filePath = licenseConfig.getFilePath();
        File licenseFileDir = new File(filePath);
        String fileName = licenseFileDir.getAbsolutePath() + "/" + licenseConfig.FILE_NAME;
        String license = "";
        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            throw new LicenseException("license file does not exist.");
        }
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(read);
            license = bufferedReader.readLine();
            bufferedReader.close();
            read.close();
        } catch (IOException e) {
            throw new LicenseException("license file read error.");
        }
        if (license.isEmpty()) {
            throw new LicenseException("license file has been corrupted.");
        }
        License bean = LicenseDecoder.decode(license);
        return bean;
    }
}
