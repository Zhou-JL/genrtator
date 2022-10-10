package com.zhoujl.demo.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: zjl
 * @company: 北京汉唐智创科技有限公司
 * @time: 2022-10-10 11:47
 * @see: com.zhoujl.demo.utils
 * @Version: 1.0
 */
@Slf4j
@Component
public class IdGeneratorSnowFlake {
    //工作id  0-31
    private long workerId = 0;
    //数据中心ID   0-31
    private long datacenterId = 1;
    private Snowflake snowflake = IdUtil.createSnowflake(workerId,datacenterId);

    @PostConstruct
    public void init(){
        try {
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            log.info("当前机器的workerId: {}",workerId);
        }catch (Exception e){
            e.printStackTrace();
            log.error("当前机器的workerId获取失败",e);
            workerId = NetUtil.getLocalhost().hashCode();
        }
    }

    public synchronized long snowflakeTd(){
        return snowflake.nextId();
    }

    public synchronized long snowflakeTd(long workerId,long datacenterId){
        Snowflake snowflake = IdUtil.createSnowflake(workerId,datacenterId);
        return snowflake.nextId();
    }


    public static void main(String[] args) {
        System.out.println(new IdGeneratorSnowFlake().snowflakeTd());
    }
}
