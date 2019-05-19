package com.fish.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @Description: 自定义任务类，通过注解实现
 * @Author devin.jiang
 * @CreateDate 2019/5/19 20:08
 */
@Component
public class HelloJob implements Job {

    private String name;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jdm = jobExecutionContext.getJobDetail().getJobDataMap();
        name = jdm.getString("name");

        System.out.println("hello: " + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
