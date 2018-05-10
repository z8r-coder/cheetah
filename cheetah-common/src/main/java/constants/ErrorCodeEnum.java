package constants;

/**
 * @Author:Roy
 * @Date: Created in 23:01 2017/10/14 0014
 */
public enum ErrorCodeEnum {
    RPC00000("RPC00000", "successful"),
    RPC00001("RPC00001", "rpc data too long"),
    RPC00002("RPC00002", "method not exist method"),
    RPC00003("RPC00003", "invoke IllegalAccess request access error"),
    RPC00004("RPC00004", "invoke IllegalArgument request param wrong"),
    RPC00005("RPC00005", "rpc invoke target error"),
    RPC00006("RPC00006", "broadcast refuse to invoke one method which have return value!"),
    RPC00007("RPC00007", "rpc filter call error!"),
    RPC00008("RPC00008", "rpc response == null"),
    RPC00009("RPC00009", "not supported enum for simple serializer "),
    RPC00010("RPC00010", "request time out"),
    RPC00011("RPC00011", "not supported java type for simple serializer "),
    RPC00020("RPC00020", "acceptor close, connect to host error"),
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
