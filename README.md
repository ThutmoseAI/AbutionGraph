
## AbutionGraph（时序图数据库）
AbationGraph® is a time-series knowledge graph database for real-time OLAP。  
AbationGraph是一款面向时序多维关联数据流式分析（GraphOLAM）的知识图谱数据库，它融合了RDF图谱+属性图谱+时序计算+数仓多维标签等多种数据库的优点，除传统图谱数据存储外，Abution的目标是以足够低的延迟（亚秒级）来服务大规模图谱数据（达BP级）的实时决策分析，而非只是简单实时用户查询。Abution能针对大规模时序数据流自动端到端的聚合计算并立即更新存储，特别适用于指标系统建设、实时交互式数据分析、可视化大屏展现、IOT流式数据监测、拓扑数据动态行为计算、相同点边id的数据根据标签分类管理等等。
AbationGraph使用Java/C++开发，支持Aremlin、Gremlin、GraphQL查询语法，并支持与Java进行混合编程开发。

![image](https://github.com/ThutmoseAI/AbutionGraph/assets/8678397/3ce3f784-5ed1-489b-a630-f5100e19319a)

![image](https://github.com/ThutmoseAI/AbutionGraph/assets/8678397/535568ba-0c54-48d7-898e-34523dae4b29)


AbutionGraph特性如下：
1. 分布式高可用，支持万亿点边存储
2. Graph-HybridOLAP，动态聚合+静态图谱自定义存储
3. Multi-Dimension，RDF图+属性图融合架构、自定义节点id、快速数据检索、数据打标分类管理
4. 预计算模型，实体与关系都可以自动聚合，自定义自动计算分析的业务模型
5. 原子级数据权限，API自带
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

## 状态
AbationGraph当前版本为v2.8.0，已经过多年大量的生产应用，数据库安装包下载地址如下：
```bash
 git clone https://www.modelscope.cn/AbutionGraph/abution_graph_db_install_package.git
```
服务器推荐：CentOS7或者Ubuntu18以上系统，不满足的话请升级系统gcc版本至8以上版本。资源推荐：由4～8个CPU内核和8～32GB内存（分布式情况下，资源丰富可在单台机器上启动多个数据库实例提供系统并行性）。

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
