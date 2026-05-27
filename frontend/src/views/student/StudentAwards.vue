<template>
  <div class="student-awards">
    <div class="page-header">
      <h2 class="page-title-text">奖惩档案</h2>
      <p class="page-desc">查看个人奖励与处分记录</p>
    </div>

    <div class="stats-grid" v-if="student">
      <div class="stat-card">
        <div class="stat-value">{{ awardList.length }}</div>
        <div class="stat-label">奖励总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color: var(--status-red)">{{ punishmentList.length }}</div>
        <div class="stat-label">处分总数</div>
      </div>
      <div class="stat-card" v-for="lv in levelStats" :key="lv.level">
        <div class="stat-value">{{ lv.count }}</div>
        <div class="stat-label">{{ lv.level }}</div>
      </div>
    </div>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <button :class="['tab-btn', activeTab === 'award' && 'tab-active']" @click="activeTab = 'award'">奖励记录 ({{ awardList.length }})</button>
      <button :class="['tab-btn', activeTab === 'punishment' && 'tab-active']" @click="activeTab = 'punishment'">处分记录 ({{ punishmentList.length }})</button>
    </div>

    <div class="card">
      <div class="table-container" v-if="currentList.length">
        <table class="data-table">
          <thead>
            <tr>
              <th>{{ activeTab === 'award' ? '奖项名称' : '处分名称' }}</th>
              <th>{{ activeTab === 'award' ? '奖项级别' : '处分级别' }}</th>
              <th>日期</th>
              <th>{{ activeTab === 'award' ? '颁发机构' : '处分机构' }}</th>
              <th>描述</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in currentList" :key="item.pkAward">
              <td class="text-small-medium" style="color: var(--text-primary)">{{ item.awardName }}</td>
              <td><span v-if="item.awardLevel" :class="['badge', activeTab === 'award' ? levelBadge(item.awardLevel) : 'badge-danger']">{{ item.awardLevel }}</span><span v-else>-</span></td>
              <td class="text-caption">{{ item.awardDate || '-' }}</td>
              <td>{{ item.issuingAuthority || '-' }}</td>
              <td class="text-caption">{{ item.description || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="empty-state" v-else>
        <p class="empty-text">{{ activeTab === 'award' ? '暂无奖励记录' : '暂无处分记录' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi, awardApi } from '../../api/modules'

const userStore = useUserStore()
const student = ref<any>(null)
const allRecords = ref<any[]>([])
const activeTab = ref<'award' | 'punishment'>('award')

const awardList = computed(() => allRecords.value.filter(a => a.type !== 'punishment'))
const punishmentList = computed(() => allRecords.value.filter(a => a.type === 'punishment'))
const currentList = computed(() => activeTab.value === 'award' ? awardList.value : punishmentList.value)

const levelStats = computed(() => {
  const map = new Map<string, number>()
  awardList.value.forEach(a => {
    const lv = a.awardLevel || '其他'
    map.set(lv, (map.get(lv) || 0) + 1)
  })
  return Array.from(map.entries()).map(([level, count]) => ({ level, count }))
})

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        student.value = stuRes.data
        const awardRes: any = await awardApi.getByStudentId(stuRes.data.pkStudent)
        allRecords.value = awardRes.data || []
      }
    }
  } catch (e) {
    console.error('加载奖惩档案失败', e)
  }
})

function levelBadge(level: string): string {
  switch (level) {
    case '国家级': return 'badge-danger'
    case '省级': return 'badge-warning'
    case '市级': return 'badge-info'
    case '校级': return 'badge-success'
    default: return 'badge-neutral'
  }
}
</script>

<style scoped>
.student-awards { display: flex; flex-direction: column; gap: 20px; }
.page-header { padding: 8px 0; }
.page-title-text { font-size: 20px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.24px; }
.page-desc { font-size: 14px; color: var(--text-tertiary); margin-top: 4px; }
.stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 16px; }
.stat-card { background: var(--bg-panel); border: 1px solid var(--border-standard); border-radius: var(--radius-card); padding: 20px; box-shadow: var(--shadow-subtle); }
.stat-value { font-size: 24px; font-weight: 590; color: var(--text-primary); letter-spacing: -0.704px; line-height: 1.2; }
.stat-label { font-size: 13px; font-weight: 510; color: var(--text-tertiary); margin-top: 4px; }
.card-title { font-size: 14px; font-weight: 590; color: var(--text-primary); margin-bottom: 16px; letter-spacing: -0.182px; }
.empty-state { padding: 40px 24px; text-align: center; }
.empty-text { font-size: 14px; color: var(--text-quaternary); }
.tab-bar { display: flex; gap: 4px; background: var(--surface-02); padding: 3px; border-radius: var(--radius-md); width: fit-content; }
.tab-btn { padding: 6px 16px; border-radius: var(--radius-md); font-size: 13px; font-weight: 510; color: var(--text-tertiary); background: none; border: none; cursor: pointer; transition: all 0.15s; }
.tab-btn:hover { color: var(--text-secondary); }
.tab-active { background: var(--bg-surface); color: var(--text-primary); box-shadow: var(--shadow-subtle); }
</style>
