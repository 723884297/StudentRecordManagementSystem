package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.StudentAward;
import com.student.archive.service.StudentAwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/award")
@RequiredArgsConstructor
public class StudentAwardController {

    private final StudentAwardService studentAwardService;

    @GetMapping("/{id}")
    public Result<StudentAward> getById(@PathVariable("id") Long id) {
        return Result.success(studentAwardService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<StudentAward>> getByStudentId(@PathVariable("studentId") Long studentId) {
        return Result.success(studentAwardService.getByStudentId(studentId));
    }

    @GetMapping("/list")
    public Result<PageResult<StudentAward>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "20") int pageSize,
                                                  @RequestParam(required = false) Long studentId,
                                                  @RequestParam(required = false) String awardLevel,
                                                  @RequestParam(required = false) String awardName) {
        return Result.success(studentAwardService.getList(pageNum, pageSize, studentId, awardLevel, awardName));
    }

    @PostMapping
    public Result<Void> add(@RequestBody StudentAward award) {
        studentAwardService.add(award);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchAdd(@RequestBody List<StudentAward> awards) {
        studentAwardService.batchAdd(awards);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody StudentAward award) {
        studentAwardService.update(award);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        studentAwardService.delete(id);
        return Result.success();
    }
}
