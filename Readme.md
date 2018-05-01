# 关于cheetah
- cheetah是基于raft协议的强一致内存索引(raft日志同步实现持久化)，内部提供一个简单的rpc框架，并提供同步与异步结果返回。
## 什么是raft协议
- [raft原文](https://pdos.csail.mit.edu/6.824/papers/raft-extended.pdf)
- [raft可视化](http://thesecretlivesofdata.com/raft/)

## 构建并启动cheetah
	单机多线程节点
	1.执行./build.sh
	2.执行./script/raft/raft_start_single_process.sh 
	
	单机多进程节点
	1.执行./build.sh
	2.执行./script/raft/raft_start_cluster.sh (如果是macos)

## cheetah-rpc
		rpc provider
		1.需要继承RpcAcceptorWrapper
		2.并通过实现 register()方法来注册业务类
		3.startService()启动服务
		
		rpc consumer
		1.需要继承RpcConnectorWrapper
		2.并通过实现getClientRemoteExecutor来获取同步或是异步的remoteExecutor
		3.通过startService启动服务
		4.getProxy()返回代理，注册反射生成对象
		
	
   cheetah-rpc详细设计
## cheetah-raft
	raft服务初始化：
	RaftRpcServerAcceptor acceptor = new RaftRpcServerAcceptor(getHost(), getPort());
    

并通过脚本进行启动

	    #!/usr/bin/env bash  
	    java -cp ../../cheetah-demo/target/dependency/*:cheetah-demo-1.0-SNAPSHOT.jar 	
	    raft.RaftServerMutilProcessStartDemo "127.0.0.1:6060" (此处host:127.0.0.1,port:6060)
	    
cheetah-raft详细设计

## cheetah-kv
- 主要通过委托java内置的ConcurrentHashMap实现带定时器的kv数据

        MapProxy mapProxy = new MapProxy();
		
cheetah-kv详细设计




