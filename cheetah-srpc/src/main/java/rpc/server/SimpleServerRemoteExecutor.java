package rpc.server;

import rpc.RemoteCall;
import rpc.RemoteExecutor;
import rpc.RpcServiceBean;
import rpc.exception.RpcException;
import rpc.exception.RpcExceptionHandler;
import rpc.exception.SimpleRpcExceptionHandler;
import rpc.utils.RpcUtils;
import rpc.utils.XAliasUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:Roy
 * @Date: Created in 17:57 2017/12/3 0003
 */
public class SimpleServerRemoteExecutor implements RemoteExecutor, RpcServicesHolder{

    /**
     * remote api注册
     */
    protected Map<String, RpcServiceBean> exeCache = new ConcurrentHashMap<String, RpcServiceBean>();

    /**
     * 业务方法执行异常处理器
     */
    private RpcExceptionHandler exceptionHandler;

    /**
     * 当前应用
     */
    private String application;

    public SimpleServerRemoteExecutor() {
        exceptionHandler = new SimpleRpcExceptionHandler();
    }

    /**
     * 通过反射执行业务方法
     */
    public void oneWay(RemoteCall remoteCall) {
        RpcUtils.invokeMethod(this.findService(remoteCall), remoteCall.getMethod(), remoteCall.getArgs(), exceptionHandler);
    }

    public Object invoke(RemoteCall call) {
        return RpcUtils.invokeMethod(this.findService(call), call.getMethod(), call.getArgs(), exceptionHandler);
    }

    public void oneWayBroadcast(RemoteCall remoteCall) {

    }



    /**
     * 注册remote服务
     * @param clazz
     * @param ifaceImpl
     */
    public void registerRemote(Class<?> clazz,Object ifaceImpl){
        this.registerRemote(clazz, ifaceImpl,null,null);
    }

    public void registerRemote(Class<?> clazz, Object ifaceImpl, String version, String group) {
        Object service = exeCache.get(clazz.getName());
        if (service != null && service != ifaceImpl) {
            throw new RpcException("can't register service "+clazz.getName()+" again");
        }
        if (ifaceImpl == service || ifaceImpl == null) {
            return;
        }
        if (version == null) {
            version = RpcUtils.DEFAULT_VERSION;
        }

        //默认分组
        if (group == null) {
            group = RpcUtils.DEFAULT_GROUP;
        }

        //添加类型映射
        XAliasUtils.addServiceRefType(clazz);
        exeCache.put(this.makeExeKey(clazz.getName(), version,group), new RpcServiceBean(clazz,ifaceImpl,version,application,group));
    }
    public void startService() {

    }

    public void stopService() {

    }


    /**
     * 通过service和版本找到实现对象
     */
    private Object findService(RemoteCall call) {
        String exeKey = makeExeKey(call.getService(), call.getVersion(), call.getGroup());
        RpcServiceBean object = exeCache.get(exeKey);
        if (object == null || object.getBean() == null) {
            throw new RpcException("group:"+call.getGroup()+" service:"+call.getService()+" version:"+call.getVersion()+" not exist");
        }
        return object.getBean();
    }

    private String makeExeKey(String service, String version, String group) {
        if (version != null) {
            return group + "_" + service + "_" + version;
        }
        return service;
    }

    /**
     * 获取列表， 用于监控使用
     */
    public List<RpcServiceBean> getRpcServices() {
        List<RpcServiceBean> list = new ArrayList<RpcServiceBean>();
        list.addAll(exeCache.values());
        return list;
    }

    public Map<String, RpcServiceBean> getExeCache() {
        return exeCache;
    }

    public void setExeCache(Map<String, RpcServiceBean> exeCache) {
        this.exeCache = exeCache;
    }

    public RpcExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(RpcExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
