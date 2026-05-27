package com.student.archive.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.archive.common.PageResult;
import com.student.archive.entity.StudentAward;
import com.student.archive.mapper.StudentAwardMapper;
import com.student.archive.service.StudentAwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAwardServiceImpl implements StudentAwardService {

    private final StudentAwardMapper studentAwardMapper;

    @Override
    public StudentAward getById(Long pkAward) {
        return studentAwardMapper.selectById(pkAward);
    }

    @Override
    public List<StudentAward> getByStudentId(Long studentId) {
        return studentAwardMapper.selectByStudentId(studentId);
    }

    @Override
    public PageResult<StudentAward> getList(int pageNum, int pageSize, Long studentId, String awardLevel, String awardName, String type) {
        PageHelper.startPage(pageNum, pageSize);
        List<StudentAward> list = studentAwardMapper.selectList(studentId, awardLevel, awardName, type);
        PageInfo<StudentAward> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    @Override
    @Transactional
    public void add(StudentAward award) {
        studentAwardMapper.insert(award);
    }

    @Override
    @Transactional
    public void batchAdd(List<StudentAward> awards) {
        if (awards != null && !awards.isEmpty()) {
            studentAwardMapper.batchInsert(awards);
        }
    }

    @Override
    @Transactional
    public void update(StudentAward award) {
        studentAwardMapper.update(award);
    }

    @Override
    @Transactional
    public void delete(Long pkAward) {
        studentAwardMapper.deleteById(pkAward);
    }
}
