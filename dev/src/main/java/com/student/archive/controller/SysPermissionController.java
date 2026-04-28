package com.student.archive.controller;

import com.student.archive.common.Result;
import com.student.archive.entity.SysPermission;
import com.student.archive.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @GetMapping("/list")
    public Result<List<SysPermission>> list() {
        return Result.success(sysPermissionService.getAll());
    }

    @GetMapping("/user/{userId}")
    public Result<List<SysPermission>> getByUserId(@PathVariable("userId") Long userId) {
        return Result.success(sysPermissionService.getByUserId(userId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysPermission permission) {
        sysPermissionService.add(permission);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysPermission permission) {
        sysPermissionService.update(permission);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        sysPermissionService.delete(id);
        return Result.success();
    }

    @PostMapping("/assign/{roleId}")
    public Result<Void> assignPermissions(@PathVariable("roleId") Long roleId, @RequestBody List<Long> permissionIds) {
        sysPermissionService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }
}
