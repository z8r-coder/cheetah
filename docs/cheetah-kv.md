## cheetah-kv
cheetah-kv作为cheetah系统中的kv存储组件，主要委托Java内部ConcurrentHashMap来实现索引，提供hash存储，数据过期等功能

### ExpEntryValue
对data 和过期时间进行了封装，意味着每一条数据对应着一个过期时间。

### 过期策略
cheetah-kv采用两种过期策略，惰性删除和定期删除相结合，主要是为了保证强一致性和减少内存占用。
- 惰性删除：key过期的时候不删除，每次从数据库获取key的时候去检查是否过期，若过期，则删除，返回null。
- 定期删除：每隔一段时间执行一次删除(可在cheetah中配置)过期key操作

ExpDataClearService 即是对过期key的一个定时删除