package com.student.archive.service;

import com.student.archive.entity.ClassInfo;

import java.util.List;

public interface ClassInfoService {

    ClassInfo getById(Long pkClass);

    List<ClassInfo> getAll();

    List<ClassInfo> getByMajorId(Long majorId);

    List<ClassInfo> getList(String className, Long majorId, String grade);

    void add(ClassInfo classInfo);

    void update(ClassInfo classInfo);

    void delete(Long pkClass);
}
