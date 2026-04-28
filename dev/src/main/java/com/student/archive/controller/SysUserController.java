package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.SysUser;
import com.student.archive.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable("id") Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String username,
                                            @RequestParam(required = false) String realName,
                                            @RequestParam(required = false) Integer status) {
        return Result.success(sysUserService.getList(pageNum, pageSize, username, realName, status));
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        sysUserService.add(user);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysUser user) {
        sysUserService.update(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/resetPassword")
    public Result<Void> resetPassword(@PathVariable("id") Long id, @RequestParam String newPassword) {
        sysUserService.resetPassword(id, newPassword);
        return Result.success();
    }
}
