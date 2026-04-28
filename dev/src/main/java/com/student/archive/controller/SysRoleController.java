package com.student.archive.controller;

import com.student.archive.common.Result;
import com.student.archive.entity.SysRole;
import com.student.archive.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/{id}")
    public Result<SysRole> getById(@PathVariable("id") Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.getAll());
    }

    @GetMapping("/user/{userId}")
    public Result<List<SysRole>> getByUserId(@PathVariable("userId") Long userId) {
        return Result.success(sysRoleService.getRolesByUserId(userId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleService.add(role);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.update(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        sysRoleService.delete(id);
        return Result.success();
    }

    @PostMapping("/assign/{userId}")
    public Result<Void> assignRoles(@PathVariable("userId") Long userId, @RequestBody List<Long> roleIds) {
        sysRoleService.assignRoles(userId, roleIds);
        return Result.success();
    }
}
