package net.fxft.webgateway.controller;

import net.fxft.common.el.MapConvert;
import net.fxft.common.entity.JsonResponse;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.util.JacksonUtil;
import net.fxft.common.util.ParseJsonMap;
import net.fxft.webgateway.po.ModuleVersionPO;
import net.fxft.webgateway.route.GatewayRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author huanglusen
 * @date 2020-03-09
 */
@RestController
@RequestMapping({"/", GatewayRoutes.Base_Prefix})
public class ModuleVersionController {

    private static final Logger log = LoggerFactory.getLogger(ModuleVersionController.class);

    @Autowired
    private JdbcUtil jdbc;

    @RequestMapping("/updateModuleVersion")
    public String updateModuleVersion(@RequestBody ParseJsonMap versionMap) {
        String ip = versionMap.getString("ip");
        String workDir = versionMap.getString("workDir");
        List<Map> jarList = (List) versionMap.getValue("jarList");
        for (Map row : jarList) {
            row.put("ip", ip);
            row.put("workDir", workDir);
            ModuleVersionPO mv = new ModuleVersionPO();
            MapConvert.mapToObject(row, mv);
            mv.setUpdateTime(new Date());
            ModuleVersionPO ex = new ModuleVersionPO();
            ex.setIp(mv.getIp());
            ex.setWorkDir(mv.getWorkDir());
            ex.setProjectName(mv.getProjectName());
            ModuleVersionPO existsPO = jdbc.select(ex).setNotPrint().whereColumnIfNotNull().queryFirst();
            if (existsPO != null) {
                mv.setId(existsPO.getId());
                jdbc.update(mv).setNotPrint().whereIdRefValueEQ().execute();
                log.debug("更新版本信息！" + mv);
            } else {
                mv.setId(0);
                jdbc.insert(mv).setNotPrint().execute(true);
                log.debug("新增版本信息！" + mv);
            }
        }
        return JacksonUtil.toJsonString(JsonResponse.of(10000, "success"));
    }

}
