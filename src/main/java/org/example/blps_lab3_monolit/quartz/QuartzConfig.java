package org.example.blps_lab3_monolit.quartz;

import org.example.blps_lab3_monolit.quartz.jobs.FavoriteNotificationJob;
import org.example.blps_lab3_monolit.quartz.jobs.SubscriptionCheckJob;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class QuartzConfig {

    @Bean(name = "favoriteNotificationJob")
    public JobDetail favoriteNotificationJob() {
        return newJob().ofType(FavoriteNotificationJob.class).storeDurably(true).build();
    }

    @Bean
    public SimpleTriggerFactoryBean favoriteNotificationJobTrigger(@Qualifier("favoriteNotificationJob") JobDetail favoriteNotificationJobDetail) {
        SimpleTriggerFactoryBean factory = new SimpleTriggerFactoryBean();
        factory.setJobDetail(favoriteNotificationJobDetail);
        factory.setRepeatInterval(120000);
        factory.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factory;
    }

    @Bean(name = "subscriptionCheckJob")
    public JobDetail subscriptionCheckJob() {
        return newJob().ofType(SubscriptionCheckJob.class).storeDurably(true).build();
    }

    @Bean
    public SimpleTriggerFactoryBean subscriptionCheckJobTrigger(@Qualifier("subscriptionCheckJob") JobDetail subscriptionCheckJobDetail) {
        SimpleTriggerFactoryBean factory = new SimpleTriggerFactoryBean();
        factory.setJobDetail(subscriptionCheckJobDetail);
        factory.setRepeatInterval(180000);
        factory.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return factory;
    }
}
