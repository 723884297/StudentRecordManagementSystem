package com.student.archive.mapper;

import com.student.archive.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUser selectById(@Param("pkUser") Long pkUser);

    SysUser selectByUsername(@Param("username") String username);

    List<SysUser> selectList(@Param("username") String username, @Param("realName") String realName, @Param("status") Integer status);

    int insert(SysUser user);

    int update(SysUser user);

    int deleteById(@Param("pkUser") Long pkUser);

    int updateStatus(@Param("pkUser") Long pkUser, @Param("status") Integer status);
}
