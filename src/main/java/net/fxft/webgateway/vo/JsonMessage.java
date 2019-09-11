package net.fxft.webgateway.vo;


import java.io.Serializable;

/**
 * 返回到前台页面的JSON数据信息
 *
 * @author navigator
 */
public class JsonMessage implements Serializable {

    public static final int CODE_SUCCESS = 10000;

    //对于用户操作的正常和成功的信息
    private boolean success;
    private int code;
    //发送给前端，用于显示的成功或失败的消息
    private String message;
    //数据
    private Object data;
    //数据的总条数
    private long total;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    //指明Action跳转的是JSON响应数据
    private static String JSON = "json";

    public JsonMessage(boolean _success, Object data) {
        this.success = _success;
        if(_success) {
            this.code = CODE_SUCCESS;
        }else{
            this.code = 500;
        }
        this.data = data;
    }

    public JsonMessage(){}
    public JsonMessage(boolean _success, String msg) {
        this(_success, msg, null);
    }

    public JsonMessage(boolean _success, long total , Object data){
        this.success = _success;
        if(_success) {
            this.code = CODE_SUCCESS;
        }else{
            this.code = 500;
        }
        this.total = total;
        this.data = data;
    }
    public JsonMessage(boolean _success, String msg, Object data) {
        this.success = _success;
        if(_success) {
            this.code = CODE_SUCCESS;
        }else{
            this.code = 500;
        }
        this.message = msg;
        this.data = data;
    }

    public JsonMessage(boolean _success, int code, String msg, Object data) {
        this.success = _success;
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public JsonMessage(int code, String msg) {
        this.success = false;
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
