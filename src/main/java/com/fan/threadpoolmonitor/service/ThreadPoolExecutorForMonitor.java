package com.fan.threadpoolmonitor.service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fanshuaiyao
 * @description: 此类继承ThreadPoolExecutor类
 * @date 2024/11/22 14:15
 */
public class ThreadPoolExecutorForMonitor extends ThreadPoolExecutor {

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    private static final String defaultPoolName="Default-Task";

    private static ThreadFactory threadFactory=new MonitorThreadFactory(defaultPoolName);

    public ThreadPoolExecutorForMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,threadFactory,defaultHandler);
    }
    public ThreadPoolExecutorForMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,new MonitorThreadFactory(poolName),defaultHandler);
    }
    public ThreadPoolExecutorForMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory,RejectedExecutionHandler handler,String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,threadFactory,handler);
    }

    //最短执行时间
    private long minCostTime;
    //最长执行时间
    private long maxCostTime;
    //总的耗时
    private AtomicLong totalCostTime=new AtomicLong();

    private ThreadLocal<Long> startTimeThreadLocal=new ThreadLocal<>();

    // 重写父类方法，super父类方法
    @Override
    public void shutdown() {
        super.shutdown();
    }

    // 在执行之前保存当前时间到ThreadLocal中
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTimeThreadLocal.set(System.currentTimeMillis());
        super.beforeExecute(t, r);
    }

    // 在执行之后加入计算时间的业务
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // 得到消耗的时间
        long costTime=System.currentTimeMillis()-startTimeThreadLocal.get();
        // 移除当前线程保存的值  防止内存泄漏
        startTimeThreadLocal.remove();
        // 更新最长耗时
        maxCostTime=maxCostTime>costTime?maxCostTime:costTime;
        // 如果是第一个完成的任务，初始化最小花费时间为第一个任务的完成时间  比大没关系，比小必须初始化一个，要不永远都是0
        if(getCompletedTaskCount()==0){
            minCostTime=costTime;
        }
        // 更新最小耗时
        minCostTime=minCostTime<costTime?minCostTime:costTime;
        // 累加任务总耗时，使用线程安全的`AtomicLong`类来避免并发问题
        totalCostTime.addAndGet(costTime);
        // 调用父类的`afterExecute`方法，保证父类逻辑的正常执行
        super.afterExecute(r, t);
    }

    public long getMinCostTime() {
        return minCostTime;
    }

    public long getMaxCostTime() {
        return maxCostTime;
    }

    //平均耗时
    public long getAverageCostTime(){
        if(getCompletedTaskCount()==0||totalCostTime.get()==0){
            return 0;
        }
        return totalCostTime.get()/getCompletedTaskCount();
    }


    // 调用覅父类的terminated方法
    @Override
    protected void terminated() {
        super.terminated();
    }

    static class MonitorThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        MonitorThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = poolName+"-pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
