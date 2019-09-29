package net.fxft.webgateway.license;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import sun.plugin2.util.SystemUtil;

import java.io.*;
import java.util.Base64;

/**
 * @author Sordors
 * @date 2019\9\20 0020 14:03
 */
@Service
public class LicenseUtil {

    private LicenseConfig LicenseConfig;

    /**
     * 注册
     *
     * @throws LicenseException
     */
    public  void register() throws LicenseException {
        String url = LicenseConfig.getRegisterUrl();
        String authIp = LicenseConfig.getAuthIp();
        Integer authPort = LicenseConfig.getAuthPort();
        String code = LicenseConfig.getCode();
        String notifyUrl = LicenseConfig.getAuthNotifyUrl();
        register(url, code, authIp, authPort, notifyUrl);
    }

    /**
     * @param code
     * @throws LicenseException
     */
    public void register(String code) throws LicenseException {
        String url = LicenseConfig.getRegisterUrl();
        String authIp = LicenseConfig.getAuthIp();
        Integer authPort = LicenseConfig.getAuthPort();
        String notifyUrl = LicenseConfig.getAuthNotifyUrl();
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
        String url = LicenseConfig.getRegisterUrl();
        Integer authPort = LicenseConfig.getAuthPort();
        String notifyUrl = LicenseConfig.getAuthNotifyUrl();
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
        String url = LicenseConfig.getRegisterUrl();
        String notifyUrl = LicenseConfig.getAuthNotifyUrl();
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
        String url = LicenseConfig.getRegisterUrl();
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
            System.out.println(response);
            LicenseUtil.saveLicenseFile(response);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw new LicenseException("the activity server response an error.");
        } catch (HttpServerErrorException e) {
            System.out.println("the activity server no response.");
            throw new LicenseException(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    /**
     * 保存证书
     *
     * @param license
     * @throws IOException
     */
    public void saveLicenseFile(String license) {
        String filePath = LicenseConfig.getFilePath();
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
        String fileName = path + "/" + LicenseConfig.FILE_NAME;
        File licenseFile = new File(fileName);
        if (!licenseFile.exists()) {
            try {
                licenseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //写入文件
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            license = license == null ? "" : license;
            bw.write(license);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取证书
     *
     * @return
     * @throws LicenseException
     */
    public static License loadLicense() throws LicenseException {
        String filePath = LicenseConfig.getFilePath();
        String[] filePathDatas = filePath.split("/");
        String path = "";
        for (String dir : filePathDatas) {
            if (dir.indexOf(".") != -1) {
                break;
            } else {
                path = path + "/" + dir;
            }
        }
        String fileName = path + "/" + LicenseConfig.FILE_NAME;
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
