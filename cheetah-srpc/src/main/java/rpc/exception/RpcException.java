package rpc.exception;

import constants.ErrorCodeEnum;

/**
 * @Author:Roy
 * @Date: Created in 22:58 2017/10/14 0014
 */
public class RpcException extends RuntimeException {
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误码
     */
    private String errorDesc;

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable t) {
        super(t);
    }

    public RpcException(String errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public RpcException(ErrorCodeEnum errorCodeEnum) {
        this.errorDesc = errorCodeEnum.getErrorDesc();
        this.errorCode = errorCodeEnum.getErrorCode();
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
