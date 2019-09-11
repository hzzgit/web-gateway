package net.fxft.webgateway.service;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutoCacheService {

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
        });
    }


}
