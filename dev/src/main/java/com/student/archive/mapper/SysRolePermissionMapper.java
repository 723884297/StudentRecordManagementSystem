package com.student.archive.mapper;

import com.student.archive.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRolePermissionMapper {

    List<SysRolePermission> selectByRoleId(@Param("roleId") Long roleId);

    int insert(SysRolePermission rolePermission);

    int deleteByRoleId(@Param("roleId") Long roleId);

    int batchInsert(@Param("list") List<SysRolePermission> list);
}
