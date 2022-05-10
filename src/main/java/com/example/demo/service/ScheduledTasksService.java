package com.example.demo.service;

import com.example.demo.client.MMediaGroupCovidAPIClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasksService {

    Logger logger = LoggerFactory.getLogger(ScheduledTasksService.class);
    @Autowired
    MMediaGroupCovidAPIClient covidAPIClient;
    @Autowired
    CacheManager cacheManager;

    @Scheduled(initialDelay = 0, fixedRate = 60 * 60 * 1000)
    public void scheduledStatisticsRefresh() {
        logger.info("Scheduled statistic refresh task run");
        Cache statisticsCache = cacheManager.getCache("statistics");
        if (statisticsCache != null) {
            statisticsCache.clear();
            logger.info("Statistics cache cleared");
        }
        covidAPIClient.getStatistic();
    }
}
