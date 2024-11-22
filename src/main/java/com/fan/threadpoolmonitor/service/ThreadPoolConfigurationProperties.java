package com.fan.threadpoolmonitor.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * @author fanshuaiyao
 * @description: 此类用来绑定怕配置文件  此类提供了获取配置文件属性的功能
 * @date 2024/11/22 14:42
 */
@ConfigurationProperties(prefix = "monitor.threadpool")
@Data
public class ThreadPoolConfigurationProperties {

    private List<ThreadPoolProperties>  executors=new ArrayList<>();

}
