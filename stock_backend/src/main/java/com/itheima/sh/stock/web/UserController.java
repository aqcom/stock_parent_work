package com.itheima.sh.stock.web;

import com.itheima.sh.stock.pojo.entity.SysUser;
import com.itheima.sh.stock.pojo.vo.R;
import com.itheima.sh.stock.service.UserService;
import com.itheima.sh.stock.vo.LoginReqVo;
import com.itheima.sh.stock.vo.LoginRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
    web层，前端直接访问当前类
        复习：
            1.@RestController相当于@Controller+@ResponseBody
                1）@Controller 表示将当前类对象放到SpringIOC容器
                2）@ResponseBody 表示当前类的所有方法不管响应的是什么数据，最后给前端都变为json格式数据
            2.@RequestMapping("/api") 表示设置前端访问的路径
            3. @GetMapping("/{username}") :浏览器是get请求，接收传递的数据用户名，如果想将数据赋值给修饰方法的形参需要在形参位置
            使用注解@PathVariable修饰，如果/{username}大括号中的标识和形参名一致，那么直接书写@PathVariable注解
            如果不一致： @GetMapping("/{name}") ===>@PathVariable("name") String username
 */
@RestController
@RequestMapping("/api")
//@Api(tags = "用户模块-操作用户登录相关内容的代码实现") 属于swagger中的注解修饰web层的类的，tags属性表示对修饰类的说明
@Api(tags = "用户模块-操作用户登录相关内容的代码实现")
public class UserController {
    //自动装配服务层对象
    @Autowired
    private UserService userService;

    //定义方法根据前端传递的用户名admin查询数据封装到实体类SysUser中
    //前端请求路径：http://localhost:8091/api/admin===>admin表示携带过来的用户名
   /* @GetMapping("/{name}")
    public SysUser findUserByUserName(@PathVariable("name") String username){
        //使用业务层对象调用业务层根据用户名查询的方法
        SysUser user = userService.findUserByUserName(username);
        return user;
    }*/

    @GetMapping("/{username}")
    // @ApiOperation属于swagger中的注解用来修饰方法，属性value表示方法作用，response修饰方法返回值类型
    @ApiOperation(value = "根据用户名查询方法",response = SysUser.class)
    /*
        1.@ApiImplicitParams 注解属于swagger中的，描述方法的参数的
        2.@ApiImplicitParams注解中有一个属性：ApiImplicitParam[] value();，ApiImplicitParam属于注解类型
        3. @ApiImplicitParam注解常见属性：
            1）paramType = "path" 表示参数传参类型  path 表示rest风格  form  表单形式提交数据
            2）name = "username"： 形参名
            3）value = "用户名"：对形参的说明
            4）required = true：参数是否必须
     */
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "path",name = "username",value = "用户名",required = true)
    })
    public SysUser findUserByUserName(@PathVariable String username){
        //使用业务层对象调用业务层根据用户名查询的方法
        SysUser user = userService.findUserByUserName(username);
        return user;
    }

    /*
        定义用户登录方法
            1. @PostMapping("/login") 表示前端是post请求，login表示请求路径
            2.@RequestBody LoginReqVo user 如果前端提交的是json格式的数据，那么这里在形参位置使用@RequestBody
            注解修饰参数，要求json数据的key必须和实体类LoginReqVo属性名一致
     */
    @PostMapping("/login")
    @ApiOperation(value = "登录方法",response = R.class) //方法作用和返回值类型
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "path",name="user",value = "封装登录用户的请求数据",required = true)
    })
    public R<LoginRespVo> login(@RequestBody LoginReqVo user){
        //调用业务层方法
        R<LoginRespVo> r = userService.login(user);
        //返回给前端json数据
        return r;
    }

    /*
        后端服务器生成验证码的方法
        思路：将后端生成的验证码返回给前端
     */
    @GetMapping("/captcha")
    @ApiOperation(value = "生成验证码的",response = R.class)
    public R<Map> generateCaptcha(){
        //1.向服务层发送请求获取验证码
        R<Map> r = userService.generateCaptcha();
        //2.将r返回给前端
        return r;
    }
}
