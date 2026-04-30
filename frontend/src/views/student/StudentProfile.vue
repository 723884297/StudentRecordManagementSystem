<template>
  <div class="student-profile">
    <div class="page-header">
      <div class="page-header-left">
        <h2 class="page-title-text">个人信息</h2>
        <p class="page-desc">查看和编辑你的学籍基本信息</p>
      </div>
      <button v-if="student && !editing" class="btn btn-secondary" @click="startEdit">编辑</button>
      <div v-if="editing" class="btn-group">
        <button class="btn btn-secondary" @click="cancelEdit">取消</button>
        <button class="btn btn-primary" @click="saveProfile" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
      </div>
    </div>

    <div class="profile-card card" v-if="student && !editing">
      <div class="profile-header">
        <div class="profile-avatar">{{ student.name?.charAt(0) }}</div>
        <div class="profile-basic">
          <h3 class="profile-name">{{ student.name }}</h3>
          <p class="profile-no">学号：{{ student.studentNo }}</p>
        </div>
        <span :class="['badge', statusClass]">{{ student.status }}</span>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">性别</span>
          <span class="info-value">{{ student.gender === 'M' ? '男' : student.gender === 'F' ? '女' : '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">出生日期</span>
          <span class="info-value">{{ student.birthDate || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">入学年份</span>
          <span class="info-value">{{ student.enrollmentYear || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">学制</span>
          <span class="info-value">{{ student.schoolLength ? student.schoolLength + '年' : '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">学院</span>
          <span class="info-value">{{ student.collegeName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">专业</span>
          <span class="info-value">{{ student.majorName || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">班级</span>
          <span class="info-value">{{ student.className || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">身份证号</span>
          <span class="info-value">{{ maskIdCard(student.idCard) }}</span>
        </div>
      </div>
    </div>

    <!-- 编辑表单 -->
    <div class="profile-card card" v-if="editing">
      <h3 class="card-title">编辑个人信息</h3>
      <form class="edit-form" @submit.prevent="saveProfile">
        <div class="form-grid">
          <div class="form-group">
            <label class="form-label">联系电话</label>
            <input v-model="editForm.phone" class="form-input" placeholder="请输入联系电话" />
          </div>
          <div class="form-group">
            <label class="form-label">电子邮箱</label>
            <input v-model="editForm.email" class="form-input" placeholder="请输入电子邮箱" />
          </div>
          <div class="form-group">
            <label class="form-label">民族</label>
            <input v-model="editForm.nation" class="form-input" placeholder="请输入民族" />
          </div>
          <div class="form-group">
            <label class="form-label">政治面貌</label>
            <input v-model="editForm.politicalStatus" class="form-input" placeholder="请输入政治面貌" />
          </div>
        </div>
      </form>
    </div>

    <div class="detail-card card" v-if="detail && !editing">
      <h3 class="card-title">详细信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">民族</span>
          <span class="info-value">{{ detail.nation || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">政治面貌</span>
          <span class="info-value">{{ detail.politicalStatus || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">联系电话</span>
          <span class="info-value">{{ detail.phone || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">电子邮箱</span>
          <span class="info-value">{{ detail.email || '-' }}</span>
        </div>
        <div class="info-item full-width">
          <span class="info-label">家庭住址</span>
          <span class="info-value">{{ detail.homeAddress || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">紧急联系人</span>
          <span class="info-value">{{ detail.emergencyContact || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">紧急联系电话</span>
          <span class="info-value">{{ detail.emergencyPhone || '-' }}</span>
        </div>
      </div>
    </div>

    <div class="empty-state card" v-if="!student && !loading">
      <p class="empty-text">暂未关联学生信息，请联系管理员</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi } from '../../api/modules'

const userStore = useUserStore()
const student = ref<any>(null)
const detail = ref<any>(null)
const loading = ref(true)
const editing = ref(false)
const saving = ref(false)

const editForm = reactive({
  phone: '',
  email: '',
  nation: '',
  politicalStatus: ''
})

const statusClass = computed(() => {
  const s = student.value?.status
  if (s === '在读') return 'badge-success'
  if (s === '休学') return 'badge-warning'
  if (s === '退学') return 'badge-danger'
  if (s === '毕业') return 'badge-info'
  return 'badge-neutral'
})

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        student.value = stuRes.data
        const detailRes: any = await studentApi.getDetail(stuRes.data.pkStudent)
        detail.value = detailRes.data
      }
    }
  } catch (e) {
    console.error('加载个人信息失败', e)
  } finally {
    loading.value = false
  }
})

function startEdit() {
  editForm.phone = detail.value?.phone || ''
  editForm.email = detail.value?.email || ''
  editForm.nation = detail.value?.nation || ''
  editForm.politicalStatus = detail.value?.politicalStatus || ''
  editing.value = true
}

function cancelEdit() {
  editing.value = false
}

async function saveProfile() {
  if (!student.value?.pkStudent) return
  saving.value = true
  try {
    await studentApi.updateDetail(student.value.pkStudent, {
      phone: editForm.phone,
      email: editForm.email,
      nation: editForm.nation,
      politicalStatus: editForm.politicalStatus
    })
    const detailRes: any = await studentApi.getDetail(student.value.pkStudent)
    detail.value = detailRes.data
    editing.value = false
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}

function maskIdCard(idCard: string): string {
  if (!idCard) return '-'
  if (idCard.length > 8) {
    return idCard.substring(0, 4) + '****' + idCard.substring(idCard.length - 4)
  }
  return idCard
}
</script>

<style scoped>
.student-profile {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 8px 0;
}

.page-title-text {
  font-size: 20px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.24px;
}

.page-desc {
  font-size: 14px;
  color: var(--text-tertiary);
  margin-top: 4px;
}

.btn-group {
  display: flex;
  gap: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-subtle);
  margin-bottom: 20px;
}

.profile-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--brand-indigo);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 590;
  flex-shrink: 0;
}

.profile-basic {
  flex: 1;
}

.profile-name {
  font-size: 18px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.165px;
}

.profile-no {
  font-size: 13px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.card-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
  margin-bottom: 16px;
  letter-spacing: -0.182px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: span 2;
}

.info-label {
  font-size: 12px;
  font-weight: 510;
  color: var(--text-quaternary);
}

.info-value {
  font-size: 14px;
  font-weight: 510;
  color: var(--text-secondary);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
}

.empty-text {
  font-size: 14px;
  color: var(--text-quaternary);
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  .info-item.full-width {
    grid-column: span 1;
  }
}
</style>
