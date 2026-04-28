package com.student.archive.service;

import com.student.archive.entity.College;

import java.util.List;

public interface CollegeService {

    College getById(Long pkCollege);

    List<College> getAll();

    List<College> getList(String collegeName);

    void add(College college);

    void update(College college);

    void delete(Long pkCollege);
}
