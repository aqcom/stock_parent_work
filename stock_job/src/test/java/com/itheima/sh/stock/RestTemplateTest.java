package com.itheima.sh.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class RestTemplateTest {
    //自动装配模板对象
    @Autowired
    private RestTemplate restTemplate;
    /*
        测试一：get请求携带参数访问外部url
     */
    /**
     *
     */
    @Test
    public void test01() {
       //向模拟新浪服务器发送请求并接收响应数据
        /*
            1.localhost：因为我们自己搭建的项目在本地
            2.6666：自己搭建服务器的端口号 server.port=6666
            3.account自己搭建服务器的AccountController指定的路径@RequestMapping("/account")
            4.getByUserNameAndAddress表示自己搭建服务器的AccountController类中的方法@GetMapping("/getByUserNameAndAddress")
            5.userName=zhangsan&address=sh：携带请求参数
         */
        String url="http://localhost:6666/account/getByUserNameAndAddress?userName=zhangsan&address=sh";
        //服务器响应的数据全部封装到result对象中 响应状态码  响应数据 等
        ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
        //获取响应头
        /*
            [Content-Type:"application/json", Transfer-Encoding:"chunked", Date:"Sun,
            05 Feb 2023 04:12:58 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]
         */
        HttpHeaders headers = result.getHeaders();
        System.out.println(headers.toString());
        //获取响应状态码
        /*
            200 OK
         */
        HttpStatus statusCode = result.getStatusCode();
        System.out.println(statusCode.toString());
        //获取响应状态码
        /*
            statusCodeValue = 200
         */
        int statusCodeValue = result.getStatusCodeValue();
        System.out.println("statusCodeValue = " + statusCodeValue);
        //最重要，响应体数据
        //body = {"id":1,"userName":"zhangsan","address":"sh"}
        String body = result.getBody();
        System.out.println("body = " + body);
    }
    /*
        get请求响应数据自动封装vo实体对象
     */
    @Test
    public void test02() {
        //1.定义变量保存服务器地址
        String url="http://localhost:6666/account/getByUserNameAndAddress?userName=zhangsan&address=sh";
        //2.向服务器发送请求获取响应数据
        Account acc = restTemplate.getForObject(url, Account.class);
        //3.输出
        System.out.println("acc = " + acc);
    }

    /*
        请求头携带参数访问外部接口
        RestTemplate的方法：
            public <T> ResponseEntity<T> exchange(String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,
                                                             Class<T> responseType, Object... uriVariables)
            参数	说明
              url	调用的url地址
              HttpMethod method	  HttpMethod是枚举类，表示哪种请求方式 HTTP请求方式 ：GET、POST、PUT、DELETE等
              HttpEntity<?> requestEntity 	发起请求时携带的对象：请求头header 和/或 请求体body
              Class<T> responseType       	请求响应对象的类型
    */
    @Test
    public void test03() {
        //1.定义变量保存请求服务器的地址
        String url="http://localhost:6666/account/getHeader";
        //2.使用HttpHeaders设置请求头参数
        HttpHeaders headers=new HttpHeaders();
        //向请求头对象中存储数据
        headers.add("userName","zhangsan");
        //3.请求头填充到请求对象HttpEntity下
        HttpEntity httpEntity = new HttpEntity(headers);
        //4.使用restTemplate调用exchange方法发送请求
        //服务器响应的数据封装到ResponseEntity中
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        //5.使用ResponseEntity获取响应体数据
        String body = responseEntity.getBody();
        System.out.println("body = " + body);
    }
    /*
         POST请求模拟form表单访问外部接口
     */
    @Test
    public void test04() {
        //1.定义变量保存请求服务器地址
         String url="http://localhost:6666/account/addAccount";
        // 2.创建请求头类的对象，设置请求头，指定请求数据方式
        HttpHeaders headers = new HttpHeaders();
        // 3.告知服务器，请求方式是form表单提交，这样对方解析数据时，就会按照form表单的方式解析处理
        //注意：如果是模拟form表单必须先在请求头中设置表单类型目的是告知服务器请求的类型是表单
        headers.add("Content-type","application/x-www-form-urlencoded");
        // 4.组装模拟form表单提交数据，内部元素相当于form表单的input框
        //请求体数据都位于LinkedMultiValueMap对象中
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("userName","胡歌");
        map.add("address","上海");

        // 5.将请求头和map集合添加到HttpEntity请求实体对象中
        /*
            注意：
                public HttpEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers)
                        参数一：请求体数据
                        参数二：请求头数据
         */
        HttpEntity httpEntity = new HttpEntity(map,headers);
        // 6.向服务器发送请求，获取响应数据
        ResponseEntity<Account> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Account.class);
        // 7.获取响应体数据
        Account account = responseEntity.getBody();
        System.out.println("account = " + account);
    }

    /**
     * post发送json数据
     * 1.请求地址 ： http://localhost:6666/account/updateAccount
     * 2.通过请求设置请求头告知服务器发送的数据为json格式：
     *     Content-type:application/json; charset=utf-8
     "{\"address\":\"上海\",\"id\":\"1\",\"userName\":\"zhangsan\"}"
     */
    @Test
    public void test05() {
        //1.定义变量保存服务器地址
        String url="http://localhost:6666/account/updateAccount";
        //2.创建请求头对象
        HttpHeaders headers = new HttpHeaders();
        //3.向请求头中添加参数，告知服务器携带请求的数据是json格式数据
        headers.add("Content-type","application/json;charset=utf-8");
        //4.定义变量保存请求数据
        String jsonStr="{\"address\":\"上海\",\"id\":\"1\",\"userName\":\"zhangsan\"}";
        //如果发送的是json格式的数据，直接将数据放到HttpEntity的第一个参数位置作为请求体即可
        HttpEntity httpEntity = new HttpEntity(jsonStr, headers);
        //5.发送请求并获取响应
        ResponseEntity<Account> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Account.class);
        Account account = responseEntity.getBody();
        System.out.println("account = " + account);
    }

    /*
        获取接口响应的cookie数据
     */
    @Test
    public void test06() {
        //1.定义变量保存服务器地址
        String url="http://localhost:6666/account/getCookie";
        //2.获取服务器端的响应数据
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        //3.获取响应头中的cookie信息
        HttpHeaders headers = responseEntity.getHeaders();
        //list = [userName=zhangsan]
        List<String> list = headers.get("set-cookie");
        System.out.println("list = " + list);
        //4.获取响应体数据 hello cookie
        System.out.println(responseEntity.getBody());
    }

    @Test
    public void test07(){
        //1.定义要操作的字符串变量
        String line = "This order was placed for QT3000! OK?";
        //2.定义正则
        /*
            (\\D*) 表示第一组 ===》\\D 表示非数字字符，*表示大于等于0个字符
            (\d+) 表示第二组 ===》\\d 表示数字字符，+表示大于等于1个字符
            (.*) 表示第三组 ===》 .表示任意字符，*表示大于等于0个字符
         */
        String pattern = "(\\D*)(\\d+)(.*)";
        //3.根据正则pattern创建 Pattern 对象，pattern 对象是一个正则表达式的编译表示
        Pattern r = Pattern.compile(pattern);
        //4.根据编译对象Pattern调用matcher方法根据被操作的字符串line创建 matcher 对象
        //Matcher 对象是对输入字符串进行解释和匹配操作的引擎
        Matcher m = r.matcher(line);
        /*
            5.Matcher类的查找方法
                public boolean find()对字符串进行匹配，匹配的字符串可以在任意位置，如果存在则返回true,不存在返回false。
         */
        if (m.find()) {
            /*
                6.Matcher类的获取组的方法：
                Found value: This order was placed for QT3000! OK?
                Found value: This order was placed for QT
                Found value: 3000
                Found value: ! OK?
             */
            // group(0) 表示获取整个字符串Found value: This order was placed for QT3000! OK?
            System.out.println("Found value: " + m.group(0) );
            // group(1) 表示获取第一组即满足正则(\\D*)字符串Found value: This order was placed for QT
            System.out.println("Found value: " + m.group(1) );
            // group(2) 表示获取第二组即满足正则(\\d+)字符串Found value: 3000
            System.out.println("Found value: " + m.group(2) );
            // group(3) 表示获取第三组即满足正则(.*)字符串Found value: ! OK?
            System.out.println("Found value: " + m.group(3) );
        } else {
            System.out.println("NO MATCH");
        }
    }

}
