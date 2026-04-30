package com.student.archive.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.archive.common.PageResult;
import com.student.archive.entity.StudentGrade;
import com.student.archive.mapper.StudentGradeMapper;
import com.student.archive.service.StudentGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentGradeServiceImpl implements StudentGradeService {

    private final StudentGradeMapper studentGradeMapper;

    @Override
    public StudentGrade getById(Long pkGrade) {
        return studentGradeMapper.selectById(pkGrade);
    }

    @Override
    public List<StudentGrade> getByStudentId(Long studentId) {
        return studentGradeMapper.selectByStudentId(studentId);
    }

    @Override
    public PageResult<StudentGrade> getList(int pageNum, int pageSize, Long studentId, String semester, String courseName) {
        PageHelper.startPage(pageNum, pageSize);
        List<StudentGrade> list = studentGradeMapper.selectList(studentId, semester, courseName);
        PageInfo<StudentGrade> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    @Override
    @Transactional
    public void add(StudentGrade grade) {
        studentGradeMapper.insert(grade);
    }

    @Override
    @Transactional
    public void batchAdd(List<StudentGrade> grades) {
        if (grades != null && !grades.isEmpty()) {
            studentGradeMapper.batchInsert(grades);
        }
    }

    @Override
    @Transactional
    public void update(StudentGrade grade) {
        studentGradeMapper.update(grade);
    }

    @Override
    @Transactional
    public void delete(Long pkGrade) {
        studentGradeMapper.deleteById(pkGrade);
    }
}
