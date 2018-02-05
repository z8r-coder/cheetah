package rpc;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:Roy
 * @Date: Created in 15:01 2017/12/3 0003
 */
public class RpcContext {

    private Map<String, Object> attachment = new HashMap<String, Object>();

    private static ThreadLocal<RpcContext> context = new ThreadLocal<RpcContext>(){
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return context.get();
    }

    public void clear() {
        context.remove();
    }

    public void putAll(Map<String, Object> attachment) {
        if (attachment != null) {
            this.attachment.putAll(attachment);
        }
    }

    public Map<String, Object> getAttachment() {
        return this.attachment;
    }

    public int size() {
        return attachment.size();
    }

    public Object getAttachment(String key) {
        return attachment.get(key);
    }

    public void setAttachment(String key, Object value) {
        this.attachment.put(key, value);
    }
}
