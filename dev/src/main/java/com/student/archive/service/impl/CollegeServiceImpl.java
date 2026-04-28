package com.student.archive.service.impl;

import com.student.archive.entity.College;
import com.student.archive.mapper.CollegeMapper;
import com.student.archive.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeServiceImpl implements CollegeService {

    private final CollegeMapper collegeMapper;

    @Override
    public College getById(Long pkCollege) {
        return collegeMapper.selectById(pkCollege);
    }

    @Override
    public List<College> getAll() {
        return collegeMapper.selectAll();
    }

    @Override
    public List<College> getList(String collegeName) {
        return collegeMapper.selectList(collegeName);
    }

    @Override
    @Transactional
    public void add(College college) {
        collegeMapper.insert(college);
    }

    @Override
    @Transactional
    public void update(College college) {
        collegeMapper.update(college);
    }

    @Override
    @Transactional
    public void delete(Long pkCollege) {
        collegeMapper.deleteById(pkCollege);
    }
}
