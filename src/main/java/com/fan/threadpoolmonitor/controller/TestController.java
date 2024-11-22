package com.fan.threadpoolmonitor.controller;


import com.fan.threadpoolmonitor.service.ThreadPoolExecutorForMonitor;
import com.fan.threadpoolmonitor.service.ThreadPoolForMonitorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @description: TestController提供使用线程池的方法，用来查看在调用之前之后通过EndPoint获取的Metric数据的变化
 * @author fanshuaiyao
 * @date 2024/11/22 15:47
 * @version 1.0
 */
@RestController
public class TestController {
    // 定义线程池名称，用于测试的线程池
    private final String poolName = "first-monitor-thread-pool";

    @Autowired
    ThreadPoolForMonitorManager threadPoolForMonitorManager;

    // 定义一个GET请求的方法，模拟执行任务
    @GetMapping("/execute")
    public String doExecute() {
        // 从线程池管理器中获取指定名称的线程池
        ThreadPoolExecutorForMonitor tpe = threadPoolForMonitorManager.getThreadPoolExecutor(poolName);

        // 启动100个任务，模拟任务的执行
        for (int i = 0; i < 100; i++) {
            // 将任务提交给线程池
            tpe.execute(() -> {
                try {
                    // 每个任务随机休眠0到4秒
                    Thread.sleep(new Random().nextInt(4000));
                } catch (InterruptedException e) {
                    // 处理线程中断异常
                    e.printStackTrace();
                }
            });
        }

        // 返回成功的响应
        return "success";
    }
}
