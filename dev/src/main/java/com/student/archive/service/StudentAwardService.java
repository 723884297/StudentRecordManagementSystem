package com.student.archive.service;

import com.student.archive.common.PageResult;
import com.student.archive.entity.StudentAward;

import java.util.List;

public interface StudentAwardService {

    StudentAward getById(Long pkAward);

    List<StudentAward> getByStudentId(Long studentId);

    PageResult<StudentAward> getList(int pageNum, int pageSize, Long studentId, String awardLevel, String awardName, String type);

    void add(StudentAward award);

    void batchAdd(List<StudentAward> awards);

    void update(StudentAward award);

    void delete(Long pkAward);
}
