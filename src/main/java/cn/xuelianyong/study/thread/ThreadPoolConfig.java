package cn.xuelianyong.study.thread;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

@Data
@Builder
public class ThreadPoolConfig {
    /**
     * 线程池类型
     */
    @Builder.Default
    public PoolType poolType = PoolType.IO_INTENSIVE;
    /**
     * 核心线程数(默认自动计算)
     */
    @Builder.Default
    private int corePoolSize = -1;
    /**
     * 最大线程数(默认自动计算)
     */
    @Builder.Default
    private int maxPoolSize = -1;
    /**
     * 队列容量
     */
    @Builder.Default
    private int queueCapacity = 1024;

    /**
     * 线程空闲时间(秒)
     */
    @Builder.Default
    private long keepAliveTime = 60;

    /**
     * 允许回收核心线程
     */
    @Builder.Default
    private boolean allowCoreThreadTimeOut = true;

    /**
     * 线程名前缀
     */
    @Builder.Default
    private String threadNamePrefix = "app-pool";

    /**
     * 任务抱桩器
     */
    private Consumer<Runnable> taskWrapper;

}
