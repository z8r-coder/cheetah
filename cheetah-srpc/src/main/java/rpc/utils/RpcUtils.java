package rpc.utils;

import org.apache.log4j.Logger;
import rpc.RemoteCall;
import rpc.RpcObject;
import rpc.constants.RpcType;
import rpc.exception.RpcException;
import rpc.exception.RpcExceptionHandler;
import rpc.exception.RpcNetExceptionHandler;
import rpc.net.AbstractRpcAcceptor;
import rpc.net.AbstractRpcConnector;
import rpc.nio.AbstractRpcNioSelector;
import rpc.nio.RpcNioConnector;
import rpc.nio.SimpleRpcNioSelector;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;

public class RpcUtils {

	private static Logger logger = Logger.getLogger(RpcUtils.class);
	private static Map<String, Method> methodCache = new HashMap<String, Method>();
	
	public static int MEM_8KB = 1024*8;
	
	public static int MEM_16KB = MEM_8KB*2;
	
	public static int MEM_32KB = MEM_16KB*2;

	public static int MEM_64KB = MEM_32KB*2;
	
	public static int MEM_128KB = MEM_64KB*2;
	
	public static int MEM_256KB = MEM_128KB*2;
	
	public static int MEM_512KB = MEM_256KB*2;
	
	public static int MEM_1M = MEM_512KB*2;
	
	public static String DEFAULT_VERSION = "0.0";

	public static String DEFAULT_GROUP = "default";

	
	public static String chooseIP(List<String> ips){
		Collections.sort(ips);
		String ip = "127.0.0.1";
		if(ips!=null){
			for(String localip:ips){
				if(localip.startsWith("127.")){
					
				}else if(localip.startsWith("192.168")){
					if(ip.startsWith("127.")){
						ip = localip;
					}else if(ip.startsWith("192.168")&&ip.endsWith(".1")){
						ip = localip;
					}
					if(!localip.startsWith("192.168")&&!localip.endsWith(".1")){
						ip = localip;
					}
				}else if(localip.startsWith("10.")){
					if(ip.startsWith("127.")){
						ip = localip;
					}else if(ip.startsWith("10.")&&ip.endsWith(".1")){
						ip = localip;
					}
					if(!localip.startsWith("10.")&&!localip.endsWith(".1")){
						ip = localip;
					}
				}else{
					ip = localip;
				}
			}
		}
		return ip;
	}

	public static void writeRpc(RpcObject rpc, OutputStream dos, RpcNetExceptionHandler handler) {
		try {
			dos.write(rpc.getType().getType());
			dos.write(RpcUtils.longToBytes(rpc.getThreadId()));
			dos.write(RpcUtils.intToBytes(rpc.getIndex()));
			dos.write(RpcUtils.intToBytes(rpc.getLength()));
			if (rpc.getLength() > 0) {
				if (rpc.getLength() > MEM_512KB) {
					throw new RpcException("rpc data too long "+ rpc.getLength());
				}
				dos.write(rpc.getData());
			}
			dos.flush();
		} catch (IOException e) {
			handleNetException(e,handler);
		}
	}
	
	private static void handleNetException(Exception e,RpcNetExceptionHandler handler){
		if(handler!=null){
			handler.handleNetException(e);
		}else{
			throw new RpcException(e);
		}
	}
	
	public static String genAddressString(String prefix,InetSocketAddress address){
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		sb.append(address.getAddress().getHostAddress());
		sb.append(":");
		sb.append(address.getPort());
		return sb.toString();
	}
	
	public static void writeDataRpc(RpcObject rpc, DataOutputStream dos,RpcNetExceptionHandler handler) {
		try {
			dos.writeInt(rpc.getType().getType());
			dos.writeLong(rpc.getThreadId());
			dos.writeInt(rpc.getIndex());
			dos.writeInt(rpc.getLength());
			if (rpc.getLength() > 0) {
				if (rpc.getLength() > MEM_512KB) {
					throw new RpcException("rpc data too long "+ rpc.getLength());
				}
				dos.write(rpc.getData());
			}
			dos.flush();
		} catch (IOException e) {
			handleNetException(e,handler);
		}
	}

