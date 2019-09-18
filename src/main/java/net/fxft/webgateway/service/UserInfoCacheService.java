package net.fxft.webgateway.service;

import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.gateway.event.everyunit.UpdateCacheEvent;
import net.fxft.gateway.event.impl.UpdateCacheEventListener;
import net.fxft.webgateway.po.Department;
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
public class UserInfoCacheService implements UpdateCacheEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(UserInfoCacheService.class);
    
    @Autowired
    private JdbcUtil jdbc;

    private Map<Integer, UserInfo> userIdMap = new ConcurrentHashMap<>();
    private Map<String, UserInfo> loginNameMap = new ConcurrentHashMap<>();
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
            Map<Integer, UserInfo> tmpidmap = new ConcurrentHashMap<>();
            Map<String, UserInfo> tmpnamemap = new ConcurrentHashMap<>();
            for (UserInfo ui : uilist) {
                tmpidmap.put(ui.getUserId(), ui);
                tmpnamemap.put(ui.getLoginName(), ui);
            }
            this.userIdMap = tmpidmap;
            this.loginNameMap = tmpnamemap;
            log.info("更新UserInfo缓存成功！size=" + this.userIdMap.size());
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
        UserInfo ui = userIdMap.get(userId);
        if (ui != null) {
            if (UserInfo.STATE_SUSPEND.equalsIgnoreCase(ui.getUserState())) {
                //用户已停用
                return null;
            }
        }
        return ui;
    }

    public UserInfo getUserById(int userId) {
        UserInfo ui = userIdMap.get(userId);
        return ui;
    }

    public UserInfo queryUserByName(String loginName) {
        return jdbc.select(UserInfo.class).andEQ("loginName", loginName)
                .andNotDeleted()
                .queryFirst();
//        UserInfo ui = loginNameMap.get(loginName);
//        return ui;
    }

    public List<Department> queryUserDepartments(int userId) {
        String sql = "select b.depId from userdepartment a, department b where b.deleted = false and a.depId = b.depId and a.userid = ?";
        List<Department> deplist = jdbc.sql(sql)
                .addIndexParam(userId)
                .query(Department.class);
        return deplist;
    }

    public String queryUserRoleName(int userId) {
        String sql = "select b.name from userrole a, role b where b.deleted = false and a.roleid = b.roleid and a.userid = ?";
        return jdbc.sql(sql).addIndexParam(userId).queryOneString();
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
