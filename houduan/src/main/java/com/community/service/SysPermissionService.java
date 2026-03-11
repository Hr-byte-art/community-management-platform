package com.community.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.SysPermission;
import com.community.mapper.SysPermissionMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Service
public class SysPermissionService extends ServiceImpl<SysPermissionMapper, SysPermission> {

    @Cacheable(value = "sysPermission", key = "#roleCode")
    public List<String> listPermissionCodesByRole(String roleCode) {
        if (StrUtil.isBlank(roleCode)) {
            return Collections.emptyList();
        }
        try {
            return baseMapper.selectPermissionCodesByRole(roleCode);
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean hasPermission(String roleCode, String permissionCode, boolean defaultValue) {
        List<String> permissionCodes = listPermissionCodesByRole(roleCode);
        if (permissionCodes == null) {
            return defaultValue;
        }
        return permissionCodes.contains(permissionCode);
    }

    public boolean hasAnyPermission(String roleCode, List<String> requiredPermissions, boolean defaultValue) {
        List<String> permissionCodes = listPermissionCodesByRole(roleCode);
        if (permissionCodes == null) {
            return defaultValue;
        }
        return requiredPermissions.stream().anyMatch(permissionCodes::contains);
    }

    @Override
    @CacheEvict(value = {"sysPermission"}, allEntries = true)
    public boolean save(SysPermission entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = {"sysPermission"}, allEntries = true)
    public boolean updateById(SysPermission entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = {"sysPermission"}, allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
}
