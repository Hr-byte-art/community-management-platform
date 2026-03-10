package com.community.controller;

import com.community.common.Result;
import com.community.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private ResidentService residentService;
    @Autowired
    private CommunityActivityService activityService;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private SecurityHazardService hazardService;
    @Autowired
    private VolunteerService volunteerService;
    @Autowired
    private FloatingPopulationService floatingService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ServiceEvaluationService serviceEvaluationService;

    @GetMapping("/overview")
    public Result<?> getOverview() {
        Map<String, Object> data = new HashMap<>();
        data.put("residentCount", residentService.count());
        data.put("activityCount", activityService.count());
        data.put("volunteerCount", volunteerService.countApproved());
        data.put("floatingCount", floatingService.countActive());
        data.put("pendingOrderCount", workOrderService.countByStatus(0));
        data.put("totalOrderCount", workOrderService.count());
        data.put("overtimeOrderCount", workOrderService.countOvertimeOpen());
        data.put("todayTodoOrderCount", workOrderService.countTodayTodo());
        data.put("noticeCount", noticeService.countPublished());
        data.put("pendingHazardCount", hazardService.countByStatus(0));
        data.put("evaluationStats", serviceEvaluationService.stats());
        return Result.success(data);
    }

    @GetMapping("/workorder")
    public Result<?> getWorkOrderStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("pending", workOrderService.countByStatus(0));
        data.put("processing", workOrderService.countByStatus(1));
        data.put("completed", workOrderService.countByStatus(2));
        data.put("closed", workOrderService.countByStatus(3));
        data.put("overtime", workOrderService.countOvertimeOpen());
        data.put("todayTodo", workOrderService.countTodayTodo());
        data.put("byType", workOrderService.countByType());
        data.put("byMonth", workOrderService.countByMonth());
        return Result.success(data);
    }

    @GetMapping("/activity")
    public Result<?> getActivityStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("normal", activityService.countByCancelled(0));
        data.put("cancelled", activityService.countByCancelled(1));
        data.put("byType", activityService.countByType());
        return Result.success(data);
    }

    @GetMapping("/resident")
    public Result<?> getResidentStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", residentService.count());
        data.put("living", residentService.countByStatus(1));
        data.put("movedOut", residentService.countByStatus(0));
        data.put("byBuilding", residentService.countByBuilding());
        return Result.success(data);
    }
}
