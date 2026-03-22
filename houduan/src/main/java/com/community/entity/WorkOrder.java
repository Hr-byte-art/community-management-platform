package com.community.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.*;
import com.community.converter.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("work_order")
public class WorkOrder {
    @TableId(type = IdType.AUTO)
    @ExcelProperty("编号")
    private Long id;
    
    @ExcelProperty("标题")
    @ColumnWidth(20)
    private String title;
    
    @ExcelProperty("内容")
    @ColumnWidth(30)
    private String content;
    
    @ExcelProperty(value = "工单类型", converter = OrderTypeConverter.class)
    private String orderType;
    
    @ExcelIgnore
    private String images;
    
    @ExcelProperty("提交人ID")
    private Long submitterId;
    
    @ExcelProperty("处理人ID")
    private Long handlerId;

    private Long assigneeId;

    private LocalDateTime deadline;

    private Integer isOvertime;

    @TableField(exist = false)
    private Integer evaluated;

    @TableField(exist = false)
    private String assigneeName;
    
    @ExcelProperty(value = "状态", converter = WorkOrderStatusConverter.class)
    private Integer status;
    
    @ExcelProperty(value = "优先级", converter = PriorityConverter.class)
    private Integer priority;
    
    @ExcelProperty("处理结果")
    @ColumnWidth(30)
    private String handleResult;
    
    @ExcelProperty("处理时间")
    @ColumnWidth(20)
    private LocalDateTime handleTime;
    
    @TableField(fill = FieldFill.INSERT)
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private LocalDateTime updateTime;
}
