package com.fish.quartz;

import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

/**
 * @Description: 配置Quartz的Scheduler调度器
 * @Author devin.jiang
 * @CreateDate 2019/5/19 20:10
 */
//@Configuration
public class QuartzConfigure {

    /**
     * 使用MethodInvokingJobDetailFactoryBean进行配置，自定义任务不需要实现Job接口
     * @param helloTask
     * @return
     */
    @Bean(name = "jobDetail")
    public MethodInvokingJobDetailFactoryBean detailFactoryBean(HelloTask helloTask) {

        MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
        //是否并发执行
        jobDetailFactoryBean.setConcurrent(false);
        //设置需要执行的实体类对应的对象
        jobDetailFactoryBean.setTargetObject(helloTask);
        //设置需要执行的方法
        jobDetailFactoryBean.setTargetMethod("sayHello");

        return jobDetailFactoryBean;

    }

    /**
     * 配置储发器
     * @param jobDetailFactoryBean
     * @return
     */
    @Bean(name = "cronJobTrigger")
    public CronTriggerFactoryBean cronJobTrigger(MethodInvokingJobDetailFactoryBean jobDetailFactoryBean) {

        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        triggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
        //设置cron表达式，10秒钟执行一次
        triggerFactoryBean.setCronExpression("*/10 * * * * ?");

        return triggerFactoryBean;

    }

    /**
     * 配置Scheduler
     * @param cronJobTrigger
     * @return
     */
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactoryBean(Trigger cronJobTrigger) {

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //用于Quartz集群，QuartzScheduler启动时会更新已存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //延时启动，应用启动1秒后
        schedulerFactoryBean.setStartupDelay(1);
        //注册触发器
        schedulerFactoryBean.setTriggers(cronJobTrigger);

        return schedulerFactoryBean;

    }

}
