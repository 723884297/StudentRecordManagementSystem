package com.student.archive.mapper;

import com.student.archive.entity.College;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollegeMapper {

    College selectById(@Param("pkCollege") Long pkCollege);

    List<College> selectAll();

    List<College> selectList(@Param("collegeName") String collegeName);

    int insert(College college);

    int update(College college);

    int deleteById(@Param("pkCollege") Long pkCollege);
}
