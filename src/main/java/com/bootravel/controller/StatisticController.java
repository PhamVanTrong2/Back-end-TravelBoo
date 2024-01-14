package com.bootravel.controller;

import com.bootravel.payload.responses.*;
import com.bootravel.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/statistic")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;
    @PostMapping("system/data")
    public DataNumberSystemResponse dataSystem() throws Exception {
        return statisticService.dataSystem();
    }

    @PostMapping("system/total-income")
    public TotalIncomeResponse totalIncomeSystem() {
        return statisticService.totalIncomeSystem();
    }

    @PostMapping("system/total-booking-weekly")
    public TotalBookingWeeklyResponse totalBookingWeeklySystem() {
        return statisticService.totalBookingWeeklySystem();
    }

    @PostMapping("business-admin/data")
    public DataNumberBusinessAdminResponse dataBusinessAdmin() {
        return statisticService.dataBusinessAdmin();
    }

    @PostMapping("business-admin/total-income")
    public TotalIncomeResponse totalIncomeBusinessAdmin() {
        return statisticService.totalIncomeBusinessAdmin();
    }

    @PostMapping("business-admin/total-booking-weekly")
    public TotalBookingWeeklyResponse totalBookingWeeklyBusinessAdmin() {
        return statisticService.totalBookingWeeklyBusinessAdmin();
    }
    @PostMapping("business-owner/data")
    public DataNumberBusinessOwnerResponse dataBusinessOwner() {
        return statisticService.dataBusinessOwner();
    }
    @PostMapping("business-owner/total-income")
    public TotalIncomeResponse totalIncomeBusinessOwner() {
        return statisticService.totalIncomeBusinessOwner();
    }
    @PostMapping("business-owner/total-booking-weekly")
    public TotalBookingWeeklyResponse totalBookingWeeklyBusinessOwner() {
        return statisticService.totalBookingWeeklyBusinessOwner();
    }
}
