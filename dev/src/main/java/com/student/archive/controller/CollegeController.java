package com.student.archive.controller;

import com.student.archive.common.Result;
import com.student.archive.entity.College;
import com.student.archive.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping("/{id}")
    public Result<College> getById(@PathVariable("id") Long id) {
        return Result.success(collegeService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<College>> list(@RequestParam(required = false) String collegeName) {
        return Result.success(collegeService.getList(collegeName));
    }

    @GetMapping("/all")
    public Result<List<College>> all() {
        return Result.success(collegeService.getAll());
    }

    @PostMapping
    public Result<Void> add(@RequestBody College college) {
        collegeService.add(college);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody College college) {
        collegeService.update(college);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        collegeService.delete(id);
        return Result.success();
    }
}
