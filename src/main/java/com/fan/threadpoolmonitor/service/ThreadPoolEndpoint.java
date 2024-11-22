package com.fan.threadpoolmonitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 这个类是一个自定义的 Spring Boot Actuator Endpoint，
 * 自定义线程池监控 Endpoint 类。
 * 使用 Spring Boot Actuator 的 @Endpoint 注解暴露一个名为 "thread-pool" 的端点。
 * 主要功能是收集所有线程池的实时运行指标并返回。
 * @author fanshuaiyao
 * @date 2024/11/22 15:36
 * @version 1.0
 */
@Configuration
// 定义自定义 Actuator Endpoint，ID 为 "thread-pool"
@Endpoint(id="thread-pool")
public class ThreadPoolEndpoint {
    @Autowired
    private ThreadPoolForMonitorManager threadPoolForMonitorManager;

    /** 
     * @description: 定义一个只读操作，返回当前线程池的监控数据。
     * @param:  
     * @return: 
     * @return 包含线程池监控信息的 Map
     * @author fanshuaiyao
     * @date: 2024/11/22 15:38
     */ 
    @ReadOperation
    public Map<String,Object> threadPoolsMetric(){
        Map<String,Object> metricMap=new HashMap<>();
        List<Map> threadPools=new ArrayList<>();
        threadPoolForMonitorManager.getThreadPoolExecutorForMonitorConcurrentMap().forEach((k,v)->{
            ThreadPoolExecutorForMonitor tpe=(ThreadPoolExecutorForMonitor) v;
            // 保存单个线程池的详细信息
            Map<String, Object> poolInfo = new HashMap<>();
            poolInfo.put("thread.pool.name", k); // 线程池名称
            poolInfo.put("thread.pool.core.size", tpe.getCorePoolSize()); // 核心线程数
            poolInfo.put("thread.pool.largest.size", tpe.getLargestPoolSize()); // 历史最大线程数
            poolInfo.put("thread.pool.max.size", tpe.getMaximumPoolSize()); // 最大线程数
            poolInfo.put("thread.pool.thread.count", tpe.getPoolSize()); // 当前线程数
            poolInfo.put("thread.pool.max.costTime", tpe.getMaxCostTime()); // 最大耗时
            poolInfo.put("thread.pool.average.costTime", tpe.getAverageCostTime()); // 平均耗时
            poolInfo.put("thread.pool.min.costTime", tpe.getMinCostTime()); // 最小耗时
            poolInfo.put("thread.pool.active.count", tpe.getActiveCount()); // 活跃线程数
            poolInfo.put("thread.pool.completed.taskCount", tpe.getCompletedTaskCount()); // 已完成任务数
            poolInfo.put("thread.pool.queue.name", tpe.getQueue().getClass().getName()); // 队列类型
            poolInfo.put("thread.pool.rejected.name", tpe.getRejectedExecutionHandler().getClass().getName()); // 拒绝策略类型
            poolInfo.put("thread.pool.task.count", tpe.getTaskCount()); // 总任务数

            // 将线程池信息加入线程池列表
            threadPools.add(poolInfo);
        });
        metricMap.put("threadPools",threadPools);
        return metricMap;
    }
}