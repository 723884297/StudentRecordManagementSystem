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
            <th>学号</th>
            <th>申请类型</th>
            <th>申请原因</th>
            <th>申请时间</th>
            <th>状态</th>
            <th>有效期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkRequest">
            <td class="text-small-medium" style="color: var(--text-primary)">{{ item.studentName || '-' }}</td>
            <td>{{ item.studentNo || '-' }}</td>
            <td>{{ item.requestType }}</td>
            <td>{{ item.reason || '-' }}</td>
            <td class="text-caption">{{ formatDate(item.createTime) }}</td>
            <td><span :class="['badge', auditBadge(item.status)]">{{ auditLabel(item.status) }}</span></td>
            <td>
              <template v-if="item.status === 1 && item.expireTime">
                <span :class="['badge', isExpired(item.expireTime) ? 'badge-neutral' : 'badge-success']">
                  {{ isExpired(item.expireTime) ? '已过期' : formatDate(item.expireTime) }}
                </span>
              </template>
              <template v-else>-</template>
            </td>
            <td>
              <div class="flex gap-4">
                <button v-if="item.status === 0" class="btn btn-primary btn-sm" @click="handleApprove(item)">通过</button>
                <button v-if="item.status === 0" class="btn btn-danger btn-sm" @click="openReject(item)">驳回</button>
                <button v-if="item.status !== 0" class="btn btn-subtle btn-sm" @click="viewDetail(item)">详情</button>
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
        <div class="modal-header"><h3 class="modal-title">驳回申请</h3><button class="btn-icon" @click="showRejectModal = false">✕</button></div>
        <div class="modal-body">
          <p class="reject-info">学生：{{ rejectItem?.studentName }}（{{ rejectItem?.studentNo }}）· 类型：{{ rejectItem?.requestType }}</p>
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

    <!-- 详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="showDetail = false">
      <div class="modal-content" style="max-width: 420px">
        <div class="modal-header"><h3 class="modal-title">申请详情</h3><button class="btn-icon" @click="showDetail = false">✕</button></div>
        <div class="modal-body">
          <div class="info-grid">
            <div class="info-item"><span class="info-label">学生</span><span class="info-value">{{ detailItem?.studentName }}</span></div>
            <div class="info-item"><span class="info-label">类型</span><span class="info-value">{{ detailItem?.requestType }}</span></div>
            <div class="info-item"><span class="info-label">审核状态</span><span class="info-value">{{ auditLabel(detailItem?.status) }}</span></div>
            <div class="info-item"><span class="info-label">审核时间</span><span class="info-value">{{ formatDate(detailItem?.auditTime) }}</span></div>
            <div class="info-item" style="grid-column:span 2"><span class="info-label">审核意见</span><span class="info-value">{{ detailItem?.auditRemark || '无' }}</span></div>
            <div class="info-item" style="grid-column:span 2" v-if="detailItem?.expireTime"><span class="info-label">导出有效期</span><span class="info-value">{{ formatDate(detailItem?.expireTime) }}{{ isExpired(detailItem?.expireTime) ? ' (已过期)' : '' }}</span></div>
          </div>
        </div>
        <div class="modal-footer"><button class="btn btn-ghost btn-sm" @click="showDetail = false">关闭</button></div>
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
const total = ref(0)
const pendingCount = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filters = reactive({ status: '' as any })

const showRejectModal = ref(false)
const rejectItem = ref<any>(null)
const rejectRemark = ref('')
const rejecting = ref(false)

const showDetail = ref(false)
const detailItem = ref<any>(null)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

function auditBadge(s: number) { return [, 'badge-success', 'badge-danger'][s] || 'badge-warning' }
function auditLabel(s: number) { return ['待审核', '已通过', '已驳回'][s] || '未知' }
function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}
function isExpired(expireStr: string): boolean {
  if (!expireStr) return true
  return new Date(expireStr).getTime() < Date.now()
}

async function loadData() {
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filters.status !== '') params.status = filters.status
    const res: any = await archiveApi.getExportRequestList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
    const pendingRes: any = await archiveApi.getExportRequestList({ pageNum: 1, pageSize: 1, status: 0 })
    pendingCount.value = pendingRes.data?.total || 0
  } catch (e) { console.error('加载导出申请列表失败', e) }
}

async function handleApprove(item: any) {
  if (!confirm(`确认通过「${item.studentName}」的${item.requestType}导出申请？`)) return
  try {
    await archiveApi.auditExportRequest(item.pkRequest, {
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
    await archiveApi.auditExportRequest(rejectItem.value.pkRequest, {
      status: 2,
      auditUserId: userStore.userInfo?.userId,
      auditRemark: rejectRemark.value
    })
    showRejectModal.value = false
    loadData()
  } catch (e: any) { alert(e.message || '驳回失败') }
  finally { rejecting.value = false }
}

function viewDetail(item: any) {
  detailItem.value = item
  showDetail.value = true
}

onMounted(() => { loadData() })
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
