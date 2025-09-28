package cn.xuelianyong.study.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {

    /**
     * 线程池实例
     */
    private final ThreadPoolExecutor executor;
    /**
     * 线程配置
     */
    private final ThreadPoolConfig config;
    /**
     * 线程池名称
     */
    private final String poolName;
    /**
     * 拒绝任务数
     */
    private final AtomicInteger rejectedTaskCount = new AtomicInteger(0);

    public ThreadPoolManager(String poolName, ThreadPoolConfig config) {
        this.poolName = poolName;
        this.config = config;
        //自动计算线程数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int coreSize = config.getCorePoolSize() > 0 ? config.getCorePoolSize() : (PoolType.CPU_INTENSIVE.equals(config.getPoolType()) ? cpuCores + 1 : cpuCores * 2);
        int maxSize = config.getMaxPoolSize() > 0 ? config.getMaxPoolSize() : (PoolType.CPU_INTENSIVE.equals(config.getPoolType()) ? coreSize : coreSize * 4);
        //创建线程池
        this.executor = new ThreadPoolExecutor(
                coreSize,
                maxSize, config.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(config.getQueueCapacity()),
                new CustomThreadFactory(config.getThreadNamePrefix()),
                new SmartRejectPolicy(rejectedTaskCount));

        //允许回收核心线程
        executor.allowCoreThreadTimeOut(config.isAllowCoreThreadTimeOut());
        //注册关闭钩子
        registerShutdownHook();
    }

    public ThreadPoolManager(String poolName) {
        this(poolName, ThreadPoolConfig.builder().build());
    }


    /**
     * 提交任务
     *
     * @param task 待执行任务
     */
    public void execute(Runnable task) {
        if (config.getTaskWrapper() != null) {
            // 使用自定义任务包装器
            executor.execute(() -> config.getTaskWrapper().accept(task));
        } else {
            // 使用默认包装器（捕获异常）
            executor.execute(wrapTask(task));
        }
    }

    /**
     * 提交任务（有返回值）
     *
     * @param task 可调用任务
     * @return Future对象
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (config.getTaskWrapper() != null) {
            return executor.submit(() -> {
                config.getTaskWrapper().accept(() -> {
                    try {
                        task.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return task.call();
            });
        }
        return executor.submit(task);
    }


    /**
     * 获取线程池指标
     */
    public ThreadPoolMetrics getMetrics() {
        ThreadPoolMetrics metrics = new ThreadPoolMetrics();
        metrics.setPoolName(poolName);
        metrics.setCorePoolSize(executor.getCorePoolSize());
        metrics.setMaxPoolSize(executor.getMaximumPoolSize());
        metrics.setActiveThreads(executor.getActiveCount());
        metrics.setPoolSize(executor.getPoolSize());
        metrics.setCompletedTasks(executor.getCompletedTaskCount());
        metrics.setQueueSize(executor.getQueue().size());
        metrics.setQueueCapacity(config.getQueueCapacity());
        metrics.setRejectedTasks(rejectedTaskCount.get());
        return metrics;
    }


    /**
     * 动态调整核心线程数
     *
     * @param corePoolSize 新的核心线程数
     */
    public void setCorePoolSize(int corePoolSize) {
        executor.setCorePoolSize(corePoolSize);
    }

    /**
     * 动态调整最大线程数
     *
     * @param maxPoolSize 新的最大线程数
     */
    public void setMaxPoolSize(int maxPoolSize) {
        executor.setMaximumPoolSize(maxPoolSize);
    }


    // 默认任务包装器（确保异常被捕获）
    private Runnable wrapTask(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception ex) {
                System.err.println("Task execution failed: " + ex.getMessage());
                // 这里可以扩展：记录详细日志、发送告警等
            }
        };
    }


    /**
     * 注册JVM关闭钩子
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down thread pool: " + poolName);
            shutdownGracefully(30, TimeUnit.SECONDS);
        }));
    }


    /**
     * 优雅关闭线程池
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     */
    public void shutdownGracefully(long timeout, TimeUnit unit) {
        executor.shutdown(); // 停止接收新任务
        try {
            // 等待现有任务完成
            if (!executor.awaitTermination(timeout, unit)) {
                // 超时后强制取消任务
                executor.shutdownNow();
                // 再次等待
                boolean result = executor.awaitTermination(timeout, unit);
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    /**
     * 立即关闭线程池
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }
}
