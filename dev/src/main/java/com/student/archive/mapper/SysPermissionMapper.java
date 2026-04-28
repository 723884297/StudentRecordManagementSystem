package com.student.archive.mapper;

import com.student.archive.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysPermissionMapper {

    SysPermission selectById(@Param("pkPermission") Long pkPermission);

    List<SysPermission> selectAll();

    List<SysPermission> selectByRoleId(@Param("roleId") Long roleId);

    List<SysPermission> selectByUserId(@Param("userId") Long userId);

    int insert(SysPermission permission);

    int update(SysPermission permission);

    int deleteById(@Param("pkPermission") Long pkPermission);
}
