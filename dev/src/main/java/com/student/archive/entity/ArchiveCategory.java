package com.student.archive.entity;

import lombok.Data;

/**
 * 档案材料分类表
 */
@Data
public class ArchiveCategory {

    private Long pkCategory;
    private String categoryName;
    private Integer sortOrder;
    private Integer isRequired;
}
