package cn.xuelianyong.study.thread;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AdvancedExample {

    public static void main(String[] args) {
        // 自定义配置
        ThreadPoolConfig config = ThreadPoolConfig.builder()
                .poolType(PoolType.CPU_INTENSIVE)
                .corePoolSize(8)
                .maxPoolSize(16)
                .queueCapacity(500)
                .keepAliveTime(120)
                .threadNamePrefix("cpu-intensive")
                .build();

        // 创建线程池
        ThreadPoolManager cpuPool = new ThreadPoolManager("cpu-worker", config);

        // 提交计算密集型任务
        Future<Long> future = cpuPool.submit(() -> {
            long result = 0;
            for (long i = 0; i < 1_000_000_000; i++) {
                result += i;
            }
            return result;
        });

        try {
            System.out.println("Computation result: " + future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}