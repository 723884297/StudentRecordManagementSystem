package com.student.archive.mapper;

import com.student.archive.entity.StudentAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentAwardMapper {

    StudentAward selectById(@Param("pkAward") Long pkAward);

    List<StudentAward> selectByStudentId(@Param("studentId") Long studentId);

    List<StudentAward> selectList(@Param("studentId") Long studentId,
                                   @Param("awardLevel") String awardLevel,
                                   @Param("awardName") String awardName);

    int insert(StudentAward award);

    int batchInsert(@Param("list") List<StudentAward> list);

    int update(StudentAward award);

    int deleteById(@Param("pkAward") Long pkAward);

    int deleteByStudentId(@Param("studentId") Long studentId);
}
