package com.zhoujl.demo.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-2-14 11:17
 * @see: com.htzc.financebusiness.config
 * @Version: 1.0
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class SwaggerConfig {

    // 定义分隔符
    private static final String splitor = ";";


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2) //swagger版本
//                .globalOperationParameters(getGlobalOperationParameters())      //全局参数设置
                .select()
                //扫描那些controller
                .apis(RequestHandlerSelectors.basePackage("com.zhoujl.demo.controller"))
//                .apis(basePackage("com.htzc.financeweb.controller.api"+splitor+"com.htzc.financeweb.controller.sys"+splitor+"com.htzc.financeweb.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());

    }



    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }


    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage)     {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(splitor)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }


    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }



    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("汉唐智创金融服务平台swagger文档") //网站标题
                .description("汉唐智创金融服务平台swagger RESTful APIs......") //网站描述
                .version("1.0.0") //版本
                .contact(new Contact("ht","","")) //联系人
                .termsOfServiceUrl("http://localhost:2052/")   //项目地址
                .build();
    }





    //全局参数配置
//    private List<Parameter> getGlobalOperationParameters() {
//        List<Parameter> pars = new ArrayList<>();
//        ParameterBuilder parameterBuilder = new ParameterBuilder();
//        // header query cookie
//        parameterBuilder.name("Authorization").description("token").modelRef(new ModelRef("string")).parameterType("header").required(false);
//        pars.add(parameterBuilder.build());
//        return pars;
//    }

}
