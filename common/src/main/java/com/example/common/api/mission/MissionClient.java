package com.example.common.api.mission;

import com.example.common.config.feign.BaseFeignClientRequestIntercepter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mission-service", url = "${exchange.services.mission-service.url:http://localhost:8082}", configuration = BaseFeignClientRequestIntercepter.class)
public interface MissionClient {

    @GetMapping(value = "/internal/missions/status", produces = "application/json")
    ResponseEntity<String> getMissionStatus(@RequestParam("missionId") Long missionId);

    @PostMapping(value = "/api/internal/achievements/award")
    void awardAchievement(@RequestParam("userId") Long userId, @RequestParam("type") com.example.common.enums.AchievementType type);

    @PostMapping(value = "/api/internal/achievements/init")
    void initAchievements(@RequestParam("userId") Long userId);
}
