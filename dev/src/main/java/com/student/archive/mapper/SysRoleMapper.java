package com.student.archive.mapper;

import com.student.archive.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    SysRole selectById(@Param("pkRole") Long pkRole);

    List<SysRole> selectAll();

    List<SysRole> selectByUserId(@Param("userId") Long userId);

    int insert(SysRole role);

    int update(SysRole role);

    int deleteById(@Param("pkRole") Long pkRole);
}
