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
}
