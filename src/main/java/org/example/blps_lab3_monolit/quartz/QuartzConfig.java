package org.example.blps_lab3_monolit.quartz;

import org.example.blps_lab3_monolit.quartz.jobs.FavoriteNotificationJob;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(FavoriteNotificationJob.class);
        factory.setDurability(true);
        return factory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger(JobDetail job) {
        SimpleTriggerFactoryBean factory = new SimpleTriggerFactoryBean();
        factory.setJobDetail(job);
        factory.setRepeatInterval(120000);
        factory.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factory;
    }
}
