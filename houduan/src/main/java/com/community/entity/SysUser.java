package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.community.common.Constants;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private String role = Constants.Role.USER; // 默认角色为普通用户
    private Integer status = Constants.UserStatus.ENABLED; // 默认启用状态
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
