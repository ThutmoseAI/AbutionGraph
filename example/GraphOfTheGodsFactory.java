package cn.thutmose.abution.test.gods;

import cn.thutmose.abution.graph.G;
import cn.thutmose.abution.graph.Graph;
import cn.thutmose.abution.graph.commonutil.iterable.CloseableIterable;
import cn.thutmose.abution.graph.data.knowhow.Edge;
import cn.thutmose.abution.graph.data.knowhow.Entity;
import cn.thutmose.abution.graph.data.knowhow.Knowledge;
import cn.thutmose.abution.graph.data.knowhow.id.EntityId;
import cn.thutmose.abution.graph.store.schema.Dimension;
import cn.thutmose.abution.graph.store.schema.Schema;
import cn.thutmose.abution.graph.type.geo.Geoshape;
import cn.thutmose.abution.graph.user.User;
import cn.thutmose.abution.jfunc.Agg;
import cn.thutmose.abution.jfunc.F;
import cn.thutmose.abution.jfunc.P;
import cn.thutmose.abution.jfunc.util.JsonSerialiser;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class GraphOfTheGodsFactory {

    public static void run() {

        // 1）图谱建模
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
                        Dimension.label("Egg|BattledAggregation", "战争统计")
                                .property("totalTime", Integer.class, Agg.Sum())
                                .groupBy()
                                .build(),
                        Dimension.label("E|Pet", "宠物").build(),
                        Dimension.label("E|Lives", "生活").property("reason", String.class).build()
                ).build();

        // 2）新建一个名叫"Gods"的图谱
        Graph g = G.TmpGraph("Gods", schema);

        // 3）手动构建图谱数据

        // 3.1）创建实体数据
        Entity saturn = Knowledge.dimV("V|Titan").vertex("saturn").property("age", 10000).build();
        Entity sky = Knowledge.dimV("V|Location").vertex("sky").build();
        Entity sea = Knowledge.dimV("V|Location").vertex("sea").build();
        Entity jupiter = Knowledge.dimV("V|God").vertex("jupiter").property("age", 5000).build();
        Entity neptune = Knowledge.dimV("V|God").vertex("neptune").property("age", 4500).build();
        Entity hercules = Knowledge.dimV("V|Demigod").vertex("hercules").property("age", 30).build();
        Entity alcmene = Knowledge.dimV("V|Human").vertex("alcmene").property("age", 45).build();
        Entity pluto = Knowledge.dimV("V|God").vertex("pluto").property("age", 4000).build();
        Entity nemean = Knowledge.dimV("V|Monster").vertex("nemean").build();
        Entity hydra = Knowledge.dimV("V|Monster").vertex("hydra").build();
        Entity cerberus = Knowledge.dimV("V|Monster").vertex("cerberus").build();
        Entity tartarus = Knowledge.dimV("V|Location").vertex("tartarus").build();
        // 导入数据
        g.addKnow(saturn, sky, sea, jupiter, neptune, hercules, alcmene, nemean, pluto, hydra, cerberus, tartarus).exec();

        // 3.2）创建关系数据（有向边）
        // jupiter relation
        Edge eg = Knowledge.dimE("E|Father").edge("jupiter", "saturn", true).build();
        Edge eg1 = Knowledge.dimE("E|Lives").edge("jupiter", "sky", true).property("reason", "loves fresh breezes").build();
        Edge eg2 = Knowledge.dimE("E|Brother").edge("jupiter", "neptune", true).build();
        Edge eg3 = Knowledge.dimE("E|Brother").edge("jupiter", "pluto", true).build();
        //neptune relation
        Edge eg4 = Knowledge.dimE("E|Lives").edge("neptune", "sea", true).property("reason", "loves waves").build();
        Edge eg5 = Knowledge.dimE("E|Brother").edge("neptune", "jupiter", true).build();
        Edge eg6 = Knowledge.dimE("E|Brother").edge("neptune", "pluto", true).build();
        //hercules relation
        Edge eg7 = Knowledge.dimE("E|Father").edge("hercules", "jupiter", true).build();
        Edge eg8 = Knowledge.dimE("E|Mother").edge("hercules", "alcmene", true).build();
        Edge eg9 = Knowledge.dimE("E|Battled").edge("hercules", "nemean", true).property("time", 1).property("place", Geoshape.point(38.1, 23.7)).build();
        Edge eg10 = Knowledge.dimE("E|Battled").edge("hercules", "hydra", true).property("time", 2).property("place", Geoshape.point(37.7, 23.9)).build();
        Edge eg11 = Knowledge.dimE("E|Battled").edge("hercules", "cerberus", true).property("time", 12).property("place", Geoshape.point(39, 22)).build();
        //pluto relation
        Edge eg12 = Knowledge.dimE("E|Brother").edge("pluto", "jupiter", true).build();
        Edge eg13 = Knowledge.dimE("E|Brother").edge("pluto", "neptune", true).build();
        Edge eg14 = Knowledge.dimE("E|Lives").edge("pluto", "tartarus", true).property("reason", "no fear of death").build();
        Edge eg15 = Knowledge.dimE("E|Pet").edge("pluto", "cerberus", true).build();
        //cerberus relation
        Edge eg16 = Knowledge.dimE("E|Lives").edge("cerberus", "tartarus", true).build();
        List<Edge> edges = Lists.newArrayList(eg, eg1, eg2, eg3, eg4, eg5, eg6, eg6, eg7, eg8, eg9, eg10, eg11, eg12, eg13, eg14, eg15, eg16);
        // 导入数据
        g.addKnow(edges).exec();

        // 4）图谱查询
        // 1跳查询：检索saturn的所有实体维度的数据
        Iterable<? extends Knowledge> scan1 = g.V("saturn").dims().exec();
        System.out.println(Lists.newArrayList(scan1));

        // 2跳查询：saturn的“孙子”是谁
        Iterable<? extends Object> scan2 = g.V("saturn").In().dim("E|Father").In().dim("E|Father").exec();
        System.out.println(Lists.newArrayList(scan2));

        // 过滤查询："V|Human"维度下，"age"<50的有哪些人
        Iterable<? extends Entity> scan3 = g.V().dim("V|Human").has("age").by(P.LessThan(50)).exec();
        System.out.println(JsonSerialiser.serialise(scan3));


        // 统计查询："saturn"出方向1跳邻居有多少个
        DimsCounter counter = g.V("saturn").OutV().dims().CountDims().exec();
        System.out.println(Lists.newArrayList(counter));
        //[DimsCounter[entityDims={Titan=1},edgeDims={},limitHit=false]]

        // 全量查询：输出所有的顶点id
        System.out.println(Lists.newArrayList(g.V().ToEntityIds().exec()));

        // 遍历查询：遍历出"jupiter"出方向的邻居，并提取出(用Map-与lambda类似)每个邻居的维度标签
        Iterable<Object> scan6 = g.V("jupiter").OutV().dims().Map(F.ItFunc(x-> ((Knowledge)x).getDimension())).exec();
        System.out.println(JsonSerialiser.serialise(scan6));


        Map<String, CloseableIterable<?>> res31 = g.E().dims().has("place").by(P.GeoWithin(Geoshape.circle(37.97, 23.72, 50)))
                .ToVertices().extSource()
                .ToEntityIds().Store("source")
                .In().ToVertices().Store("god1").Restart()
                .Select("source").Out().ToVertices().Store("god2").SelectMap().setExports("god1","god2")
                .exec();
        System.out.println(JsonSerialiser.serialise(res31));

//        Object exec1 = g.V("hercules").While().gql(G.GetNeighborIds().ToSet().Map(x-> {
//            System.out.println(x);
//            return x;
//        }).ToEntityIds().getChain()).maxRepeats(2).exec();
//        System.out.println(JsonSerialiser.serialise(exec1));
    }


    public static void runAgg() {

        /**
         * 静态图谱：只需要指定字段及类型
         * 动态图谱：需要指定字段类型、聚合函数、序列化函数（可选）、.groupBy()聚合窗口
         * */
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

        // 导入数据：
        // 起点(人), 终点(地点), 关系标签, 发生时间, 持续时长
        // "hercules", "nemean", Battled, 2023-10-08, 20.0
        // "hercules", "hydra", Battled, 2023-10-08, 10.1
        // "hercules", "hydra", Battled, 2023-10-09, 10.1
        // "hercules", "hydra", Battled, 2023-10-09, 11.1

        // 1.添加第一条数据： "hercules", "nemean", Battled, 2023-10-08, 20.0
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

        System.out.println(JsonSerialiser.serialise(
                g.V("hercules").dim("Vgg|TimeWindow").exec()
        ));
        //[{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":{"freq":{"FreqMap":{"nemean":1}},"count":1,"hll":{"DistinctCountHll":{"cardinality":1.0}},"startDate":{"java.util.Date":1696694400000},"stopDate":{"java.util.Date":1696780799000}}}]

        System.out.println(JsonSerialiser.serialise(
                g.E("hercules->nemean").dim("Egg|Merge").exec()
        ));
        //[{"class":"Edge","dimension":"Egg|Merge","source":"hercules","target":"nemean","directed":true,"properties":{"total_duration":20.0}}]

        // 2.添加第二条数据："hercules", "hydra", Battled, 2023-10-08, 10.1
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

        System.out.println(JsonSerialiser.serialise(
                g.V("hercules").dim("Vgg|TimeWindow").exec()
        ));
        //[{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":{"freq":{"FreqMap":{"hydra":1,"nemean":1}},"count":2,"hll":{"DistinctCountHll":{"cardinality":2.000000004967054}},"startDate":{"java.util.Date":1696694400000},"stopDate":{"java.util.Date":1696780799000}}}]
        System.out.println(JsonSerialiser.serialise(
                g.E("hercules->nemean").dim("Egg|Merge").exec()
        ));
        // [{"class":"Edge","dimension":"Egg|Merge","source":"hercules","target":"nemean","directed":true,"properties":{"total_duration":30.1}}]



        // 3.添加第三四条数据：
        // "hercules", "hydra", Battled, 2023-10-09, 10.1
        // "hercules", "hydra", Battled, 2023-10-09, 11.1

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
        //[{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":{"freq":{"FreqMap":{"hydra":2}},"count":2,"hll":{"DistinctCountHll":{"cardinality":1.0}},"startDate":{"java.util.Date":1696780800000},"stopDate":{"java.util.Date":1696867199000}}},{"class":"Entity","dimension":"Vgg|TimeWindow","vertex":"hercules","properties":{"freq":{"FreqMap":{"hydra":1,"nemean":1}},"count":2,"hll":{"DistinctCountHll":{"cardinality":2.000000004967054}},"startDate":{"java.util.Date":1696694400000},"stopDate":{"java.util.Date":1696780799000}}}]
        //[{"class":"Edge","dimension":"Egg|Merge","source":"hercules","target":"nemean","directed":true,"properties":{"total_duration":51.3}}]


    }


    public static void main(String[] args) throws Exception {
	run();
	
	System.out.println("-----------------------");
	
	runAgg()；
    }

}
