package com.zhoujl.demo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 */
/**
 * 属性配置错误的值时，而又不希望 Spring Boot 应用启动失败，我们可以设置 ignoreInvalidFields 属性为 true (默认为 false)
 * */
@ConfigurationProperties(
        prefix = "finance",
        ignoreInvalidFields = true
)
@Slf4j
@Data
public class FinanceProperties {

    private FileServerProperties fileServer = new FileServerProperties();


    @Data
    public class  FileServerProperties{
        Boolean streamSupported = false;
        String url = "http://47.105.161.220:8010";
        String publicFileUrl;
        String uploadFileUrl;
    }

}
