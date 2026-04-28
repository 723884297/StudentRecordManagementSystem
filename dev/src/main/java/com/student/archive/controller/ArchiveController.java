package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.ArchiveCategory;
import com.student.archive.entity.ArchiveFile;
import com.student.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;

    // ===== 分类管理 =====

    @GetMapping("/category/list")
    public Result<List<ArchiveCategory>> categoryList() {
        return Result.success(archiveService.getAllCategories());
    }

    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody ArchiveCategory category) {
        archiveService.addCategory(category);
        return Result.success();
    }

    @PutMapping("/category")
    public Result<Void> updateCategory(@RequestBody ArchiveCategory category) {
        archiveService.updateCategory(category);
        return Result.success();
    }

    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        archiveService.deleteCategory(id);
        return Result.success();
    }

    // ===== 档案文件管理 =====

    @GetMapping("/file/{id}")
    public Result<ArchiveFile> getFileById(@PathVariable("id") Long id) {
        return Result.success(archiveService.getFileById(id));
    }

    @GetMapping("/file/student/{studentId}")
    public Result<List<ArchiveFile>> getFilesByStudentId(@PathVariable("studentId") Long studentId) {
        return Result.success(archiveService.getFilesByStudentId(studentId));
    }

    @GetMapping("/file/list")
    public Result<PageResult<ArchiveFile>> fileList(@RequestParam(defaultValue = "1") int pageNum,
                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                    @RequestParam(required = false) Long studentId,
                                                    @RequestParam(required = false) Long categoryId,
                                                    @RequestParam(required = false) Integer status) {
        return Result.success(archiveService.getFileList(pageNum, pageSize, studentId, categoryId, status));
    }

    @PostMapping("/file")
    public Result<Void> addFile(@RequestBody ArchiveFile file) {
        archiveService.addFile(file);
        return Result.success();
    }

    @DeleteMapping("/file/{id}")
    public Result<Void> deleteFile(@PathVariable("id") Long id) {
        archiveService.deleteFile(id);
        return Result.success();
    }

    @PutMapping("/file/{id}/audit")
    public Result<Void> auditFile(@PathVariable("id") Long id,
                                  @RequestParam Integer status,
                                  @RequestParam Long auditUserId,
                                  @RequestParam(required = false) String auditRemark) {
        archiveService.auditFile(id, status, auditUserId, auditRemark);
        return Result.success();
    }
}
