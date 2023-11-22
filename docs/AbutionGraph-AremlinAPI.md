> Ps：在AbutionGraph中，所有的Object都是Json可序列化的，您可以使用JsonSerialiser将两种形似互转，可以很方便的在数据中台中调用。


- AbutionGraph使用的完整流程：  
    1. 图谱结构建模-构建Schema  
    2. 新建图实例-没有则自动创建  
    3. 写入数据源-数据转成Knowledge  
    4. 数据查询及分析-结合聚合/过滤/转换函数  
