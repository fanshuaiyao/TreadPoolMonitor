## 此程序通过springBoot的EndPoint来实现对线程池的监控
* 通过重写**ThreadPoolExecutor**类实现一个监控器实例，添加了计算耗时的任务
* 通过配置文件和配置类加载线程池的核心属性
* 通过**ThreadPoolForMonitorManager**管理和初始化线程池
* 为了能够手动设置阻塞队列的大小，重新复制出来了**LinkedBlockingQueue**类为**ResizeLinkedBlockingQueue**，添加**setCapacity**方法设置阻塞队列的容量


模拟执行任务

* 请求地址：
> http://localhost:8080/execute
* 返回值
```
success
```

监控线程池状态

* 请求地址：
> http://localhost:8080/actuator/thread-pool
* 返回值
```json
{
  "threadPools": [
    {
      "thread.pool.queue.name": "com.fan.threadpoolmonitor.ResizeLinkedBlockingQueue",
      "thread.pool.core.size": 2,
      "thread.pool.min.costTime": 0,
      "thread.pool.completed.taskCount": 0,
      "thread.pool.max.costTime": 0,
      "thread.pool.task.count": 0,
      "thread.pool.name": "second-monitor-thread-pool",
      "thread.pool.largest.size": 0,
      "thread.pool.rejected.name": "java.util.concurrent.ThreadPoolExecutor$AbortPolicy",
      "thread.pool.active.count": 0,
      "thread.pool.thread.count": 0,
      "thread.pool.average.costTime": 0,
      "thread.pool.max.size": 4
    },
    {
      "thread.pool.queue.name": "com.fan.threadpoolmonitor.ResizeLinkedBlockingQueue",
      "thread.pool.core.size": 4,
      "thread.pool.min.costTime": 47,
      "thread.pool.completed.taskCount": 35,
      "thread.pool.max.costTime": 27797,
      "thread.pool.task.count": 100,
      "thread.pool.name": "first-monitor-thread-pool",
      "thread.pool.largest.size": 4,
      "thread.pool.rejected.name": "java.util.concurrent.ThreadPoolExecutor$AbortPolicy",
      "thread.pool.active.count": 4,
      "thread.pool.thread.count": 4,
      "thread.pool.average.costTime": 4678,
      "thread.pool.max.size": 8
    }
  ]
}
```