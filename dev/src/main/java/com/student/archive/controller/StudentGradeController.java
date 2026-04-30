package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.StudentGrade;
import com.student.archive.service.StudentGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
public class StudentGradeController {

    private final StudentGradeService studentGradeService;

    @GetMapping("/{id}")
    public Result<StudentGrade> getById(@PathVariable("id") Long id) {
        return Result.success(studentGradeService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<StudentGrade>> getByStudentId(@PathVariable("studentId") Long studentId) {
        return Result.success(studentGradeService.getByStudentId(studentId));
    }

    @GetMapping("/list")
    public Result<PageResult<StudentGrade>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "20") int pageSize,
                                                  @RequestParam(required = false) Long studentId,
                                                  @RequestParam(required = false) String semester,
                                                  @RequestParam(required = false) String courseName) {
        return Result.success(studentGradeService.getList(pageNum, pageSize, studentId, semester, courseName));
    }

    @PostMapping
    public Result<Void> add(@RequestBody StudentGrade grade) {
        studentGradeService.add(grade);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchAdd(@RequestBody List<StudentGrade> grades) {
        studentGradeService.batchAdd(grades);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody StudentGrade grade) {
        studentGradeService.update(grade);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        studentGradeService.delete(id);
        return Result.success();
    }
}
