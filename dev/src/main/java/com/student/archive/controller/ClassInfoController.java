package com.student.archive.controller;

import com.student.archive.common.Result;
import com.student.archive.entity.ClassInfo;
import com.student.archive.service.ClassInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassInfoController {

    private final ClassInfoService classInfoService;

    @GetMapping("/{id}")
    public Result<ClassInfo> getById(@PathVariable("id") Long id) {
        return Result.success(classInfoService.getById(id));
    }

    @GetMapping("/list")
    public Result<List<ClassInfo>> list(@RequestParam(required = false) String className,
                                        @RequestParam(required = false) Long majorId,
                                        @RequestParam(required = false) String grade) {
        return Result.success(classInfoService.getList(className, majorId, grade));
    }

    @GetMapping("/major/{majorId}")
    public Result<List<ClassInfo>> getByMajorId(@PathVariable("majorId") Long majorId) {
        return Result.success(classInfoService.getByMajorId(majorId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody ClassInfo classInfo) {
        classInfoService.add(classInfo);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody ClassInfo classInfo) {
        classInfoService.update(classInfo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        classInfoService.delete(id);
        return Result.success();
    }
}
