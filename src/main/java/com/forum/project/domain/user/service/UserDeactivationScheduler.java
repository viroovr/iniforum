package com.forum.project.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UserDeactivationScheduler {
    private final UserFacade userFacade;

    @Scheduled(cron = "0 0 0 * * *")
    public void deactivateInactiveUsers() {
        Duration inactivityPeriod = Duration.ofDays(30);
        userFacade.deactivateInactiveUsers(inactivityPeriod);
    }
}
