package com.student.archive.controller;

import com.student.archive.common.Result;
import com.student.archive.entity.SysOperationLog;
import com.student.archive.mapper.ArchiveFileMapper;
import com.student.archive.mapper.StudentMapper;
import com.student.archive.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计接口
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final StudentMapper studentMapper;
    private final ArchiveFileMapper archiveFileMapper;
    private final SysOperationLogMapper sysOperationLogMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> data = new HashMap<>();

        // 学生统计
        int studentTotal = studentMapper.countTotal();
        List<Map<String, Object>> statusList = studentMapper.countByStatus();
        data.put("studentTotal", studentTotal);
        data.put("statusDistribution", statusList);

        // 档案统计
        int archiveTotal = archiveFileMapper.countTotal();
        int pendingAudit = archiveFileMapper.countByStatus(0);  // 0=待审核
        data.put("archiveTotal", archiveTotal);
        data.put("pendingAudit", pendingAudit);

        // 近期操作日志（最近10条）
        List<SysOperationLog> recentLogs = sysOperationLogMapper.selectRecent(10);
        data.put("recentActivities", recentLogs);

        return Result.success(data);
    }
}
