package com.student.archive.controller;

import com.student.archive.common.Result;
import com.student.archive.entity.Major;
import com.student.archive.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    @GetMapping("/{id}")
    public Result<Major> getById(@PathVariable("id") Long id) {
        return Result.success(majorService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<Major>> list(@RequestParam(required = false) String majorName,
                                    @RequestParam(required = false) Long collegeId) {
        return Result.success(majorService.getList(majorName, collegeId));
    }

    @GetMapping("/college/{collegeId}")
    public Result<List<Major>> getByCollegeId(@PathVariable("collegeId") Long collegeId) {
        return Result.success(majorService.getByCollegeId(collegeId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Major major) {
        majorService.add(major);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Major major) {
        majorService.update(major);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        majorService.delete(id);
        return Result.success();
    }
}
