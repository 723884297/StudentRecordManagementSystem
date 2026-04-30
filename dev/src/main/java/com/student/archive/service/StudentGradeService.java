package com.student.archive.service;

import com.student.archive.common.PageResult;
import com.student.archive.entity.StudentGrade;

import java.util.List;

public interface StudentGradeService {

    StudentGrade getById(Long pkGrade);

    List<StudentGrade> getByStudentId(Long studentId);

    PageResult<StudentGrade> getList(int pageNum, int pageSize, Long studentId, String semester, String courseName);

    void add(StudentGrade grade);

    void batchAdd(List<StudentGrade> grades);

    void update(StudentGrade grade);

    void delete(Long pkGrade);
}
