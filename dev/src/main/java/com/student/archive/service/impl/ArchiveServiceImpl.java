package com.student.archive.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.student.archive.common.PageResult;
import com.student.archive.entity.ArchiveCategory;
import com.student.archive.entity.ArchiveFile;
import com.student.archive.mapper.ArchiveCategoryMapper;
import com.student.archive.mapper.ArchiveFileMapper;
import com.student.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveCategoryMapper archiveCategoryMapper;
    private final ArchiveFileMapper archiveFileMapper;

    @Override
    public List<ArchiveCategory> getAllCategories() {
        return archiveCategoryMapper.selectAll();
    }

    @Override
    @Transactional
    public void addCategory(ArchiveCategory category) {
        archiveCategoryMapper.insert(category);
    }

    @Override
    @Transactional
    public void updateCategory(ArchiveCategory category) {
        archiveCategoryMapper.update(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long pkCategory) {
        archiveCategoryMapper.deleteById(pkCategory);
    }

    @Override
    public ArchiveFile getFileById(Long pkArchiveFile) {
        return archiveFileMapper.selectById(pkArchiveFile);
    }

    @Override
    public List<ArchiveFile> getFilesByStudentId(Long studentId) {
        return archiveFileMapper.selectByStudentId(studentId);
    }

    @Override
    public PageResult<ArchiveFile> getFileList(int pageNum, int pageSize, Long studentId, Long categoryId, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        List<ArchiveFile> list = archiveFileMapper.selectList(studentId, categoryId, status);
        PageInfo<ArchiveFile> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    @Override
    @Transactional
    public void addFile(ArchiveFile file) {
        archiveFileMapper.insert(file);
    }

    @Override
    @Transactional
    public void deleteFile(Long pkArchiveFile) {
        archiveFileMapper.deleteById(pkArchiveFile);
    }

    @Override
    @Transactional
    public void auditFile(Long pkArchiveFile, Integer status, Long auditUserId, String auditRemark) {
        archiveFileMapper.updateStatus(pkArchiveFile, status, auditUserId, auditRemark);
    }
}