	private static void dataTooLongEx(RpcObject rpcObject, InputStream dis) throws IOException {
		if (rpcObject.getLength() > 0) {
			if (rpcObject.getLength() > MEM_512KB) {
				throw new RpcException("rpc data too long "	+ rpcObject.getLength());
			}
			byte[] buf = new byte[rpcObject.getLength()];
			dis.read(buf);
			rpcObject.setData(buf);
		}
	}
	public static RpcObject readRpc(InputStream dis, byte[] buffer,RpcNetExceptionHandler handler) {
		try {
			RpcObject rpc = new RpcObject();
			int type = dis.read();
			rpc.setType(RpcType.getByType(type));
			byte[] thBytes = new byte[8];
			dis.read(thBytes);
			rpc.setThreadId(RpcUtils.bytesToLong(thBytes));
			byte[] indexBytes = new byte[4];
			dis.read(indexBytes);
			rpc.setIndex(RpcUtils.bytesToInt(indexBytes));
			byte[] lenBytes = new byte[4];
			dis.read(lenBytes);
			rpc.setLength(RpcUtils.bytesToInt(lenBytes));

			dataTooLongEx(rpc, dis);
			return rpc;
		} catch (IOException e) {
			handleNetException(e,handler);
			return null;
		}
	}
	
	public static RpcObject readDataRpc(DataInputStream dis,RpcNetExceptionHandler handler) {
		try {
			RpcObject rpc = new RpcObject();
			rpc.setType(RpcType.getByType(dis.readInt()));
			rpc.setThreadId(dis.readLong());
			rpc.setIndex(dis.readInt());
			rpc.setLength(dis.readInt());
			dataTooLongEx(rpc, dis);
			return rpc;
		} catch (IOException e) {
			handleNetException(e,handler);
			return null;
		}
	}

	public static void close(DataInputStream dis, DataOutputStream dos) {
		try {
			dis.close();
			dos.close();
		} catch (IOException e) {
			// close all
		}
	}
	
	public static List<Field> getFields(Class clazz){
		Field[] fields = clazz.getDeclaredFields();
		ArrayList<Field> fs = new ArrayList<Field>();
		for(Field f:fields){
			fs.add(f);
		}
		Class superclass = clazz.getSuperclass();
		if(superclass!=null&&superclass!=Object.class){
			fs.addAll(getFields(superclass));
		}
		return fs;
	}

	/**
	 * 调用对象的方法执行
	 */
	public static Object invokeMethod(Object obj, String methodName,Object[] args,RpcExceptionHandler exceptionHandler) {
		Class<? extends Object> clazz = obj.getClass();
		String key = clazz.getCanonicalName() + "." + methodName;
		Method method = methodCache.get(key);
		if (method == null) {
			method = RpcUtils.findMethod(clazz, methodName, args);
			if (method == null) {
				throw new RpcException("method not exist method:" + methodName);
			}
			methodCache.put(key, method);
		}
		return RpcUtils.invoke(method, obj, args,exceptionHandler);
	}

	/**
	 * 调用对象的方法执行
	 * @param method
	 * @param obj
	 * @param args
	 * @param exceptionHandler
	 * @return
	 */
	public static Object invoke(Method method, Object obj, Object[] args,RpcExceptionHandler exceptionHandler) {
		try {
			return method.invoke(obj, args);
		} catch (IllegalAccessException e) {
			throw new RpcException("invoke IllegalAccess request access error");
		} catch (IllegalArgumentException e) {
			throw new RpcException("invoke IllegalArgument request param wrong");
		} catch (InvocationTargetException e) {
			if(e.getCause()!=null){
				exceptionHandler.handleException(null, null, e.getCause());
			}else{
				exceptionHandler.handleException(null, null, e);
			}
			throw new RpcException("rpc invoke target error");
		}
	}

