package com.jesper.seckill.exception;

import com.jesper.seckill.result.CodeMsg;

public class ReduceInventoryException extends RuntimeException{
    private static final long servialVersionUID = 1L;

    private Integer code;

    private String msg;



    public ReduceInventoryException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public ReduceInventoryException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
