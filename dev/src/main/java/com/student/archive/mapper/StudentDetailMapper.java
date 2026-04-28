package com.student.archive.mapper;

import com.student.archive.entity.StudentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentDetailMapper {

    StudentDetail selectByStudentId(@Param("studentId") Long studentId);

    int insert(StudentDetail detail);

    int update(StudentDetail detail);

    int deleteByStudentId(@Param("studentId") Long studentId);
}
