package com.student.archive.controller;

import com.student.archive.common.PageResult;
import com.student.archive.common.Result;
import com.student.archive.entity.Student;
import com.student.archive.entity.StudentDetail;
import com.student.archive.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable("id") Long id) {
        return Result.success(studentService.getById(id));
    }

    @GetMapping("/no/{studentNo}")
    public Result<Student> getByStudentNo(@PathVariable("studentNo") String studentNo) {
        return Result.success(studentService.getByStudentNo(studentNo));
    }

    @GetMapping("/{id}/detail")
    public Result<StudentDetail> getDetail(@PathVariable("id") Long id) {
        return Result.success(studentService.getDetail(id));
    }

    @GetMapping("/list")
    public Result<PageResult<Student>> list(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String studentNo,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) Long collegeId,
                                            @RequestParam(required = false) Long majorId,
                                            @RequestParam(required = false) Long classId,
                                            @RequestParam(required = false) String status) {
        return Result.success(studentService.getList(pageNum, pageSize, studentNo, name, collegeId, majorId, classId, status));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Student student) {
        studentService.add(student, null);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Student student) {
        studentService.update(student, null);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        studentService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @RequestParam String status) {
        studentService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/detail")
    public Result<Void> updateDetail(@PathVariable("id") Long id, @RequestBody StudentDetail detail) {
        detail.setStudentId(id);
        Student student = new Student();
        student.setPkStudent(id);
        studentService.update(student, detail);
        return Result.success();
    }
}
