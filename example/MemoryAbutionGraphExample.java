package cn.thutmose.abution.graph.ostore;

import cn.thutmose.abution.graph.G;
import cn.thutmose.abution.graph.Graph;
import cn.thutmose.abution.graph.data.knowhow.Knowledge;
import cn.thutmose.abution.graph.store.schema.Dimension;
import cn.thutmose.abution.graph.store.schema.Schema;
import cn.thutmose.abution.graph.type.frequency.FreqMap;
import cn.thutmose.abution.graph.type.frequency.serialisation.FreqMapSerialiser;
import cn.thutmose.abution.jfunc.Agg;
import com.google.common.collect.Lists;


public class MemoryAbutionGraphExample {

    public static void main(String[] args) throws Exception {

        /**
         * (1) 初始化图实例（不存在会自动建图）
         * */
        // 3个必填参数（图谱名称、Schema、远程服务器端口）
        Graph g = G.MemoryGraph("tgMemory1", Schema.Builder()
                .entity(
                        Dimension.label("DIM").property("p1",String.class).build(),
                        Dimension.label("AGG1")
                                .property("str",String.class, Agg.StrConcat())
                                .property("freqmap", FreqMap.class, Agg.FreqMap(), new FreqMapSerialiser())
                                .groupBy().build()
                ).edge(Dimension.label("EDG").groupBy().build()).build(), "127.0.0.1:5701");

        // 保存元数据，以便可视化实时可见
        g.addOrUpdate();


        /**
         * （2）构建和写入Knowledge（Entity和Edge的统称）
         * */
        // FreqMap是一种聚合的数据类型
        FreqMap freq1 = new FreqMap();
        freq1.update("aa");
        freq1.update("aa");
        System.out.println(freq1);

        // 写入两个节点和一条关系
        g.addKnow(Knowledge.dimV("AGG1").vertex("a").property("str", "3").property("freqmap", freq1).build()).exec();
        g.addKnow(Knowledge.dimV("AGG1").vertex("b").property("str", "3").property("freqmap", freq1).build()).exec();
        g.addKnow(Knowledge.dimE("EDG").edge("a","b", true).build()).exec();

        /**
         * （3）数据查询
         *  Ps：可以多尝试一两次重复执行导入数据，观察结果变化
         * */
        System.out.println(Lists.newArrayList(g.V("a").dims().exec()));
        System.out.println(Lists.newArrayList(g.V("a","b").dims("AGG1").exec()));
        System.out.println(Lists.newArrayList(g.E().dims().exec()));
        System.out.println(Lists.newArrayList(g.V("a").Out().dims().exec()));

        System.out.println(Lists.newArrayList(g.V("a","b").OutE().dims().exec()));

    }

}
