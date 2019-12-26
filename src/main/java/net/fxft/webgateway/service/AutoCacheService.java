package net.fxft.webgateway.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutoCacheService {

    private static final Logger log = LoggerFactory.getLogger(AutoCacheService.class);
    
    private Map<String, AutoCacheMap> cmap = new ConcurrentHashMap<>();

    public AutoCacheMap getCacheMap(String cacheName) {
        AutoCacheMap cm = cmap.get(cacheName);
        if (cm == null) {
            synchronized (cmap) {
                cm = cmap.get(cacheName);
                if (cm == null) {
                    cm = new AutoCacheMap(cacheName);
                    cmap.put(cacheName, cm);
                }
                return cm;
            }
        }
        return cm;
    }

    @Scheduled(fixedRate = 5*60*1000)
    public void clearTimeoutData() {
        cmap.forEach((k,v) ->{
            v.clearTimeoutItems();
            log.debug("AutoCacheService数量统计！name=" + k + "; size=" + v.size());
        });
    }

    @PostConstruct
    public void loadCache() {
        try {
            File f = new File("autoCache.cache");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object obj = ois.readObject();
                ois.close();
                this.cmap = (Map<String, AutoCacheMap>) obj;
                log.info("从autoCache.cache中恢复缓存！cache=" + this.cmap);
            }
        } catch (Exception e) {
            log.error("loadCache出错！", e);
        }


    }

    @PreDestroy
    public void saveCache() {
        try {
            long l1 = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream("autoCache.cache");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(cmap);
            oos.flush();
            oos.close();
            long l2 = System.currentTimeMillis();
            log.info("退出前保存autoCache！耗时=" + (l2 - l1) + "; cache=" + this.cmap);
        } catch (IOException e) {
            log.error("saveCache出错！", e);
        }


    }


}
