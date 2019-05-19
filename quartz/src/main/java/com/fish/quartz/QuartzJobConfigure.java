package com.fish.quartz;

import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

/**
 * @Description: 配置Quartz的Scheduler调度器
 * @Author devin.jiang
 * @CreateDate 2019/5/19 20:10
 */
@Configuration
public class QuartzJobConfigure {

    /**
     * 使用JobDetailFactoryBean进行配置，自定义任务需要实现Job接口
     * @param helloJob
     * @return
     */
    @Bean("jobDetail")
    public JobDetailFactoryBean jobDetailFactoryBean(HelloJob helloJob) {

        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(helloJob.getClass());
        jobDetailFactoryBean.setDurability(true);
        //额外参数
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("name","Quartz");
        jobDetailFactoryBean.setJobDataMap(jobDataMap);

        return jobDetailFactoryBean;

    }

    /**
     * 普通的触发器，此处也可用Cron储发器实现
     * @param jobDetail
     * @return
     */
    @Bean("simpleTrigger")
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean(JobDetailFactoryBean jobDetail) {

        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(jobDetail.getObject());
        simpleTriggerFactoryBean.setStartDelay(0);
        simpleTriggerFactoryBean.setRepeatInterval(1000);

        return simpleTriggerFactoryBean;

    }

    /**
     * 配置调度容器
     * @param simpleTrigger
     * @return
     */
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactoryBean(Trigger simpleTrigger) {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //用于Quartz集群，QuartzScheduler启动时会更新已存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //延时启动，应用启动1秒后
        schedulerFactoryBean.setStartupDelay(1);
        //注册触发器
        schedulerFactoryBean.setTriggers(simpleTrigger);

        return schedulerFactoryBean;

    }

}
