package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.SysOperationLog;
import com.student.archive.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 操作日志管理接口
 */
@RestController
@RequestMapping("/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final SysOperationLogMapper sysOperationLogMapper;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/list")
    public Result<PageResult<SysOperationLog>> getList(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {

        int offset = (pageNum - 1) * pageSize;
        List<SysOperationLog> list = sysOperationLogMapper.selectPage(userId, module, operation, offset, pageSize);
        int total = sysOperationLogMapper.countPage(userId, module, operation);

        PageResult<SysOperationLog> pageResult = new PageResult<>(total, pageNum, pageSize, list);
        return Result.success(pageResult);
    }
}
