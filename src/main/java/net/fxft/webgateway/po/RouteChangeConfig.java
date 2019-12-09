package net.fxft.webgateway.po;

import lombok.Data;
import net.fxft.common.jdbc.DbTable;

import java.util.Objects;

/**
 * 路由配置对象
 */
@DbTable("route_change_config")
@Data
public class RouteChangeConfig {

    /**
     * 主键id值
     */
    private Integer id;
    /**
     * 路由地址
     */
    private String url;
    /*
    排序
     */
    private int orders;
    /**
     * 路由路径名称
     */
    private String pathName;
    /**
     * 过滤器名称
     */
    private String fliterName;
    /**
     * 是否有效 1表示有效，0表示无效
     */
    private Integer flag;
    /**
     * 备注
     */
    private String remark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteChangeConfig that = (RouteChangeConfig) o;
        return orders == that.orders &&
                Objects.equals(id, that.id) &&
                Objects.equals(url, that.url) &&
                Objects.equals(pathName, that.pathName) &&
                Objects.equals(fliterName, that.fliterName) &&
                Objects.equals(flag, that.flag) &&
                Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, orders, pathName, fliterName, flag, remark);
    }
}
