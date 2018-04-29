package constants;

/**
 * @Author:Roy
 * @Date: Created in 23:01 2017/10/14 0014
 */
public enum ErrorCodeEnum {
    RPC00000("RPC00000", "successful"),
    RPC00010("RPC00010", "request time out"),
    RPC00020("RPC00020", "acceptor close, connect to host error")
    ;

    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误描述
     */
    private String errorDesc;

    ErrorCodeEnum(String errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }
}
