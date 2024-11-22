package com.fan.threadpoolmonitor.service;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author fanshuaiyao
 * @description: 此类声明ThreadPool的核心属性
 * @date 2024/11/22 14:44
 */
@Data
public class ThreadPoolProperties {

    private String poolName;
    private int corePoolSize;
    private int maxmumPoolSize=Runtime.getRuntime().availableProcessors();
    private long keepAliveTime=60;
    private TimeUnit unit= TimeUnit.SECONDS;
    private int queueCapacity=Integer.MAX_VALUE;
}
