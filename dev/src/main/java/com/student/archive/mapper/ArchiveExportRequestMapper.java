package com.student.archive.mapper;

import com.student.archive.entity.ArchiveExportRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArchiveExportRequestMapper {

    ArchiveExportRequest selectById(@Param("pkRequest") Long pkRequest);

    List<ArchiveExportRequest> selectByStudentId(@Param("studentId") Long studentId);

    List<ArchiveExportRequest> selectList(@Param("studentId") Long studentId, @Param("status") Integer status);

    int insert(ArchiveExportRequest request);

    int updateAudit(@Param("pkRequest") Long pkRequest, @Param("status") Integer status,
                    @Param("auditUserId") Long auditUserId, @Param("auditRemark") String auditRemark);

    /**
     * 查询学生当前所有有效的已通过申请（未过期）
     */
    List<ArchiveExportRequest> selectValidApproved(@Param("studentId") Long studentId);
}
