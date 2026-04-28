<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="toolbar-filters">
        <select v-model="filters.status" class="form-select toolbar-input">
          <option value="">全部状态</option>
          <option :value="0">待审核</option>
          <option :value="1">已通过</option>
          <option :value="2">已驳回</option>
        </select>
        <button class="btn btn-subtle btn-sm" @click="loadData">查询</button>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>学生</th>
            <th>材料分类</th>
            <th>文件名</th>
            <th>大小</th>
            <th>状态</th>
            <th>上传时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkArchiveFile">
            <td class="text-small-medium" style="color: var(--text-primary)">{{ item.studentName }}</td>
            <td>{{ item.categoryName }}</td>
            <td>{{ item.fileName }}</td>
            <td>{{ formatSize(item.fileSize) }}</td>
            <td>
              <span :class="['badge', auditBadge(item.status)]">{{ auditLabel(item.status) }}</span>
            </td>
            <td class="text-caption">{{ item.uploadTime }}</td>
            <td>
              <div class="flex gap-4">
                <button v-if="item.status === 0" class="btn btn-primary btn-sm" @click="handleAudit(item, 1)">通过</button>
                <button v-if="item.status === 0" class="btn btn-danger btn-sm" @click="handleAudit(item, 2)">驳回</button>
                <button class="btn btn-subtle btn-sm" @click="handleDelete(item)">删除</button>
              </div>
            </td>
          </tr>
          <tr v-if="list.length === 0">
            <td colspan="7" class="empty-cell">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const list = ref<any[]>([])
const filters = reactive({ status: '' })

function auditBadge(s: number) {
  return [, 'badge-success', 'badge-danger'][s] || 'badge-warning'
}
function auditLabel(s: number) {
  return ['待审核', '已通过', '已驳回'][s] || '未知'
}
function formatSize(bytes: number) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + 'KB'
  return (bytes / 1048576).toFixed(1) + 'MB'
}

function loadData() {
  list.value = [
    { pkArchiveFile: 1, studentName: '张三', categoryName: '入学材料', fileName: '录取通知书.pdf', fileSize: 524288, status: 0, uploadTime: '2024-09-01 10:30' },
    { pkArchiveFile: 2, studentName: '李四', categoryName: '身份证明', fileName: '身份证正反面.jpg', fileSize: 1048576, status: 1, uploadTime: '2024-09-01 09:15' },
    { pkArchiveFile: 3, studentName: '王五', categoryName: '成绩单', fileName: '2023秋季成绩单.pdf', fileSize: 256000, status: 2, uploadTime: '2024-08-20 14:00' },
  ]
}

function handleAudit(item: any, status: number) {
  item.status = status
}
function handleDelete(item: any) {
  if (confirm(`确定删除文件 ${item.fileName}？`)) loadData()
}

onMounted(loadData)
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-filters { display: flex; gap: 8px; align-items: center; }
.toolbar-input { width: 160px; padding: 7px 12px; font-size: 13px; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
</style>
