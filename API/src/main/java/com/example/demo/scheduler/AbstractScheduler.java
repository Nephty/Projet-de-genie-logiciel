package com.example.demo.scheduler;


public abstract class AbstractScheduler {

    protected final long minute = 60;
    protected final long hour = 60 * minute;
    protected final long day = 24 * hour;
    protected final long year = 365 * day;
}
