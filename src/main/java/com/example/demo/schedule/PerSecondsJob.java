package com.example.demo.schedule;


import com.example.demo.service.ProcessRobotUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PerSecondsJob {

    @Autowired
    private ProcessRobotUpdateService processRobotUpdateService;

    @Scheduled(cron = "*/1 * * * * ?")
    public void getRobotUpdate() throws Exception {
        processRobotUpdateService.process();
    }

}