	public static void handleException(RpcExceptionHandler rpcExceptionHandler, RpcObject rpc, RemoteCall call, Exception e){
		if(rpcExceptionHandler!=null){
			rpcExceptionHandler.handleException(rpc,call,e);
		}else{
			logger.error("exceptionHandler null exception message:"+e.getMessage());
		}
	}
	
	public static long getNowInmilliseconds() {
		return new Date().getTime();
	}

	public static byte[] intToBytes(int iSource) {
		byte[] bLocalArr = new byte[4];
		for (int i=0;i<bLocalArr.length; i++) {
			bLocalArr[i] = (byte) (iSource >> 8*(3-i) & 0xFF);
		}
		return bLocalArr;
	}

	public static int bytesToInt(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i=0; i<bRefArr.length; i++) {
			bLoop = bRefArr[i];
			iOutcome += (bLoop & 0xFF) << (8 * (3-i));
		}
		return iOutcome;
	}

	public static byte[] longToBytes(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 7; i>-1; i--) {
			b[i] = new Long(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static long bytesToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;
		s6 <<= 8;
		s5 <<= 16;
		s4 <<= 24;
		s3 <<= 8 * 4;
		s2 <<= 8 * 5;
		s1 <<= 8 * 6;
		s0 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	@Override
	public String toString() {
		return super.toString();
	}

    // TODO: 2017/10/15 0015 RpcOioWriter
    public static AbstractRpcConnector createRpcConnector(AbstractRpcNioSelector nioSelector, Class<? extends AbstractRpcConnector> connectorClass){
		try{
			if(connectorClass.equals(RpcNioConnector.class)){
				Constructor<? extends AbstractRpcConnector> constructor = connectorClass.getConstructor(AbstractRpcNioSelector.class);
				return constructor.newInstance(nioSelector);
//			}else if(connectorClass==RpcOioConnector.class){
//				Constructor<? extends AbstractRpcConnector> constructor = connectorClass.getConstructor(AbstractRpcOioWriter.class);
//				return constructor.newInstance(writer);
			}else{
				return connectorClass.newInstance();
			}
		}catch(Exception e){
			throw new RpcException(e);
		}
	}
	
	public static AbstractRpcConnector createConnector(Class connectorClass){
		SimpleRpcNioSelector nioSelector = new SimpleRpcNioSelector();
//		SimpleRpcOioWriter writer = new SimpleRpcOioWriter();
		if(connectorClass==null){
			connectorClass = RpcNioConnector.class;
		}
		return RpcUtils.createRpcConnector(nioSelector, connectorClass);
	}
	
	public static String bytesToHexString(byte[] bytes){   
	    StringBuilder stringBuilder = new StringBuilder();   
	    if (bytes == null || bytes.length <= 0) {   
	        return stringBuilder.toString();   
	    }   
	    for (int i = 0; i < bytes.length; i++) {   
	        int v = bytes[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();   
	} 

	public static Method findMethod(Class clazz, String name, Object[] args) {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	public static void setAddress (String host, int port, AbstractRpcConnector connector) {
		connector.setPort(port);
		connector.setHost(host);
	}

	public static void setAddress (String host, int port, AbstractRpcAcceptor acceptor) {
		acceptor.setPort(port);
		acceptor.setHost(host);
	}
	
	public static long getNowMinute(){
		return getMinute(new Date());
	}
	
	@SuppressWarnings("deprecation")
	public static long getMinute(Date date){
		GregorianCalendar calendar = new GregorianCalendar(1900+date.getYear(),date.getMonth(),date.getDay(),date.getHours(),date.getMinutes());
		return calendar.getTimeInMillis();
	}
	
	public static final long MINUTE = 60*1000;
	
}
