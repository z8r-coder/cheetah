package rpc;

import java.io.Serializable;

/**
 * @author ruanxin
 * @create 2018-02-11
 * @desc
 */
public class RpcService implements Serializable {
    /**
     * service's name
     */
    private String serviceName;

    /**
     * version
     */
    private String version;

    /**
     * service implement
     */
    private String serviceImpl;

    /**
     * start time
     */
    private long time;

    /**
     * application marker
     */
    private String application;

    /**
     * group
     */
    private String group;

    public RpcService (String serviceName, String version, String impl) {
        this.serviceName = serviceName;
        this.version = version;
        this.serviceImpl = impl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RpcService that = (RpcService) o;

        if (serviceName != null ? !serviceName.equals(that.serviceName) : that.serviceName != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (serviceImpl != null ? !serviceImpl.equals(that.serviceImpl) : that.serviceImpl != null) return false;
        if (application != null ? !application.equals(that.application) : that.application != null) return false;
        return group != null ? group.equals(that.group) : that.group == null;
    }
}
