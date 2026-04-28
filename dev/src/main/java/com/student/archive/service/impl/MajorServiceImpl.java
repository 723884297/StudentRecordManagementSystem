package com.student.archive.service.impl;

import com.student.archive.entity.Major;
import com.student.archive.mapper.MajorMapper;
import com.student.archive.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {

    private final MajorMapper majorMapper;

    @Override
    public Major getById(Long pkMajor) {
        return majorMapper.selectById(pkMajor);
    }

    @Override
    public List<Major> getAll() {
        return majorMapper.selectAll();
    }

    @Override
    public List<Major> getByCollegeId(Long collegeId) {
        return majorMapper.selectByCollegeId(collegeId);
    }

    @Override
    public List<Major> getList(String majorName, Long collegeId) {
        return majorMapper.selectList(majorName, collegeId);
    }

    @Override
    @Transactional
    public void add(Major major) {
        majorMapper.insert(major);
    }

    @Override
    @Transactional
    public void update(Major major) {
        majorMapper.update(major);
    }

    @Override
    @Transactional
    public void delete(Long pkMajor) {
        majorMapper.deleteById(pkMajor);
    }
}
