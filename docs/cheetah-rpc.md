## cheetah-rpc
作为cheetah内部通信组件，为使其轻量化，不依赖其他开源框架，仅仅使用Java原生nio来实现（将来有时间会用netty实现一遍）。
### 调用链（直连，无负载均衡）
![enter image description here](https://raw.githubusercontent.com/Ruan-Xin/cheetah/master/docs/cheetah-rpc%E8%B0%83%E7%94%A8%E9%93%BE.png)
cheetah有个非常重要的组件，remoteExecutor执行器，分为

 - clientRemoteExecutor，主要对Rpc请求进行封装，并发送
 - serverRemoteExecutor，主要反射执行业务方法

acceptor 和selector属于通信底层nio组件，对其进行了功能化的封装，并对监听进行异步化。
具有参数返回的业务函数，提供两个执行器
SyncClientRemoteExecutor和AsyncClientRemoteExecutor.
异步执行器成员函数包括了一个回调类
RpcCallBack，并提供两个方法进行业务操作，successfull()和fail()

优点：
- cheetah-rpc完全组件化，无任何依赖
- 轻量级，仅仅是对Java原生nio进行封装
- 提供wrapper进行业务注册，减少代码量
- 消息传递采用异步监听的模式
- 过滤链，方法执行拦截

缺点：

 - 功能性不强，仅仅是为了服务通信
 - 无流量监听系统
 - 无注册中心（本来提供一个简单的注册中心，太蹩脚删掉了）
 - 无负载均衡
 - 无业务集群模式

具体使用方法可查看cheetah-demo

目前cheetah-rpc仅仅是cheetah系统中的通讯组件