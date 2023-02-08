package com.itheima.sh.stock;

import com.itheima.sh.stock.utils.IdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *  测试雪花算法生成的id
 */
@SpringBootTest
public class IdWorkTest {
    //装配
    @Autowired
    private IdWorker idWorker;

    /*
        测试雪花算法生成的id
     */
    /**
     *
     */
    @Test
    public void testWorkId() {
        //循环100次生成100个id
        for (int i = 0; i < 100; i++) {
            //使用对象idWorker调用IdWorker类的方法生成随机的id
            long id = idWorker.nextId();
            System.out.println(id);
        }
    }
}
