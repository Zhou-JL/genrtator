package com.zhoujl.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-3-23 10:13
 * @see: com.zhoujl.provider
 * @Version: 1.0
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class GenratorProvider {
    public static void main(String[] args) {
        SpringApplication.run(GenratorProvider.class, args);
    }
}
