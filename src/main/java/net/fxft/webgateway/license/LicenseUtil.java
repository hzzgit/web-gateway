package net.fxft.webgateway.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Base64;

/**
 * @author Sordors
 * @date 2019\9\20 0020 14:03
 */
@Service
public class LicenseUtil {
    
    private static final Logger log = LoggerFactory.getLogger(LicenseUtil.class);

    @Autowired
    private LicenseConfig licenseConfig;

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

    /**
     * @param code
     * @throws LicenseException
     */
    public void register(String code) throws LicenseException {
        String url = licenseConfig.getRegisterUrl();
        String authIp = licenseConfig.getAuthIp();
        Integer authPort = licenseConfig.getAuthPort();
        String notifyUrl = licenseConfig.getAuthNotifyUrl();
        register(url, code, authIp, authPort, notifyUrl);
    }

    /**
     * 注册
     *
     * @param authIp
     * @param code
     * @throws LicenseException
     */
    public void register(String code, String authIp) throws LicenseException {
        String url = licenseConfig.getRegisterUrl();
        Integer authPort = licenseConfig.getAuthPort();
        String notifyUrl = licenseConfig.getAuthNotifyUrl();
        register(url, code, authIp, authPort, notifyUrl);
    }

    /**
     * 注册
     *
     * @param authIp
     * @param authPort
     * @param code
     * @throws LicenseException
     */
    public void register(String code, String authIp, Integer authPort) throws LicenseException {
        String url = licenseConfig.getRegisterUrl();
        String notifyUrl = licenseConfig.getAuthNotifyUrl();
        register(url, code, authIp, authPort, notifyUrl);
    }

    /**
     * 注册
     *
     * @param code
     * @param authIp
     * @param authPort
     * @param notifyUrl
     * @throws LicenseException
     */
    public void register(String code, String authIp, Integer authPort, String notifyUrl) throws LicenseException {
        String url = licenseConfig.getRegisterUrl();
        register(url, code, authIp, authPort, notifyUrl);
    }

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
    public void register(String url, String code, String authIp, Integer authPort, String notifyUrl) throws LicenseException {

        if (url == null || url.equals("")) {
            throw new LicenseException("Not found url");
        }

        SystemData systemData = SystemUtil.getSystemData();

        if (systemData.getSystemUuId() == null || systemData.getSystemUuId().equals("")) {
            throw new LicenseException("Not found SystemUuId, Please use administrator privileges to run the program.");
        }

        MultiValueMap<String, Object> map = new LinkedMultiValueMap();
        map.add("code", code);
        map.add("systemData", Base64.getEncoder().encodeToString(systemData.toBytes()));

        if (code != null && "".equals(code)) {
            map.add("code", code);
        }

        if (authIp != null && "".equals(authIp)) {
            map.add("authIp", authIp);
        }

        if (notifyUrl != null && "".equals(notifyUrl)) {
            map.add("notifyUrl", notifyUrl);
        }

        if (authPort != null && authPort > 0) {
            map.add("authPort", authPort);
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.postForObject(url, map, String.class);
            log.debug("register返回值：" + response);
            saveLicenseFile(response);
        } catch (HttpClientErrorException e) {
            throw new LicenseException("the activity server response an error.", e);
        } catch (HttpServerErrorException e) {
            throw new LicenseException(e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new LicenseException("register error!", e);
        }
    }

    /**
     * 保存证书
     *
     * @param license
     * @throws IOException
     */
    public void saveLicenseFile(String license) throws Exception{
        String filePath = licenseConfig.getFilePath();
        String[] filePathDatas = filePath.split("/");
        String path = "";
        for (String dir : filePathDatas) {
            if (dir.indexOf(".") != -1) {
                break;
            } else {
                path = path + "/" + dir;
            }
        }

        File licenseFileDir = new File(path);
        //递归创建目录
        if (!licenseFileDir.exists()) {
            licenseFileDir.mkdirs();
        }
        String fileName = path + "/" + licenseConfig.FILE_NAME;
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
        String[] filePathDatas = filePath.split("/");
        String path = "";
        for (String dir : filePathDatas) {
            if (dir.indexOf(".") != -1) {
                break;
            } else {
                path = path + "/" + dir;
            }
        }
        String fileName = path + "/" + licenseConfig.FILE_NAME;
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
