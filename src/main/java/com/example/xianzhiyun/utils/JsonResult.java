package com.example.xianzhiyun.utils;

public class JsonResult<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> r = new JsonResult<>();
        r.code = 0;
        r.msg = "成功";
        r.data = data;
        return r;
    }

    public static <T> JsonResult<T> error(int code, String msg) {
        JsonResult<T> r = new JsonResult<>();
        r.code = code;
        r.msg = msg;
        r.data = null;
        return r;
    }

    // 兼容已有调用：
    public static <T> JsonResult<T> ok(T data) {
        return success(data);
    }

    public static <T> JsonResult<T> fail(String msg) {
        return error(400, msg);
    }

    public static <T> JsonResult<T> unauth(String msg) {
        return error(401, msg);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}