package net.fxft.webgateway.service;

import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.gateway.event.everyunit.UpdateCacheEvent;
import net.fxft.gateway.event.impl.UpdateCacheEventListener;
import net.fxft.webgateway.po.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserInfoService implements UpdateCacheEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(UserInfoService.class);
    
    @Autowired
    private JdbcUtil jdbc;

    private Map<Integer, UserInfo> usermap = new ConcurrentHashMap<>();
    private AtomicBoolean updateCacheFlag = new AtomicBoolean(true);

    @PostConstruct
    @Scheduled(fixedRate = 1000)
    private void execUpdateCache() {
        if (updateCacheFlag.get()) {
            updateCacheFlag.getAndSet(false);
            List<UserInfo> uilist = jdbc.select(UserInfo.class)
                    .setNotPrint()
                    .andNotDeleted()
                    .query();
            Map<Integer, UserInfo> tmpmap = new ConcurrentHashMap<>();
            for (UserInfo ui : uilist) {
                tmpmap.put(ui.getUserId(), ui);
            }
            this.usermap = tmpmap;
            log.info("更新UserInfo缓存成功！size=" + this.usermap.size());
        }
    }

    /**
     * 每小时更新一次
     */
    @Scheduled(fixedRate = 60*60_000)
    public void updateCache() {
        updateCacheFlag.getAndSet(true);
    }

    public UserInfo getLoginableUserById(int userId) {
        UserInfo ui = usermap.get(userId);
        if (ui != null) {
            if (UserInfo.STATE_SUSPEND.equalsIgnoreCase(ui.getUserState())) {
                //用户已停用
                return null;
            }
        }
        return ui;
    }

    @Override
    public boolean isEventMatch(UpdateCacheEvent updateCacheEvent) {
        return "userinfo".equalsIgnoreCase(updateCacheEvent.getCacheName());
    }

    @Override
    public void fireUpdateCache(UpdateCacheEvent updateCacheEvent) {
        updateCache();
    }
}
