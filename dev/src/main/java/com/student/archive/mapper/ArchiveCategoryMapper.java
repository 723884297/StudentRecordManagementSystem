package com.student.archive.mapper;

import com.student.archive.entity.ArchiveCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArchiveCategoryMapper {

    ArchiveCategory selectById(@Param("pkCategory") Long pkCategory);

    List<ArchiveCategory> selectAll();

    int insert(ArchiveCategory category);

    int update(ArchiveCategory category);

    int deleteById(@Param("pkCategory") Long pkCategory);
}
