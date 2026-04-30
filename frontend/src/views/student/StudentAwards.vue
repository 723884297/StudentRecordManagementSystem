<template>
  <div class="student-awards">
    <div class="page-header">
      <h2 class="page-title-text">奖项档案</h2>
      <p class="page-desc">查看个人获奖记录</p>
    </div>

    <div class="stats-grid" v-if="student">
      <div class="stat-card">
        <div class="stat-value">{{ awards.length }}</div>
        <div class="stat-label">获奖总数</div>
      </div>
      <div class="stat-card" v-for="lv in levelStats" :key="lv.level">
        <div class="stat-value">{{ lv.count }}</div>
        <div class="stat-label">{{ lv.level }}</div>
      </div>
    </div>

    <div class="card">
      <h3 class="card-title">获奖记录</h3>
      <div class="table-container" v-if="awards.length">
        <table class="data-table">
          <thead>
            <tr>
              <th>奖项名称</th>
              <th>奖项级别</th>
              <th>获奖日期</th>
              <th>颁发机构</th>
              <th>描述</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in awards" :key="item.pkAward">
              <td class="text-small-medium" style="color: var(--text-primary)">{{ item.awardName }}</td>
              <td><span v-if="item.awardLevel" :class="['badge', levelBadge(item.awardLevel)]">{{ item.awardLevel }}</span><span v-else>-</span></td>
              <td class="text-caption">{{ item.awardDate || '-' }}</td>
              <td>{{ item.issuingAuthority || '-' }}</td>
              <td class="text-caption">{{ item.description || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="empty-state" v-else>
        <p class="empty-text">暂无获奖记录</p>
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
const awards = ref<any[]>([])

const levelStats = computed(() => {
  const map = new Map<string, number>()
  awards.value.forEach(a => {
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
        awards.value = awardRes.data || []
      }
    }
  } catch (e) {
    console.error('加载奖项档案失败', e)
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
</style>
