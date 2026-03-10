package com.community.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.*;
import com.community.converter.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("community_activity")
public class CommunityActivity {
    @TableId(type = IdType.AUTO)
    @ExcelProperty("编号")
    private Long id;
    
    @ExcelProperty("活动标题")
    @ColumnWidth(20)
    private String title;
    
    @ExcelProperty("活动内容")
    @ColumnWidth(30)
    private String content;
    
    @ExcelIgnore
    private String coverImage;
    
    @ExcelProperty(value = "活动类型", converter = ActivityTypeConverter.class)
    private String activityType;
    
    @ExcelProperty("活动地点")
    @ColumnWidth(20)
    private String location;
    
    @ExcelProperty("开始时间")
    @ColumnWidth(20)
    private LocalDateTime startTime;
    
    @ExcelProperty("结束时间")
    @ColumnWidth(20)
    private LocalDateTime endTime;
    
    @ExcelProperty("是否取消")
    private Integer isCancelled;
    
    @ExcelProperty("组织者ID")
    private Long organizerId;
    
    @TableField(fill = FieldFill.INSERT)
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private LocalDateTime updateTime;
    
    // 计算活动状态的方法
    public String getActivityStatus() {
        if (isCancelled != null && isCancelled == 1) {
            return "已取消";
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && endTime != null) {
            if (now.isBefore(startTime)) {
                return "未开始";
            } else if (now.isAfter(endTime)) {
                return "已结束";
            } else {
                return "进行中";
            }
        }
        return "未知";
    }
}
