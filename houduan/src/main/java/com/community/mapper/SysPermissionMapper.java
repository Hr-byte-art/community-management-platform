package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    @Select("SELECT sp.permission_code " +
            "FROM sys_permission sp " +
            "INNER JOIN sys_role_permission srp ON sp.permission_code = srp.permission_code " +
            "WHERE srp.role_code = #{roleCode} AND sp.status = 1 " +
            "ORDER BY sp.sort_no ASC, sp.id ASC")
    List<String> selectPermissionCodesByRole(@Param("roleCode") String roleCode);
}
