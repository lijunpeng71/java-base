package cn.xuelianyong.study.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SmartRejectPolicy implements RejectedExecutionHandler {
    private final AtomicInteger rejectedCount;

    SmartRejectPolicy(AtomicInteger rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        rejectedCount.incrementAndGet();
        System.err.println("Task rejected: " + r.toString());

        try {
            // 尝试重新入队（等待100ms）
            boolean retry = executor.getQueue().offer(r, 100, TimeUnit.MILLISECONDS);
            if (!retry) {
                System.err.println("Retry failed, executing in caller thread");
                r.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Retry interrupted, executing in caller thread");
            r.run();
        }
    }
}
