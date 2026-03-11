package com.community.controller;

import com.community.annotation.Log;
import com.community.annotation.Auth;
import com.community.common.Result;
import com.community.entity.Resident;
import com.community.entity.FamilyRelation;
import com.community.service.ResidentService;
import com.community.service.FamilyRelationService;
import com.community.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/resident")
public class ResidentController {
    @Autowired
    private ResidentService residentService;
    @Autowired
    private FamilyRelationService familyRelationService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String name, String buildingNo, Integer status) {
        return Result.success(residentService.pageQuery(pageNum, pageSize, name, buildingNo, status));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(residentService.getById(id));
    }

    @Auth(permissions = {"btn.resident.add"})
    @Log("新增居民")
    @PostMapping
    public Result<?> add(@RequestBody Resident resident) {
        // 验证身份证号格式
        if (!ValidationUtil.isValidIdCard(resident.getIdCard())) {
            return Result.error("身份证号格式不正确");
        }
        // 验证手机号格式
        if (!ValidationUtil.isValidPhone(resident.getPhone())) {
            return Result.error("手机号格式不正确");
        }
        // 检查身份证号是否已存在
        if (residentService.existsByIdCard(resident.getIdCard(), null)) {
            return Result.error("身份证号已存在");
        }
        // 检查手机号是否已存在
        if (residentService.existsByPhone(resident.getPhone(), null)) {
            return Result.error("手机号已存在");
        }
        residentService.save(resident);
        return Result.success();
    }

    @Auth(permissions = {"btn.resident.edit"})
    @Log("更新居民信息")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Resident resident) {
        // 验证身份证号格式
        if (!ValidationUtil.isValidIdCard(resident.getIdCard())) {
            return Result.error("身份证号格式不正确");
        }
        // 验证手机号格式
        if (!ValidationUtil.isValidPhone(resident.getPhone())) {
            return Result.error("手机号格式不正确");
        }
        // 检查身份证号是否已存在（排除当前记录）
        if (residentService.existsByIdCard(resident.getIdCard(), id)) {
            return Result.error("身份证号已存在");
        }
        // 检查手机号是否已存在（排除当前记录）
        if (residentService.existsByPhone(resident.getPhone(), id)) {
            return Result.error("手机号已存在");
        }
        resident.setId(id);
        residentService.updateById(resident);
        return Result.success();
    }

    @Auth(permissions = {"btn.resident.delete"})
    @Log("删除居民")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        residentService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/family")
    public Result<?> getFamilyRelations(@PathVariable Long id) {
        List<FamilyRelation> relations = familyRelationService.findByResidentId(id);
        return Result.success(relations);
    }

    @Auth(permissions = {"btn.resident.family.add"})
    @Log("添加家庭关系")
    @PostMapping("/family")
    public Result<?> addFamilyRelation(@RequestBody FamilyRelation relation) {
        familyRelationService.save(relation);
        return Result.success();
    }

    @Auth(permissions = {"btn.resident.family.delete"})
    @Log("删除家庭关系")
    @DeleteMapping("/family/{id}")
    public Result<?> deleteFamilyRelation(@PathVariable Long id) {
        familyRelationService.removeById(id);
        return Result.success();
    }
}
