package cn.xuelianyong.study.thread;

import cn.hutool.json.JSONUtil;

import java.util.concurrent.TimeUnit;

public class ThreadPoolExample {

    public static void main(String[] args) {
        // 创建IO密集型线程池（默认配置）
        ThreadPoolManager ioPool = new ThreadPoolManager("io-worker");

        // 提交任务
        for (int i = 0; i < 100; i++) {
            int taskId = i;
            ioPool.execute(() -> {
                System.out.println("Processing task " + taskId + " on " + Thread.currentThread().getName());
                // 模拟IO操作
                System.out.println("metric:" + JSONUtil.toJsonStr(ioPool.getMetrics()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 获取线程池指标
        ThreadPoolMetrics metrics = ioPool.getMetrics();
        System.out.println("Active threads: " + metrics.getActiveThreads());
        System.out.println("Queue size: " + metrics.getQueueSize());

        // 关闭线程池（实际项目中通常不需要手动关闭，因为有ShutdownHook）
        ioPool.shutdownGracefully(10, TimeUnit.SECONDS);
    }
}