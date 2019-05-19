package com.fish.quartz;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/19 20:22
 */
@Configuration
@Component
@EnableScheduling
public class HelloTask {

    public void sayHello() {
        System.out.println("hello word");
    }

}
