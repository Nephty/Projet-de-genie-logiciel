package com.example.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccountScheduler {

    private final long minute = 60;
    private final long hour = 60 * minute;
    private final long day = 24 * hour;

    @Scheduled(fixedRate = day)
    public void processAccount() {}
}
