package com.fan.threadpoolmonitor.service;

import com.fan.threadpoolmonitor.ResizeLinkedBlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description: 此类的作用是实现线程池的管理和初始化，初始化时根据配置文件来生成的
 * 1. 从配置文件中获取线程池的基本配置
 * 2. 根据配置信息来构建ThreadPoolExecutorForMonitor实例
 * 3. 把实例信息保存到集合中
 * @author fanshuaiyao
 * @date 2024/11/22 15:04
 * @version 1.0
 */
@Component
public class ThreadPoolForMonitorManager {

    // 注入包含线程池配置的属性类
    @Autowired
    ThreadPoolConfigurationProperties poolConfigurationProperties;

    // 用于保存所有线程池实例，键是线程池名称，值是对应的线程池实例
    private final ConcurrentMap<String, ThreadPoolExecutorForMonitor> threadPoolExecutorForMonitorConcurrentMap = new ConcurrentHashMap<>();

    // 初始化方法，用于根据配置动态创建线程池实例
    @PostConstruct
    public void init() {
        // 遍历配置文件中定义的线程池信息
        poolConfigurationProperties.getExecutors().forEach(threadPoolProperties -> {
            // 如果线程池集合中还没有该线程池，则创建
            if (!threadPoolExecutorForMonitorConcurrentMap.containsKey(threadPoolProperties.getPoolName())) {
                // 创建自定义的 ThreadPoolExecutorForMonitor 线程池实例
                ThreadPoolExecutorForMonitor executorForMonitor = new ThreadPoolExecutorForMonitor(
                        threadPoolProperties.getCorePoolSize(), // 核心线程数
                        threadPoolProperties.getMaxmumPoolSize(), // 最大线程数
                        threadPoolProperties.getKeepAliveTime(), // 线程空闲保持时间
                        threadPoolProperties.getUnit(), // 时间单位
                        new ResizeLinkedBlockingQueue<>(threadPoolProperties.getQueueCapacity()), // 动态队列容量  大小可以自己设置在配置文件
                        threadPoolProperties.getPoolName() // 线程池名称
                );
                // 将创建的线程池实例放入集合中，键为线程池名称
                threadPoolExecutorForMonitorConcurrentMap.put(threadPoolProperties.getPoolName(), executorForMonitor);
            }
        });
    }

    // 根据线程池名称获取对应的线程池实例
    public ThreadPoolExecutorForMonitor getThreadPoolExecutor(String poolName) {
        // 从集合中获取线程池实例
        ThreadPoolExecutorForMonitor threadPoolExecutorForMonitor = threadPoolExecutorForMonitorConcurrentMap.get(poolName);
        // 如果找不到对应的线程池，抛出运行时异常
        if (threadPoolExecutorForMonitor == null) {
            throw new RuntimeException("找不到名字为" + poolName + "的线程池");
        }
        return threadPoolExecutorForMonitor; // 返回线程池实例
    }

    // 获取整个线程池集合
    public ConcurrentMap<String, ThreadPoolExecutorForMonitor> getThreadPoolExecutorForMonitorConcurrentMap() {
        return this.threadPoolExecutorForMonitorConcurrentMap; // 返回集合
    }
}
