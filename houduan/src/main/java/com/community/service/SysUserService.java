package com.community.service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.SysUser;
import com.community.mapper.SysUserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.StrUtil;
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {
    
    @Cacheable(value = "sysUser", key = "#username")
    public SysUser findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }
    
    public Page<SysUser> pageQuery(Integer pageNum, Integer pageSize, String username, String realName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(username), SysUser::getUsername, username);
        wrapper.like(StrUtil.isNotBlank(realName), SysUser::getRealName, realName);
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
    
    /**
     * 检查手机号是否已存在
     */
    public boolean existsByPhone(String phone, Long excludeId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        if (excludeId != null) {
            wrapper.ne(SysUser::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
    
    @Cacheable(value = "sysUser", key = "#id")
    public SysUser getUserById(Long id) {
        return super.getById(id);
    }
    
    @Override
    @CacheEvict(value = {"sysUser"}, allEntries = true)
    public boolean save(SysUser entity) {
        return super.save(entity);
    }
    
    @Override
    @CacheEvict(value = {"sysUser"}, allEntries = true)
    public boolean updateById(SysUser entity) {
        return super.updateById(entity);
    }
    
    @Override
    @CacheEvict(value = {"sysUser"}, allEntries = true)
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}