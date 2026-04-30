<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="toolbar-filters">
        <select v-model="filters.status" class="form-select toolbar-input" @change="loadData">
          <option value="">全部状态</option>
          <option :value="0">待审核</option>
          <option :value="1">已通过</option>
          <option :value="2">已驳回</option>
        </select>
        <select v-model="filters.categoryId" class="form-select toolbar-input" @change="loadData">
          <option value="">全部分类</option>
          <option v-for="c in categories" :key="c.pkCategory" :value="c.pkCategory">{{ c.categoryName }}</option>
        </select>
        <button class="btn btn-subtle btn-sm" @click="loadData">查询</button>
      </div>
      <div class="toolbar-stats">
        <span class="stat-badge pending">待审核 {{ pendingCount }}</span>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>学生</th>
            <th>材料分类</th>
            <th>文件名</th>
            <th>描述</th>
            <th>大小</th>
            <th>状态</th>
            <th>上传时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkArchiveFile">
            <td class="text-small-medium" style="color: var(--text-primary)">{{ item.studentName || '-' }}</td>
            <td>{{ item.categoryName || '-' }}</td>
            <td>{{ item.fileName }}</td>
            <td>{{ item.description || '-' }}</td>
            <td>{{ formatSize(item.fileSize) }}</td>
            <td><span :class="['badge', auditBadge(item.status)]">{{ auditLabel(item.status) }}</span></td>
            <td class="text-caption">{{ formatDate(item.uploadTime) }}</td>
            <td>
              <div class="flex gap-4">
                <button v-if="item.status === 0" class="btn btn-primary btn-sm" @click="handleApprove(item)">通过</button>
                <button v-if="item.status === 0" class="btn btn-danger btn-sm" @click="openReject(item)">驳回</button>
                <button v-if="item.status !== 0" class="btn btn-subtle btn-sm" @click="viewAuditInfo(item)">审核详情</button>
              </div>
            </td>
          </tr>
          <tr v-if="list.length === 0">
            <td colspan="8" class="empty-cell">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="total > 0">
      <span class="text-caption">共 {{ total }} 条</span>
      <div class="pagination-pages">
        <button class="pagination-btn" :disabled="pageNum <= 1" @click="pageNum--; loadData()">上一页</button>
        <span class="pagination-info">{{ pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pageNum >= totalPages" @click="pageNum++; loadData()">下一页</button>
      </div>
    </div>

    <!-- 驳回弹窗 -->
    <div v-if="showRejectModal" class="modal-overlay" @click.self="showRejectModal = false">
      <div class="modal-content" style="max-width: 420px">
        <div class="modal-header"><h3 class="modal-title">驳回档案</h3><button class="btn-icon" @click="showRejectModal = false">✕</button></div>
        <div class="modal-body">
          <p class="reject-info">文件：{{ rejectItem?.fileName }}</p>
          <div class="form-group">
            <label class="form-label">驳回原因</label>
            <textarea v-model="rejectRemark" class="form-input" rows="3" placeholder="请输入驳回原因（选填）"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-ghost btn-sm" @click="showRejectModal = false">取消</button>
          <button class="btn btn-danger btn-sm" @click="confirmReject" :disabled="rejecting">{{ rejecting ? '处理中...' : '确认驳回' }}</button>
        </div>
      </div>
    </div>

    <!-- 审核详情弹窗 -->
    <div v-if="showAuditInfo" class="modal-overlay" @click.self="showAuditInfo = false">
      <div class="modal-content" style="max-width: 420px">
        <div class="modal-header"><h3 class="modal-title">审核详情</h3><button class="btn-icon" @click="showAuditInfo = false">✕</button></div>
        <div class="modal-body">
          <div class="info-grid">
            <div class="info-item"><span class="info-label">文件名</span><span class="info-value">{{ auditInfoItem?.fileName }}</span></div>
            <div class="info-item"><span class="info-label">审核状态</span><span class="info-value">{{ auditLabel(auditInfoItem?.status) }}</span></div>
            <div class="info-item"><span class="info-label">审核时间</span><span class="info-value">{{ formatDate(auditInfoItem?.auditTime) }}</span></div>
            <div class="info-item" style="grid-column:span 2"><span class="info-label">审核意见</span><span class="info-value">{{ auditInfoItem?.auditRemark || '无' }}</span></div>
          </div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showAuditInfo = false">关闭</button></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { archiveApi } from '../../api/modules'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const list = ref<any[]>([])
const categories = ref<any[]>([])
const total = ref(0)
const pendingCount = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filters = reactive({ status: '' as any, categoryId: '' as any })

const showRejectModal = ref(false)
const rejectItem = ref<any>(null)
const rejectRemark = ref('')
const rejecting = ref(false)

const showAuditInfo = ref(false)
const auditInfoItem = ref<any>(null)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

function auditBadge(s: number) { return [, 'badge-success', 'badge-danger'][s] || 'badge-warning' }
function auditLabel(s: number) { return ['待审核', '已通过', '已驳回'][s] || '未知' }
function formatSize(bytes: number) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + 'KB'
  return (bytes / 1048576).toFixed(1) + 'MB'
}
function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

async function loadCategories() {
  try {
    const res: any = await archiveApi.getCategoryList()
    categories.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadData() {
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filters.status !== '') params.status = filters.status
    if (filters.categoryId) params.categoryId = filters.categoryId
    const res: any = await archiveApi.getFileList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
    // 统计待审核数
    const pendingRes: any = await archiveApi.getFileList({ pageNum: 1, pageSize: 1, status: 0 })
    pendingCount.value = pendingRes.data?.total || 0
  } catch (e) { console.error('加载档案列表失败', e) }
}

async function handleApprove(item: any) {
  if (!confirm(`确认通过「${item.fileName}」？`)) return
  try {
    await archiveApi.auditFile(item.pkArchiveFile, {
      status: 1,
      auditUserId: userStore.userInfo?.userId,
      auditRemark: ''
    })
    loadData()
  } catch (e: any) { alert(e.message || '审核失败') }
}

function openReject(item: any) {
  rejectItem.value = item
  rejectRemark.value = ''
  showRejectModal.value = true
}

async function confirmReject() {
  if (!rejectItem.value) return
  rejecting.value = true
  try {
    await archiveApi.auditFile(rejectItem.value.pkArchiveFile, {
      status: 2,
      auditUserId: userStore.userInfo?.userId,
      auditRemark: rejectRemark.value
    })
    showRejectModal.value = false
    loadData()
  } catch (e: any) { alert(e.message || '驳回失败') }
  finally { rejecting.value = false }
}

function viewAuditInfo(item: any) {
  auditInfoItem.value = item
  showAuditInfo.value = true
}

onMounted(() => { loadCategories(); loadData() })
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.toolbar-filters { display: flex; gap: 8px; align-items: center; }
.toolbar-input { width: 160px; padding: 7px 12px; font-size: 13px; }
.toolbar-stats { display: flex; gap: 8px; }
.stat-badge { padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 510; }
.stat-badge.pending { background: rgba(217, 119, 6, 0.08); color: var(--status-yellow); }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }
.reject-info { font-size: 13px; color: var(--text-tertiary); margin-bottom: 12px; }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.info-item { display: flex; flex-direction: column; gap: 2px; }
.info-label { font-size: 12px; font-weight: 510; color: var(--text-quaternary); }
.info-value { font-size: 14px; font-weight: 510; color: var(--text-secondary); }
</style>
