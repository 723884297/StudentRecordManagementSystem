package com.student.archive.service;

import com.student.archive.common.PageResult;
import com.student.archive.entity.Student;
import com.student.archive.entity.StudentDetail;

public interface StudentService {

    Student getById(Long pkStudent);

    Student getByStudentNo(String studentNo);

    StudentDetail getDetail(Long studentId);

    PageResult<Student> getList(int pageNum, int pageSize, String studentNo, String name,
                                Long collegeId, Long majorId, Long classId, String status);

    void add(Student student, StudentDetail detail);

    void update(Student student, StudentDetail detail);

    void delete(Long pkStudent);

    void updateStatus(Long pkStudent, String status);
}
