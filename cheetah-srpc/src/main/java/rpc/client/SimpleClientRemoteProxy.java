package rpc.client;

import rpc.RemoteCall;
import rpc.RemoteExecutor;
import rpc.RpcContext;
import rpc.RpcService;
import rpc.utils.RpcUtils;
import rpc.utils.XAliasUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:Roy
 * @Date: Created in 14:55 2017/12/3 0003
 */
public class SimpleClientRemoteProxy implements InvocationHandler, RpcService {

    private RemoteExecutor remoteExecutor;

    private Map<Class, String> versionCache = new ConcurrentHashMap<Class, String>();

    private Map<Class, String> groupCache = new ConcurrentHashMap<Class, String>();

    /**
     * 应用
     */
    private String application;

    public void startService() {
        remoteExecutor.startService();
    }

    public void stopService() {
        remoteExecutor.startService();
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication() {
        return application;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> service = method.getDeclaringClass();

        String name = method.getName();
        RemoteCall call = new RemoteCall(service.getName(), name);
        call.setArgs(args);
        String version = versionCache.get(service);
        if (version != null) {
            call.setVersion(version);
        } else {
            call.setVersion(RpcUtils.DEFAULT_VERSION);
        }

        String group = groupCache.get(service);
        if (group == null) {
            group = RpcUtils.DEFAULT_GROUP;
        }
        call.setGroup(group);

        //上下文 attachment支持
        Map<String, Object> attachment = RpcContext.getContext().getAttachment();
        call.setAttachment(attachment);

        call.getAttachment().put("Application", application);

        if (method.getReturnType() == void.class) {
            remoteExecutor.oneWay(call);
            return null;
        }
        return remoteExecutor.invoke(call);
    }

    public RemoteExecutor getRemoteExecutor() {
        return remoteExecutor;
    }

    public void setRemoteExecutor(RemoteExecutor remoteExecutor) {
        this.remoteExecutor = remoteExecutor;
    }

    public <Iface> Iface registerRemote(Class<Iface> remote) {
        return registerRemote(remote, RpcUtils.DEFAULT_VERSION);
    }

    public <Iface> Iface registerRemote(Class<Iface> remote, String version) {
        return registerRemote(remote, version, RpcUtils.DEFAULT_GROUP);
    }

    public <Iface> Iface registerRemote(Class<Iface> remote, String version, String group) {
        Iface result = (Iface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{remote}, this);
        if (version == null) {
            version = RpcUtils.DEFAULT_VERSION;
        }
        versionCache.put(remote, version);

        if (group == null) {
            group = RpcUtils.DEFAULT_GROUP;
        }

        XAliasUtils.addServiceRefType(remote);

        groupCache.put(remote, group);
        return result;
    }
}
