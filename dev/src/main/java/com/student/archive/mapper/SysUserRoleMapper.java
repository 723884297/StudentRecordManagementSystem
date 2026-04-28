package com.student.archive.mapper;

import com.student.archive.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper {

    List<SysUserRole> selectByUserId(@Param("userId") Long userId);

    int insert(SysUserRole userRole);

    int deleteByUserId(@Param("userId") Long userId);

    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    int batchInsert(@Param("list") List<SysUserRole> list);
}
