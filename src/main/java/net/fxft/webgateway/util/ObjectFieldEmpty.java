package net.fxft.webgateway.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @Author Lirenhui
 * @Date 2019/6/12 16:17
 */
public final class ObjectFieldEmpty {
    
    private static final Logger log = LoggerFactory.getLogger(ObjectFieldEmpty.class);

    /**
     * 判断一个实体类对象实例的所有成员变量是否为空
     *
     * @param obj 校验的类对象实例
     * @return Boolean
     * @throws Exception
     */
    public static Boolean isObjectFieldEmpty(Object obj) throws Exception {
        if (null == obj) {
            return true;
        }

        try {
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                if (f.get(obj) != null && StringUtils.isNotBlank(f.get(obj).toString())) {
                    return false;
                }

            }
        } catch (Exception e) {
            log.error("", e);
        }

        return true;
    }
}
