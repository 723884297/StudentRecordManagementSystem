package com.student.archive.service.impl;

import com.student.archive.entity.ClassInfo;
import com.student.archive.mapper.ClassInfoMapper;
import com.student.archive.service.ClassInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassInfoServiceImpl implements ClassInfoService {

    private final ClassInfoMapper classInfoMapper;

    @Override
    public ClassInfo getById(Long pkClass) {
        return classInfoMapper.selectById(pkClass);
    }

    @Override
    public List<ClassInfo> getAll() {
        return classInfoMapper.selectAll();
    }

    @Override
    public List<ClassInfo> getByMajorId(Long majorId) {
        return classInfoMapper.selectByMajorId(majorId);
    }

    @Override
    public List<ClassInfo> getList(String className, Long majorId, String grade) {
        return classInfoMapper.selectList(className, majorId, grade);
    }

    @Override
    @Transactional
    public void add(ClassInfo classInfo) {
        classInfoMapper.insert(classInfo);
    }

    @Override
    @Transactional
    public void update(ClassInfo classInfo) {
        classInfoMapper.update(classInfo);
    }

    @Override
    @Transactional
    public void delete(Long pkClass) {
        classInfoMapper.deleteById(pkClass);
    }
}
