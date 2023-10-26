
## AbutionGraph（时序图数据库）
AbationGraph® is a time-series knowledge graph database for real-time OLAP。  
AbationGraph是一款面向时序多维关联数据流式分析（GraphOLAM）的知识图谱数据库，它融合了RDF图谱+属性图谱+时序计算+数仓多维标签等多种数据库的优点，除传统图谱数据存储外，Abution的目标是以足够低的延迟（亚秒级）来服务大规模图谱数据（达BP级）的实时决策分析，而非只是简单实时用户查询。Abution能针对大规模时序数据流自动端到端的聚合计算并立即更新存储，特别适用于指标系统建设、实时交互式数据分析、可视化大屏展现、IOT流式数据监测、拓扑数据动态行为计算、相同点边id的数据根据标签分类管理等等。
AbationGraph使用Java/C++开发，支持Aremlin、Gremlin、GraphQL查询语法，并支持与Java进行混合编程开发。

![image](https://github.com/ThutmoseAI/AbutionGraph/assets/8678397/3ce3f784-5ed1-489b-a630-f5100e19319a)

AbutionGraph特性如下：
1. 分布式高可用，支持万亿点边存储
2. Graph-HybridOLAP，动态聚合+静态图谱自定义存储
3. Multi-Dimension，RDF图+属性图融合架构、自定义节点id、快速数据检索、数据打标分类管理
4. 预计算模型，实体与关系都可以自动聚合，自定义自动计算分析的业务模型
5. 原子级数据权限
6. 亚秒级关联计算分析

AbutionGraph适用场景如：
1. 交互式数据分析  
   希望快速从大规模历史关联数据中得出决策报告，数据探索-秒内响应、年月日时间窗口分析-秒内响应等。
2. 流式数据监控  
   希望从实时源源不断产生数据的iot/应用程序中立即反映趋势，态势感知、实时聚合计算、时序指标变化规律等。
3. 多维数据管理  
   希望将同一个id-人身份证等，绑定上工商/税务/车房产/银行/通话等不同结构的数据，并通过设定标签识别类别数据，实现高效管理与查询。
4. 知识关联计算  
   希望导入的实体与关系自动实现关联，而不是明确“点表/边表”必须一一具备，允许孤立点。此外，希望自动汇总一跳邻居节点信息如：出度入度、基数统计、百分位数等，实现复杂关联指标的即席查询。
5. 子图隔离  
   希望在一个图谱中实现不同用户导入的数据仅自己可见，或授权可见，很适用于公安、政府、跨部门、多用户协作等场景。

![image](https://github.com/ThutmoseAI/AbutionGraph/assets/8678397/535568ba-0c54-48d7-898e-34523dae4b29)


## 状态
AbationGraph当前版本为v2.8.0，已经过多年大量的生产应用，数据库安装包下载地址如下：
```bash
 git clone https://www.modelscope.cn/AbutionGraph/abution_graph_db_install_package.git
```
服务器推荐：CentOS7或者Ubuntu18以上系统，不满足的话请升级系统gcc版本至8以上版本。  
资源推荐：由4～8个CPU内核和8～32GB内存（分布式情况下，资源丰富可在单台机器上启动多个数据库实例提供系统并行性）。

## 快速上手体验
Abution的TmpGraph实例使用临时缓存持久化数据，无需安装部署即可体验大部分功能，程序执行完毕则释放空间，本意是方便开发者本地调试编写的程序。
TmpGraph推荐使用jdk8/11进行开发。此外，abution-jshell是系统封装的一个REPL启动命令，可以直接输入业务代码并查看其执行结果。
1. 导入开发包jar依赖到IDEA
2. 运行程序 GraphOfTheGodsFactory.java（如下）

### 传统静态图谱
**1）图谱建模**  
AbutionGraph的Schema由entity和edge组成，缺少任一项也是允许的。其中，维度标签都由Dimension类定义，label第二个参数为标签描述，可缺省；property的字段可以指定为任意类型，只要写入数据类型一致即可。
```java
Schema schema = Schema
    .entity(
        Dimension.label("V|Titan", "太阳神").property("age", Integer.class).build(),
        Dimension.label("V|God", "上帝").property("age", Integer.class).build(),
        Dimension.label("V|Demigod", "小神").property("age", Integer.class).build(),
        Dimension.label("V|Human", "人类").property("age", Integer.class).build(),
        Dimension.label("V|Monster", "怪物").build(),
        Dimension.label("V|Location", "场景").build()
    ).edge(
        Dimension.label("E|Father", "父亲").build(),
        Dimension.label("E|Brother", "兄弟").build(),
        Dimension.label("E|Mother", "母亲").build(),
        Dimension.label("E|Battled", "战争")
           .property("time", Integer.class)
           .property("place", Geoshape.class)
           .build(),
        Dimension.label("E|Pet", "宠物").build(),
        Dimension.label("E|Lives", "生活").property("reason", String.class).build()
    ).build();
```

**2）创建图谱**
应用schema新建一个名叫"Gods"的图谱。
```java
Graph g = G.TmpGraph("Gods", schema);
```

**3）手动构建图谱数据**
1. 创建实体数据
```java
Entity saturn = Knowledge.dimV("V|Titan").vertex("saturn").property("age", 10000).build();
Entity sky = Knowledge.dimV("V|Location").vertex("sky").build();
Entity jupiter = Knowledge.dimV("V|God").vertex("jupiter").property("age", 5000).build();
Entity neptune = Knowledge.dimV("V|God").vertex("neptune").property("age", 4500).build();
Entity hercules = Knowledge.dimV("V|Demigod").vertex("hercules").property("age", 30).build();
...
```
2. 创建关系数据
```java
// jupiter relation
Edge eg = Knowledge.dimE("E|Father").edge("jupiter", "saturn", true).build();
Edge eg1 = Knowledge.dimE("E|Lives").edge("jupiter", "sky", true).property("reason", "loves fresh breezes").build();
Edge eg2 = Knowledge.dimE("E|Brother").edge("jupiter", "neptune", true).build();
//hercules relation
Edge eg7 = Knowledge.dimE("E|Father").edge("hercules", "jupiter", true).build();
Edge eg8 = Knowledge.dimE("E|Mother").edge("hercules", "alcmene", true).build();
Edge eg9 = Knowledge.dimE("E|Battled").edge("hercules", "nemean", true).property("time", 1).property("place", Geoshape.point(38.1, 23.7)).build();
Edge eg10 = Knowledge.dimE("E|Battled").edge("hercules", "hydra", true).property("time", 2).property("place", Geoshape.point(37.7, 23.9)).build();
...
List<Edge> edges = Lists.newArrayList(eg, eg1, eg2, eg3, eg4, eg5, ...);
```
Ps：可见，实体和关系可以0属性，这是RDF图谱的特性。此外，实体和关系也无需完全对应，允许孤立点和孤立边数据的导入。

**4）导入数据**
1. 导入实体数据
``````java
g.addKnow(saturn, sky, sea, jupiter, neptune, hercules, ...).exec();
``````
2. 导入关系数据
``````java
g.addKnow(edges).exec();
``````
Ps：因为Entity和Edge都属于Knowledge类，因此实体与关系数据无需分开，可以混合导入，数据库会自动区分。

**5）图谱查询**  
Aremlin语法规则：pipline大写字母开头的为功能函数，后接小写开头的都为该功能函数的参数，直到下一个大写开头的功能函数出现。
1. 1跳查询：检索saturn的所有实体维度的数据
```java
Iterable<? extends Knowledge> scan1 = g.V("saturn").dims().exec();
System.out.println(Lists.newArrayList(scan1));
// [Entity[vertex=saturn,dimension=Titan,properties=Properties[age=<java.lang.Integer>10000]]]
```
2. 2跳查询：saturn的“孙子”是谁？
```java
Iterable<? extends Object> scan2 = g.V("saturn").In().dim("E|Father").In().dim("E|Father").exec();
System.out.println(Lists.newArrayList(scan2));
// ["hercules"]
```
3. 过滤查询："V|Human"维度下，"age"<50的有哪些人
```java
Iterable<? extends Entity> scan3 = g.V().dim("V|Human").has("age").by(P.LessThan(50)).exec();
System.out.println(JsonSerialiser.serialise(scan3));
```
4. 统计查询："saturn"出方向1跳邻居有多少个
```java
DimsCounter counter = g.V("saturn").OutV().dims().CountDims().exec();
System.out.println(Lists.newArrayList(counter));
//[DimsCounter[entityDims={Titan=1},edgeDims={},limitHit=false]]
```
5. 全量查询：输出所有的顶点id
```java
System.out.println(Lists.newArrayList( g.V().ToEntityIds().exec() ));
// [EntityKey[vertex=hercules], EntityKey[vertex=hydra], EntityKey[vertex=cerberus], ...]
```
6. 数据转换：遍历出"jupiter"出方向的邻居（Knowledge类型），并从每一个Knowledge中提取出(用Map-等价lambda)邻居的维度标签进行返回
```java
Iterable<Object> scan6 = g.V("jupiter").OutV().dims().Map(F.ItFunc(x-> ((Knowledge)x).getDimension())).exec();
System.out.println(JsonSerialiser.serialise(scan6));
// ["god","Titan","god","location"]
```

### 进阶-时序动态图谱
时序动态图谱实际是一种预计算技术，其核心思想是提前计算和存储某些计算结果，以便在需要时能够更快地获取结果，用于提高应用程序的响应时间。  
静态图谱：只需要指定字段及类型；  
动态图谱：需要指定字段类型、聚合函数、序列化函数（可选）、.groupBy()聚合窗口；  
Ps：静态图谱和动态图谱可以节点不同维度的形式异构存储。

1）构建图谱  
可见property多了一些聚合配置（除默认功能外，聚合函数可自定义）：
```java
Graph g = G.TmpGraph("Gods", Schema
    .entity(
         Dimension.label("Vgg|TimeWindow", "")
              .property("startDate", Date.class, Agg.Min())
              .property("stopDate", Date.class, Agg.Max())
              .property("hll", DistinctCountHll.class, Agg.DistinctCountHll(), new DistinctCountHllSerialiser())
              .property("freq", FreqMap.class, Agg.FreqMap(), new FreqMapSerialiser())
              .property("count", Integer.class, Agg.Sum())
              .groupBy("startDate", "stopDate") // 指定聚合窗口; 不指定字段即为全局聚合：.groupBy()
              .build())
    .edge(
         Dimension.label("Egg|Merge", "合并边")
              .property("total_duration", Double.class, Agg.Sum())
              .groupBy()
              .build()
    ).build());
```

2）模拟数据
```text
起点(人), 终点(地点), 关系标签, 发生时间, 持续时长
"hercules", "nemean", Battled, 2023-10-08, 20.0
"hercules", "hydra", Battled, 2023-10-08, 10.1
"hercules", "hydra", Battled, 2023-10-09, 10.1
"hercules", "hydra", Battled, 2023-10-09, 11.1
```

3）导入数据  
下面我们将模拟流式数据一条一条的导入并立即查看存储效果。  
1. 添加第一条数据： "hercules", "nemean", Battled, 2023-10-08, 20.0
```java
Entity entt1 = Knowledge.dimV("Vgg|TimeWindow")
       .vertex("hercules")                                                     //人名作为节点id
       .property("startDate", DateUtil.parse("2023-10-08 00:00:00")) //开始时间（窗口开）
       .property("stopDate", DateUtil.parse("2023-10-08 23:59:59"))  //结束时间（窗口闭）
       .property("hll", new DistinctCountHll().update("nemean"))        //将尾节点放入基数统计类
       .property("freq", new FreqMap().update("nemean"))                  //将尾节点放入频率估计类
       .property("count", 1)                                                //本次自动+1
       .build();
Edge edge1 = Knowledge.dimE("Egg|Merge")
       .edge("hercules","nemean",true)
       .property("total_duration", 20.0d)
       .build();
g.addKnow(entt1,edge1).exec();
```
查看结果：
```java
System.out.println(JsonSerialiser.serialise(
       g.V("hercules").dim("Vgg|TimeWindow").exec()
));
```
```json
[{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":
  {"freq":{"FreqMap":{"nemean":1}},"count":1,"hll":{"DistinctCountHll":{"cardinality":1.0}},"startDate":{"java.util.Date":1696694400000},"stopDate":{"java.util.Date":1696780799000}}}]
```
```java
System.out.println(JsonSerialiser.serialise(
       g.E("hercules->nemean").dim("Egg|Merge").exec()
));
```
```json
[{"class":"Edge","dimension":"Egg|Merge","source":"hercules","target":"nemean","directed":true,"properties":{"total_duration":20.0}}]
```


2. 添加第二条数据："hercules", "hydra", Battled, 2023-10-08, 10.1
```java
Entity entt2 = Knowledge.dimV("Vgg|TimeWindow")
       .vertex("hercules")                                                     //
       .property("startDate", DateUtil.parse("2023-10-08 00:00:00")) //开始时间（窗口开）
       .property("stopDate", DateUtil.parse("2023-10-08 23:59:59"))  //结束时间（窗口闭）
       .property("hll", new DistinctCountHll().update("hydra"))        //将尾节点放入基数统计类
       .property("freq", new FreqMap().update("hydra"))                  //将尾节点放入频率估计类
       .property("count", 1)                                                //本次自动+1
       .build();
Edge edge2 = Knowledge.dimE("Egg|Merge")
       .edge("hercules","nemean",true)
       .property("total_duration", 10.1d)
       .build();
g.addKnow(entt2,edge2).exec();
```
查看结果：
```java
System.out.println(JsonSerialiser.serialise(
     g.V("hercules").dim("Vgg|TimeWindow").exec()
));
```
```json
[{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":
  {"freq":{"FreqMap":{"hydra":1,"nemean":1}},"count":2,"hll":{"DistinctCountHll":{"cardinality":2.000000004967054}},"startDate":{"java.util.Date":1696694400000},"stopDate":{"java.util.Date":1696780799000}}}]
```
```java
System.out.println(JsonSerialiser.serialise(
     g.E("hercules->nemean").dim("Egg|Merge").exec()
));
```
```json
[{"class":"Edge","dimension":"Egg|Merge","source":"hercules","target":"nemean","directed":true,"properties":{"total_duration":30.1}}]
```
可见，与schema预设一致，"hercules"的Entity中，count自动累加成2（1+1），hll也由1变成了2（hydra与nemean两个不重复），而时间则都是2023-10-08（开始和结束点是该天的窗口界限）；Edge中，total_duration变成了新老数据只和。

3. 添加第三四条数据并观察结果  
// "hercules", "hydra", Battled, 2023-10-09, 10.1  
// "hercules", "hydra", Battled, 2023-10-09, 11.1  
```java
Entity entt3 = Knowledge.dimV("Vgg|TimeWindow")
       .vertex("hercules")
       .property("startDate", DateUtil.parse("2023-10-09 00:00:00"))
       .property("stopDate", DateUtil.parse("2023-10-09 23:59:59"))
       .property("hll", new DistinctCountHll().update("hydra").update("hydra"))
       .property("freq", new FreqMap().update("hydra").update("hydra"))
       .property("count", 2)              //本次自动+2：篇幅影响，两条数据手动合并录入了
       .build();
Edge edge3 = Knowledge.dimE("Egg|Merge")
       .edge("hercules","nemean",true)
       .property("total_duration", 10.1d+11.1d)
       .build();
g.addKnow(entt3,edge3).exec();

System.out.println(JsonSerialiser.serialise(
       g.V("hercules").dim("Vgg|TimeWindow").exec()
));
System.out.println(JsonSerialiser.serialise(
       g.E("hercules->nemean").dim("Egg|Merge").exec()
));
```
```json
[{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":{"freq":{"FreqMap":{"hydra":2}},"count":2,"hll":{"DistinctCountHll":{"cardinality":1.0}},"startDate":{"java.util.Date":1696780800000},"stopDate":{"java.util.Date":1696867199000}}},
   {"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":{"freq":{"FreqMap":{"hydra":1,"nemean":1}},"count":2,"hll":{"DistinctCountHll":{"cardinality":2.000000004967054}},"startDate":{"java.util.Date":1696694400000},"stopDate":{"java.util.Date":1696780799000}}},

   {"class":"Edge","dimension":"Egg|Merge","source":"hercules","target":"nemean","directed":true,"properties":{"total_duration":51.3}}]
```
可见，节点"hercules"的Entity有两条已经完成所有属性聚合的数据（不同时间窗口的两天）；而"hercules to nemean"的Edge是全局聚合，所以始终会聚合属性并合并成一条边。



## 快速启动一个AbutionGraph实例
拉取Abution的Docker镜像：
```bash
 docker pull thutmose/abutiongraph-standalone:latest
```
启动Abution的容器：
```bash
    docker run -d \
    -m 8G \
    -p 9995:9995 \
    -p 9090:9090 \
    -p 50070:50070 \
    -p 8032:8032 \
    -p 8042:8042 \
    -p 2181:2181 \
    --name thutmose-2.8.0 \
    thutmose/abutiongraph-standalone:2.8.0
```

## AbutionGraph安装

## AbutionGraph运行


。。。持续更新中
