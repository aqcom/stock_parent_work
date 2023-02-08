package com.itheima.sh.stock;

import com.google.common.collect.Lists;
import com.itheima.sh.stock.mapper.StockBusinessMapper;
import com.itheima.sh.stock.service.StockTimerTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author by itheima
 * @Date 2022/1/1
 * @Description
 */
@SpringBootTest
public class StockTimerServiceTest {
    @Autowired
    private StockTimerTaskService stockTimerService;

    @Autowired
    private StockBusinessMapper stockBusinessMapper;

    /**
     * 获取大盘数据
     */
    @Test
    public void test01() {
        stockTimerService.getInnerMarketInfo();
    }

    /*
        获取个股编码测试
     */
    @Test
    public void test02() {
        //1.使用stockBusinessMapper对象调用方法获取股票编码集合
        List<String> stockCodes = stockBusinessMapper.getStockCodes();//{"000001","000002",...}
        //2.遍历集合取出个股编码，如果是以6开头表示是上证股票则在前面加sh否则是深证股票，在代码前加sz
       /* stockCodes = stockCodes.stream().map(code -> {//code="000001" ,"000002"
            //判断code是否以6开始,如果是以6开始直接拼接sh，否则加sz
            //sz000001
            code = code.startsWith("6") ? ("sh" + code) : ("sz" + code);
            return code;
        }).collect(Collectors.toList());*/
        stockCodes = stockCodes.stream()
                .map(code -> code.startsWith("6") ? ("sh" + code) : ("sz" + code))
                .collect(Collectors.toList());

        //3.输出集合
        /*
            stockCodes = [sz000001, sz000002, sz000004, sz000005, sz000006, sz000007,
                          sz000008, sz000009, sz000011, sz000012, sz000014, sz000016, sz000017,
                          sz000019, sz000020, sz000021, sz000023, sz000025, sz000026, sz000027,
                          sh600000, sh600004, sh600006, sh600007, sh600008, sh600009, sh600010,
                           sh600011, sh600012, sh600015, sh600016, sh600017, sh600018,
                           sh600019, sh600020, sh600021, sh600022, sh600023, sh600025, sh600026]
         */
        System.out.println("stockCodes = " + stockCodes);
    }

    /*
        使用谷歌工具测试集合分片
     */
    @Test
    public void test03() {
        //1.创建集合对象
        ArrayList<Integer> list = new ArrayList<>();
        //2.向集合中存储50个数据
        for (int i = 1; i <= 50; i++) {
            list.add(i);
        }
        //3.将集合均等分，每份大小最多15个
        /*
            Lists类的静态方法：
            public static <T> List<List<T>> partition(List<T> list, int size)
                参数：该方法接受两个参数：
                    list:被分片的总集合
                    size:每个子集合的期望大小。最后一个子集合的大小可能较小。
                返回值：该方法返回连续子集合的列表。每个子集合(除了最后一个子集合)的大小都等于分区大小。
         */
        List<List<Integer>> partitionList = Lists.partition(list, 15);
        partitionList.forEach(list2->{
            System.out.println("list2 = " + list2);
        });
    }

    /**
     * 批量获取股票分时数据详情信息
     */
    @Test
    public void test04() {
        stockTimerService.getStockRtIndex();
    }
}