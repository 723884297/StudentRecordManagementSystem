package com.student.archive.mapper;

import com.student.archive.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassInfoMapper {

    ClassInfo selectById(@Param("pkClass") Long pkClass);

    List<ClassInfo> selectAll();

    List<ClassInfo> selectByMajorId(@Param("majorId") Long majorId);

    List<ClassInfo> selectList(@Param("className") String className, @Param("majorId") Long majorId, @Param("grade") String grade);

    int insert(ClassInfo classInfo);

    int update(ClassInfo classInfo);

    int deleteById(@Param("pkClass") Long pkClass);
}
