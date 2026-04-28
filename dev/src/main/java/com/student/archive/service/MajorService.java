package com.student.archive.service;

import com.student.archive.entity.Major;

import java.util.List;

public interface MajorService {

    Major getById(Long pkMajor);

    List<Major> getAll();

    List<Major> getByCollegeId(Long collegeId);

    List<Major> getList(String majorName, Long collegeId);

    void add(Major major);

    void update(Major major);

    void delete(Long pkMajor);
}
