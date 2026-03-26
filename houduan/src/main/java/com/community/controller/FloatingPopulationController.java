package com.community.controller;

import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.common.Result;
import com.community.common.Constants;
import com.community.entity.FloatingPopulation;
import com.community.service.FloatingPopulationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/floating")
public class FloatingPopulationController {
    @Autowired
    private FloatingPopulationService floatingPopulationService;

    @Auth(permissions = {"menu.floating"})
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String name, Integer status) {
        return Result.success(floatingPopulationService.pageQuery(pageNum, pageSize, name, status));
    }

    @Auth(permissions = {"menu.floating"})
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(floatingPopulationService.getById(id));
    }

    @Log("登记流动人口")
    @Auth(permissions = {"btn.floating.add"})
    @PostMapping
    public Result<?> add(@RequestBody FloatingPopulation population) {
        floatingPopulationService.save(population);
        return Result.success();
    }

    @Log("编辑流动人口信息")
    @Auth(permissions = {"btn.floating.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FloatingPopulation population) {
        population.setId(id);
        floatingPopulationService.updateById(population);
        return Result.success();
    }

    @Log("删除流动人口信息")
    @Auth(permissions = {"btn.floating.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        floatingPopulationService.removeById(id);
        return Result.success();
    }
}
