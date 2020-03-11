package net.fxft.webgateway.util;

import lombok.Data;

/**
 * @ClassName CustomException
 * @Author zwj
 * @Description 自定义异常
 * @Date 2020/3/11 16:35
 */
@Data
public class CustomException extends RuntimeException{

    /*
     * message - 详细消息。 详细消息被保存以供以后通过getMessage()方法检索。
     * cause - 原因。 (空值是允许的，并指出原因不存在或未知。)
     * enableSuppression - 是否启用或禁用抑制
     * writableStackTrace - 堆栈跟踪是否可写
     * */

    private static final long serialVersionUID = 1L;

    private boolean code;

    private String message;

    private Object data;

    public CustomException() {
    }

    public CustomException(boolean code, String message) {
        this.code = code;
        this.message = message;
    }

    public CustomException(boolean code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
