<template>
  <div class="page">
    <div class="page-toolbar">
      <div class="flex gap-8">
        <input v-model="filters.module" class="form-input" style="width:150px" placeholder="模块筛选" @keyup.enter="search" />
        <input v-model="filters.operation" class="form-input" style="width:150px" placeholder="操作类型" @keyup.enter="search" />
        <button class="btn btn-primary btn-sm" @click="search">搜索</button>
        <button class="btn btn-ghost btn-sm" @click="resetFilters">重置</button>
      </div>
    </div>

    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th style="width:60px">ID</th>
            <th style="width:100px">操作人</th>
            <th style="width:100px">模块</th>
            <th style="width:120px">操作类型</th>
            <th>描述</th>
            <th style="width:120px">IP</th>
            <th style="width:160px">时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in list" :key="item.pkLog">
            <td>{{ item.pkLog }}</td>
            <td>{{ item.userName || item.userId || '-' }}</td>
            <td><span class="tag">{{ item.module || '-' }}</span></td>
            <td>{{ item.operation || '-' }}</td>
            <td class="text-ellipsis">{{ item.description || '-' }}</td>
            <td>{{ item.ip || '-' }}</td>
            <td>{{ formatDate(item.createTime) }}</td>
          </tr>
          <tr v-if="!list.length && !loading">
            <td colspan="7" class="empty-cell">暂无操作日志</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="total > pageSize">
      <span class="text-caption">共 {{ total }} 条</span>
      <div class="pagination-pages">
        <button class="pagination-btn" :disabled="pageNum <= 1" @click="pageNum--; loadData()">上一页</button>
        <span class="pagination-info">{{ pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pageNum >= totalPages" @click="pageNum++; loadData()">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { operationLogApi } from '../api/modules'

const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const loading = ref(false)

const filters = reactive({
  module: '',
  operation: ''
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

function search() {
  pageNum.value = 1
  loadData()
}

function resetFilters() {
  filters.module = ''
  filters.operation = ''
  pageNum.value = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (filters.module) params.module = filters.module
    if (filters.operation) params.operation = filters.operation
    const res: any = await operationLogApi.getList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载操作日志失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.page-toolbar { display: flex; justify-content: space-between; align-items: center; }
.flex { display: flex; align-items: center; }
.gap-8 { gap: 8px; }
.table-wrap { overflow-x: auto; border: 1px solid var(--border-standard); border-radius: var(--radius-card); }
.table { width: 100%; border-collapse: collapse; font-size: 13px; }
.table th { background: var(--bg-secondary); font-weight: 510; color: var(--text-secondary); padding: 10px 12px; text-align: left; border-bottom: 1px solid var(--border-standard); }
.table td { padding: 10px 12px; border-bottom: 1px solid var(--border-subtle); color: var(--text-primary); }
.table tr:last-child td { border-bottom: none; }
.table tr:hover td { background: var(--surface-05); }
.tag { display: inline-block; padding: 2px 8px; border-radius: 4px; background: var(--surface-05); font-size: 12px; color: var(--text-secondary); }
.text-ellipsis { max-width: 260px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.empty-cell { text-align: center; padding: 40px 12px !important; color: var(--text-quaternary); }
.pagination { display: flex; justify-content: space-between; align-items: center; }
.pagination-pages { display: flex; align-items: center; gap: 4px; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }
.pagination-btn { padding: 6px 12px; border: 1px solid var(--border-standard); border-radius: 6px; background: var(--bg-panel); font-size: 13px; cursor: pointer; color: var(--text-primary); }
.pagination-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.text-caption { font-size: 12px; color: var(--text-tertiary); }
.form-input { padding: 7px 12px; border: 1px solid var(--border-standard); border-radius: 6px; font-size: 13px; background: var(--bg-panel); color: var(--text-primary); outline: none; }
.form-input:focus { border-color: var(--brand-indigo); }
.btn { padding: 7px 14px; border-radius: 6px; font-size: 13px; font-weight: 510; cursor: pointer; border: none; transition: all 0.15s; }
.btn-primary { background: var(--brand-indigo); color: #fff; }
.btn-primary:hover { opacity: 0.9; }
.btn-ghost { background: transparent; color: var(--text-secondary); border: 1px solid var(--border-standard); }
.btn-ghost:hover { background: var(--surface-05); }
.btn-sm { padding: 6px 12px; }
</style>
