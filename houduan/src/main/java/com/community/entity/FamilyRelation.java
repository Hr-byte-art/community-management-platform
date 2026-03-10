package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("family_relation")
public class FamilyRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long residentId;
    private Long relatedResidentId;
    private String relation;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
