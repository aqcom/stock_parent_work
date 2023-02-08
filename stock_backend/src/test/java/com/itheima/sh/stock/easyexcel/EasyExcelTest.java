package com.itheima.sh.stock.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class EasyExcelTest {

    /**
     * 测试 EasyExce导出数据功能
     */
    @Test
    public void test() {
        //创建集合对象
        ArrayList<User> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserName("柳岩"+i);
            user.setAge(18+i);
            user.setAddress("湖南"+i);
            user.setBirthday(new Date());
            list.add(user);
        }
        /*
            1.EasyExcel属于核心类
            2.write方法表示写出数据，方法参数：
                1）fileName：写出文件的路径 "C:\\Users\\tians\\Desktop\\user\\user.xls"
                2)将哪个实体类写出到指定文件中 User.class
            3.  sheet("黑马程序员用户信息") 表示在excel表格中的sheet的名字
            4. doWrite(list) 将list集合中的数据写出到指定文件中

         */
        EasyExcel.write("C:\\Users\\tians\\Desktop\\user\\user.xls", User.class).
                sheet("黑马程序员用户信息").doWrite(list);
    }
    /*
        需求：读取C:\\Users\\tians\\Desktop\\user\\user.xls文件中的数据到User实体类对象中
            1.EasyExcel.read(fileName, DemoData.class, new DemoDataListener())
                参数：
                    fileName：读取表格的文件路径
                    DemoData.class：封装读取数据的类的字节码
                    new DemoDataListener()：监听器

     */
    @Test
    public void test01() {
        //创建集合对象存储读取表格的数据
        ArrayList list = new ArrayList<>();
        //使用easyexcel类调用读取表格数据的方法
        EasyExcel.read("C:\\Users\\tians\\Desktop\\user\\user.xls",
                User.class,
                            new AnalysisEventListener() {
                                /*
                                    1.invoke方法的参数：
                                        1）o:表示存储表格中每行数据的对象
                                        2）analysisContext：保存读取表格的所有内容
                                 */
                                @Override
                                public void invoke(Object o, AnalysisContext analysisContext) {
                                    //System.out.println("o = " + o);
                                    //将存储数据的对象o放到list集合中
                                    list.add(o);
                                }
                                //将数据解析完毕之后最后执行的方法
                                @Override
                                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                                    System.out.println("数据解析完毕");
                                }
                            }).sheet().doRead();

        System.out.println("list = " + list);
    }
}
