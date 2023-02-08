package com.itheima.sh.stock.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
//表示swagger2相关技术的开启。会扫描当前类所在包，以及子包中所有的类型中的注解。做swagger文档的定制
@EnableSwagger2
//该注解是knife4j提供的增强注解,Ui提供了例如动态参数、参数过滤等增强功能,
// 如果你想使用这些增强功能就必须加该注解，否则可以不用加
@EnableKnife4j
//导入对象校验配置类
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {
    @Bean
    public Docket buildDocket() {
        //构建在线API概要对象
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .select()
                // 要扫描的API(Controller)基础包
                .apis(RequestHandlerSelectors.basePackage("com.itheima.sh.stock.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo buildApiInfo() {
        //网站联系方式
        Contact contact = new Contact("黑马程序员", "https://www.itheima.com/", "itcast@163.com");
        return new ApiInfoBuilder()
                //页面标题
                .title("今日指数-在线接口API文档")
                //描述
                .description("这是一个方便前后端开发人员快速了解开发接口需求的在线接口API文档")
                .contact(contact)
                //版本号
                .version("1.0.0").build();
    }
}