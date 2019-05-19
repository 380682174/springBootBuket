package cn.fish.springbatch.patch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 21:58
 */
@Slf4j
public class CsvJobListener implements JobExecutionListener {

    private long startTime;
    private long endTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {

        startTime = System.currentTimeMillis();
        log.info("任务处理开始");

    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        endTime = System.currentTimeMillis();
        log.info("任务处理结束，总耗时=" + (endTime - startTime) + "ms");

    }
}
