package com.student.archive.mapper;

import com.student.archive.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysOperationLogMapper {

    List<SysOperationLog> selectList(@Param("userId") Long userId, @Param("module") String module,
                                     @Param("operation") String operation);

    int insert(SysOperationLog log);

    List<SysOperationLog> selectRecent(@Param("limit") int limit);

    List<SysOperationLog> selectPage(@Param("userId") Long userId, @Param("module") String module,
                                     @Param("operation") String operation,
                                     @Param("offset") int offset, @Param("pageSize") int pageSize);

    int countPage(@Param("userId") Long userId, @Param("module") String module,
                  @Param("operation") String operation);
}
