package cn.xuelianyong.study.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolMetrics {

    /**
     * 线程池名称
     */
    private String poolName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 活动线程
     */
    private int activeThreads;

    /**
     * 线程池大小
     */
    private int poolSize;

    /**
     * 完成线程
     */
    private long completedTasks;

    /**
     * 队列大小
     */
    private int queueSize;


    /**
     * 队列大小
     */
    private int queueCapacity;

    /**
     * 拒绝的任务
     */
    private int rejectedTasks;

}
