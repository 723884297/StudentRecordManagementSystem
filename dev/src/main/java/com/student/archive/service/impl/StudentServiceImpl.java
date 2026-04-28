package com.student.archive.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.archive.common.BusinessException;
import com.student.archive.common.PageResult;
import com.student.archive.entity.Student;
import com.student.archive.entity.StudentDetail;
import com.student.archive.mapper.StudentDetailMapper;
import com.student.archive.mapper.StudentMapper;
import com.student.archive.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final StudentDetailMapper studentDetailMapper;

    @Override
    public Student getById(Long pkStudent) {
        return studentMapper.selectById(pkStudent);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        return studentMapper.selectByStudentNo(studentNo);
    }

    @Override
    public StudentDetail getDetail(Long studentId) {
        return studentDetailMapper.selectByStudentId(studentId);
    }

    @Override
    public PageResult<Student> getList(int pageNum, int pageSize, String studentNo, String name,
                                       Long collegeId, Long majorId, Long classId, String status) {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> list = studentMapper.selectList(studentNo, name, collegeId, majorId, classId, status);
        PageInfo<Student> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    @Override
    @Transactional
    public void add(Student student, StudentDetail detail) {
        Student existing = studentMapper.selectByStudentNo(student.getStudentNo());
        if (existing != null) {
            throw new BusinessException("学号已存在");
        }
        studentMapper.insert(student);
        if (detail != null) {
            detail.setStudentId(student.getPkStudent());
            studentDetailMapper.insert(detail);
        }
    }

    @Override
    @Transactional
    public void update(Student student, StudentDetail detail) {
        studentMapper.update(student);
        if (detail != null) {
            detail.setStudentId(student.getPkStudent());
            StudentDetail existing = studentDetailMapper.selectByStudentId(student.getPkStudent());
            if (existing != null) {
                detail.setPkStudentDetail(existing.getPkStudentDetail());
                studentDetailMapper.update(detail);
            } else {
                studentDetailMapper.insert(detail);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long pkStudent) {
        studentDetailMapper.deleteByStudentId(pkStudent);
        studentMapper.deleteById(pkStudent);
    }

    @Override
    @Transactional
    public void updateStatus(Long pkStudent, String status) {
        studentMapper.updateStatus(pkStudent, status);
    }
}
