package com.student.archive.mapper;

import com.student.archive.entity.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MajorMapper {

    Major selectById(@Param("pkMajor") Long pkMajor);

    List<Major> selectAll();

    List<Major> selectByCollegeId(@Param("collegeId") Long collegeId);

    List<Major> selectList(@Param("majorName") String majorName, @Param("collegeId") Long collegeId);

    int insert(Major major);

    int update(Major major);

    int deleteById(@Param("pkMajor") Long pkMajor);
}
