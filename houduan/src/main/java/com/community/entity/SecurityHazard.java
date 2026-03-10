package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("security_hazard")
public class SecurityHazard {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String hazardType;
    private String location;
    private String images;
    private Long reporterId;
    private Long handlerId;
    private Integer status;
    private String handleResult;
    private LocalDateTime handleTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
