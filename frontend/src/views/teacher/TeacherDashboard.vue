<template>
  <div class="teacher-dashboard">
    <div class="welcome-section">
      <h2 class="welcome-title">教师工作台</h2>
      <p class="welcome-subtitle">管理学生信息、档案审核与通知发送</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ stats.studentTotal }}</div>
        <div class="stat-label">学生总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.activeStudents }}</div>
        <div class="stat-label">在读学生</div>
      </div>
      <div class="stat-card highlight">
        <div class="stat-value">{{ stats.pendingAudit }}</div>
        <div class="stat-label">待审核档案</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.archiveTotal }}</div>
        <div class="stat-label">档案总数</div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">待审核档案</h3>
          <router-link to="/teacher/archives" class="card-link">查看全部 →</router-link>
        </div>
        <div class="audit-list" v-if="pendingFiles.length">
          <div class="audit-item" v-for="file in pendingFiles" :key="file.pkArchiveFile">
            <div class="audit-info">
              <span class="audit-name">{{ file.studentName || '未知学生' }}</span>
              <span class="audit-file">{{ file.fileName }} · {{ file.categoryName || '未分类' }}</span>
            </div>
            <span class="badge badge-warning">待审核</span>
          </div>
        </div>
        <div class="empty-state" v-else>
          <span class="empty-text">暂无待审核档案</span>
        </div>
      </div>

      <div class="card">
        <h3 class="card-title">快捷操作</h3>
        <div class="quick-actions">
          <router-link to="/teacher/students" class="action-card">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
            <span>学生管理</span>
          </router-link>
          <router-link to="/teacher/archives" class="action-card">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
            <span>档案审核</span>
          </router-link>
          <router-link to="/teacher/notifications" class="action-card">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
            <span>通知中心</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { dashboardApi, archiveApi } from '../../api/modules'

const stats = ref({ studentTotal: 0, archiveTotal: 0, pendingAudit: 0, activeStudents: 0 })
const pendingFiles = ref<any[]>([])

onMounted(async () => {
  try {
    const res: any = await dashboardApi.getStats()
    const data = res.data
    const distribution: { label: string; count: number }[] = data.statusDistribution || []
    const activeCount = distribution.find((d: any) => d.label === '在读')?.count || 0

    stats.value = {
      studentTotal: data.studentTotal || 0,
      archiveTotal: data.archiveTotal || 0,
      pendingAudit: data.pendingAudit || 0,
      activeStudents: activeCount
    }
  } catch (e) {
    console.error('加载仪表盘数据失败', e)
  }

  // 加载待审核档案列表（最近5条）
  try {
    const res: any = await archiveApi.getFileList({ pageNum: 1, pageSize: 5, status: 0 })
    pendingFiles.value = res.data?.list || []
  } catch (e) {
    console.error('加载待审核档案失败', e)
  }
})
</script>

<style scoped>
.teacher-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-section {
  padding: 24px 0 8px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.288px;
}

.welcome-subtitle {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 6px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: var(--bg-panel);
  border: 1px solid var(--border-standard);
  border-radius: var(--radius-card);
  padding: 20px;
  box-shadow: var(--shadow-subtle);
}

.stat-card.highlight {
  border-color: rgba(217, 119, 6, 0.3);
  background: rgba(217, 119, 6, 0.04);
}

.stat-value {
  font-size: 28px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.704px;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-tertiary);
  margin-top: 4px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
  margin-bottom: 16px;
  letter-spacing: -0.182px;
}

.card-header .card-title {
  margin-bottom: 0;
}

.card-link {
  font-size: 12px;
  color: var(--brand-indigo);
  text-decoration: none;
  font-weight: 510;
}
.card-link:hover {
  text-decoration: underline;
}

.audit-list {
  display: flex;
  flex-direction: column;
}

.audit-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-subtle);
}
.audit-item:last-child {
  border-bottom: none;
}

.audit-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.audit-name {
  font-size: 13px;
  font-weight: 510;
  color: var(--text-secondary);
}

.audit-file {
  font-size: 12px;
  color: var(--text-quaternary);
}

.empty-state {
  padding: 24px;
  text-align: center;
}

.empty-text {
  font-size: 13px;
  color: var(--text-quaternary);
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--surface-03);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.15s ease;
  font-size: 14px;
  font-weight: 510;
}
.action-card:hover {
  background: var(--surface-05);
  color: var(--text-primary);
  border-color: var(--border-standard);
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
