package net.fxft.webgateway.service;

import net.fxft.common.function.LoopConsumer.LoopControl;
import net.fxft.common.util.LoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class AutoCacheMap implements Serializable{
    
    private static final Logger log = LoggerFactory.getLogger(AutoCacheMap.class);

    private String name;
    private Map<String, CacheValue> vmap = new ConcurrentHashMap<>();

    public AutoCacheMap(String name) {
        this.name = name;
    }

    public void put(String key, Object value, int lifeSeconds) {
        CacheValue cv = new CacheValue();
        cv.value = value;
        cv.createTime = System.currentTimeMillis();
        cv.lifeMillis = lifeSeconds * 1000;
        vmap.put(key, cv);
    }

    public <T> T get(String key) {
        CacheValue cv = vmap.get(key);
        if (cv != null) {
            if (System.currentTimeMillis() < cv.createTime + cv.lifeMillis) {
                return (T)cv.value;
            } else {
                vmap.remove(key);
                return null;
            }
        }else{
            return null;
        }
    }

    public Object getAndRemove(String key) {
        Object v = get(key);
        vmap.remove(key);
        return v;
    }


    public void clearTimeoutItems() {
        final LongAdder delcnt = new LongAdder();
        final long now = System.currentTimeMillis();
        LoopUtil.loopMap(vmap, (k, cv) -> {
            if (now > cv.createTime + cv.lifeMillis) {
                delcnt.increment();
                return LoopControl.REMOVE;
            } else {
                return LoopControl.CONTINUE;
            }
        });
        if (delcnt.sum() > 0) {
            log.debug("CacheMap删除过期数据！name=" + name + "; delcnt=" + delcnt.sum());
        }
    }

    public int size() {
        return vmap.size();
    }

    @Override
    public String toString() {
        return "cacheName=" + name + "; size=" + size();
    }

    class CacheValue implements Serializable {
        Object value;
        long createTime;
        int lifeMillis;
    }

}
