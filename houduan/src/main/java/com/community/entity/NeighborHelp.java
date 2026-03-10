package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("neighbor_help")
public class NeighborHelp {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String helpType;
    private String category;
    private Long userId;
    private String contactInfo;
    private String images;
    private Integer status;
    private Integer viewCount;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
