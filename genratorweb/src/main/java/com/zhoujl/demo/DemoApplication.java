package com.zhoujl.demo;

import com.zhoujl.demo.config.FinanceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


@MapperScan(basePackages = "com.zhoujl.demo.dao")
@EnableConfigurationProperties({FinanceProperties.class})
@SpringBootApplication(scanBasePackages = {
        "com.zhoujl.demo"
//        "com.htzc.financeweb",
//        "com.htzc.rpcservice",
})
@EnableTransactionManagement     //开启声明式事物
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
