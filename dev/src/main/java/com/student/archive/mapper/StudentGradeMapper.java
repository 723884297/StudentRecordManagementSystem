package com.student.archive.mapper;

import com.student.archive.entity.StudentGrade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentGradeMapper {

    StudentGrade selectById(@Param("pkGrade") Long pkGrade);

    List<StudentGrade> selectByStudentId(@Param("studentId") Long studentId);

    List<StudentGrade> selectList(@Param("studentId") Long studentId,
                                   @Param("semester") String semester,
                                   @Param("courseName") String courseName);

    int insert(StudentGrade grade);

    int batchInsert(@Param("list") List<StudentGrade> list);

    int update(StudentGrade grade);

    int deleteById(@Param("pkGrade") Long pkGrade);

    int deleteByStudentId(@Param("studentId") Long studentId);
}
