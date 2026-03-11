package com.community.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.community.annotation.Auth;
import com.community.entity.*;
import com.community.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExportController {
    @Autowired
    private ResidentService residentService;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private CommunityActivityService activityService;
    @Autowired
    private FloatingPopulationService floatingService;

    @GetMapping("/resident")
    @Auth(permissions = {"btn.resident.export"})
    public void exportResident(HttpServletResponse response) throws Exception {
        setExcelResponse(response, "居民信息");
        List<Resident> list = residentService.list();
        EasyExcel.write(response.getOutputStream(), Resident.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("居民信息").doWrite(list);
    }

    @GetMapping("/workorder")
    @Auth(permissions = {"btn.workorder.export"})
    public void exportWorkOrder(HttpServletResponse response) throws Exception {
        setExcelResponse(response, "工单数据");
        List<WorkOrder> list = workOrderService.list();
        EasyExcel.write(response.getOutputStream(), WorkOrder.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("工单数据").doWrite(list);
    }

    @GetMapping("/activity")
    @Auth
    public void exportActivity(HttpServletResponse response) throws Exception {
        setExcelResponse(response, "活动数据");
        List<CommunityActivity> list = activityService.list();
        EasyExcel.write(response.getOutputStream(), CommunityActivity.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("活动数据").doWrite(list);
    }

    @GetMapping("/floating")
    @Auth
    public void exportFloating(HttpServletResponse response) throws Exception {
        setExcelResponse(response, "流动人口");
        List<FloatingPopulation> list = floatingService.list();
        EasyExcel.write(response.getOutputStream(), FloatingPopulation.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("流动人口").doWrite(list);
    }

    private void setExcelResponse(HttpServletResponse response, String filename) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");
    }
}
