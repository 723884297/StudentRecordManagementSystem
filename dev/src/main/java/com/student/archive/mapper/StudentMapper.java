package com.student.archive.mapper;

import com.student.archive.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {

    Student selectById(@Param("pkStudent") Long pkStudent);

    Student selectByStudentNo(@Param("studentNo") String studentNo);

    Student selectByUserId(@Param("userId") Long userId);

    List<Student> selectList(@Param("studentNo") String studentNo, @Param("name") String name,
                             @Param("collegeId") Long collegeId, @Param("majorId") Long majorId,
                             @Param("classId") Long classId, @Param("status") String status);

    int insert(Student student);

    int update(Student student);

    int deleteById(@Param("pkStudent") Long pkStudent);

    int updateStatus(@Param("pkStudent") Long pkStudent, @Param("status") String status);

    int countTotal();

    List<Map<String, Object>> countByStatus();
}
