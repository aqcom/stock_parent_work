package com.itheima.sh.stock.service.impl;

import com.google.common.base.Strings;
import com.itheima.sh.stock.mapper.SysUserMapper;
import com.itheima.sh.stock.pojo.entity.SysUser;
import com.itheima.sh.stock.pojo.vo.R;
import com.itheima.sh.stock.pojo.vo.ResponseCode;
import com.itheima.sh.stock.service.UserService;
import com.itheima.sh.stock.utils.IdWorker;
import com.itheima.sh.stock.vo.LoginReqVo;
import com.itheima.sh.stock.vo.LoginRespVo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
    业务层
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    //自动装配Mapper对象
    @Autowired
    private SysUserMapper userMapper;

    //装配密码加密对象
    @Autowired
    private PasswordEncoder passwordEncoder;

    //装配雪花算法对象
    @Autowired
    private IdWorker idWorker;

    //装配redis模板对象
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public SysUser findUserByUserName(String username) {
        //使用userMapper对象调用SysUserMapper接口中的根据用户名查询的方法
        SysUser user = userMapper.findUserByUserName(username);
        //将查询的数据返回给web层
        return user;
    }
    /*
        登录
     */
    @Override
    public R<LoginRespVo> login(LoginReqVo user) {
        //1.先校验web层传递的数据是否合法，如果非法直接结束方法
        /*
            1）user == null 判断用户对象是否是null,如果是null整体返回true
            2)Strings.isNullOrEmpty(user.getUsername()) 判断前端提交的用户名是否为空，如果为空返回true
            3)Strings.isNullOrEmpty(user.getCode()) 判断前端输入框输入的验证码是否为空，如果为空返回true
            4) Strings.isNullOrEmpty(user.getRkey()) 判断前端是否提交了rkey，如果为空返回true
         */
        if(user == null ||
                Strings.isNullOrEmpty(user.getUsername()) ||
                Strings.isNullOrEmpty(user.getPassword()) ||
                Strings.isNullOrEmpty(user.getCode()) ||
                Strings.isNullOrEmpty(user.getRkey())){
            //2.如果执行到这里说明传入的参数是非法的。不能向下执行了，返回给前端数据
            /*
                1)R类中的方法：
                     public static <T> R<T> error(ResponseCode res){//ResponseCode res = ResponseCode.DATA_ERROR
                        //res.getCode() 获取0  res.getMessage()获取异常信息
                        //return new R<T>(res.getCode(),res.getMessage());
                        return new R<T>("0","参数异常");
                    }
                2)ResponseCode.DATA_ERROR===>DATA_ERROR(0,"参数异常")===>执行枚举类ResponseCode中的构造方法
                public enum ResponseCode{
                         ResponseCode(int code, String message) {//int code=0,String message="参数异常"
                            //将形参值赋值给ResponseCode成员变量
                            this.code = code;
                            this.message = message;
                        }

                         private int code;//状态码 0
                         private String message;//消息  "参数异常"
                   }
             */
            //将对象new R<T>("0","参数异常");返回web层
            return R.error(ResponseCode.DATA_ERROR);
        }


        //如果程序能够执行到这里说明数据是非空的
        //根据提交的rkey到redis中获取验证码的值其实就是浏览器图片中的验证码
        String redisCode = (String) redisTemplate.opsForValue().get(user.getRkey());

        //校验从redis中获取的验证码是否为空，或者输入框输入的验证码是否等于redis中的验证码,如果非法则返回给前端错误信息
        //Strings.isNullOrEmpty(redisCode) true表示redisCode是空
        /*
            1.如果Strings.isNullOrEmpty(redisCode) 为false说明redisCode不是空，然后继续向后执行代码：
            2.redisCode.equalsIgnoreCase(user.getCode())：
                1）redisCode 是从redis获取的验证码即图片中的
                2）user.getCode() 表示前端提交即输入框输入的验证码
                3）如果两个验证码不相等，redisCode.equalsIgnoreCase(user.getCode())返回false
         */
        if(Strings.isNullOrEmpty(redisCode) || !redisCode.equalsIgnoreCase(user.getCode())){
            //说明验证码有误，响应给web层
            return R.error(ResponseCode.CHECK_CODE_ERROR);
        }

        //如果程序执行到这里说明验证码校验通过
        //根据redis的key删除redis中存储验证码的键值
        redisTemplate.delete(user.getRkey());

        //3.根据用户名到dao层查询用户信息
        SysUser u = userMapper.findUserByUserName(user.getUsername());
        //4.判断u和密码是否匹配
        //u == null 说明没有查询到用户
        //passwordEncoder.matches(明文,密文)
        //user.getPassword() 表示浏览器传递的明文
        //u.getPassword()表示密码是数据库查询的
        // passwordEncoder.matches(user.getPassword(),u.getPassword()) 如果匹配返回true,不匹配返回false
        if(u == null || !passwordEncoder.matches(user.getPassword(),u.getPassword())){
            //5.说明用户名或者密码有误
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }

        //6.如果能够执行到这里，说明登录成功，返回给web层数据
        LoginRespVo respVo = new LoginRespVo();
        //将源数据对象u封装到目标对象respVo
        BeanUtils.copyProperties(u,respVo);
        return R.ok(respVo);
    }
    //生成验证码给浏览器客户端
    @Override
    public R<Map> generateCaptcha() {
        //1.使用commons.lang3包下的工具类RandomStringUtils生成4位数字验证码
        String codeStr = RandomStringUtils.randomNumeric(4);//4表示随机生成4位数字  假设 "5411"
        //2.获取全局唯一id "rkey":"1613917482278719496"并转换为字符串类型
//        idWorker.nextId()+"";
        String rkey = String.valueOf(idWorker.nextId());
        //3.验证码存入redis中，并设置有效期1分钟 ===>1613917482278719496=5411
        //60表示60秒即1分钟，因为指定单位是TimeUnit.SECONDS
        redisTemplate.opsForValue().set(rkey,codeStr,60, TimeUnit.SECONDS);
        //4.组装数据:将随机生成的唯一id值和验证码存入到Map集合中
        Map map = new HashMap();
        map.put("code",codeStr);//注意啦：map的key不能随便乱写，一定要和前端规定的接口文档一致必须是code
        map.put("rkey",rkey);
        //5.将存放的验证码和唯一的id值的map集合响应给web层
        return R.ok(map);
    }
}
