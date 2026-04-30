<template>
  <div class="student-family">
    <div class="page-header">
      <h2 class="page-title-text">家庭信息</h2>
      <p class="page-desc">查看和编辑你的家庭联系信息</p>
    </div>

    <div class="card" v-if="detail">
      <div class="card-header">
        <h3 class="card-title">家庭联系方式</h3>
        <button v-if="!editing" class="btn btn-secondary" @click="editing = true">编辑</button>
        <div v-else class="btn-group">
          <button class="btn btn-secondary" @click="cancelEdit">取消</button>
          <button class="btn btn-primary" @click="saveDetail" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
        </div>
      </div>

      <div class="info-grid" v-if="!editing">
        <div class="info-item">
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
        <div class="info-item">
          <span class="info-label">联系电话</span>
          <span class="info-value">{{ detail.phone || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">电子邮箱</span>
          <span class="info-value">{{ detail.email || '-' }}</span>
        </div>
      </div>

      <form v-else class="edit-form" @submit.prevent="saveDetail">
        <div class="form-grid">
          <div class="form-group full-width">
            <label class="form-label">家庭住址</label>
            <input v-model="editForm.homeAddress" class="form-input" placeholder="请输入家庭住址" />
          </div>
          <div class="form-group">
            <label class="form-label">紧急联系人</label>
            <input v-model="editForm.emergencyContact" class="form-input" placeholder="请输入紧急联系人" />
          </div>
          <div class="form-group">
            <label class="form-label">紧急联系电话</label>
            <input v-model="editForm.emergencyPhone" class="form-input" placeholder="请输入紧急联系电话" />
          </div>
          <div class="form-group">
            <label class="form-label">联系电话</label>
            <input v-model="editForm.phone" class="form-input" placeholder="请输入联系电话" />
          </div>
          <div class="form-group">
            <label class="form-label">电子邮箱</label>
            <input v-model="editForm.email" class="form-input" placeholder="请输入电子邮箱" />
          </div>
        </div>
      </form>
    </div>

    <div class="empty-state card" v-if="!detail && !loading">
      <p class="empty-text">暂未关联学生信息，请联系管理员</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { studentApi } from '../../api/modules'

const userStore = useUserStore()
const detail = ref<any>(null)
const studentId = ref<number | null>(null)
const loading = ref(true)
const editing = ref(false)
const saving = ref(false)

const editForm = reactive({
  homeAddress: '',
  emergencyContact: '',
  emergencyPhone: '',
  phone: '',
  email: ''
})

onMounted(async () => {
  try {
    const userId = userStore.userInfo?.userId
    if (userId) {
      const stuRes: any = await studentApi.getByUserId(userId)
      if (stuRes.data) {
        studentId.value = stuRes.data.pkStudent
        const detailRes: any = await studentApi.getDetail(stuRes.data.pkStudent)
        if (detailRes.data) {
          detail.value = detailRes.data
        } else {
          detail.value = {}
        }
      }
    }
  } catch (e) {
    console.error('加载家庭信息失败', e)
  } finally {
    loading.value = false
  }
})

function cancelEdit() {
  editing.value = false
  resetForm()
}

function resetForm() {
  editForm.homeAddress = detail.value?.homeAddress || ''
  editForm.emergencyContact = detail.value?.emergencyContact || ''
  editForm.emergencyPhone = detail.value?.emergencyPhone || ''
  editForm.phone = detail.value?.phone || ''
  editForm.email = detail.value?.email || ''
}

async function saveDetail() {
  if (!studentId.value) return
  saving.value = true
  try {
    await studentApi.updateDetail(studentId.value, {
      homeAddress: editForm.homeAddress,
      emergencyContact: editForm.emergencyContact,
      emergencyPhone: editForm.emergencyPhone,
      phone: editForm.phone,
      email: editForm.email
    })
    // 重新加载
    const detailRes: any = await studentApi.getDetail(studentId.value)
    detail.value = detailRes.data || {}
    editing.value = false
  } catch (e) {
    console.error('保存失败', e)
  } finally {
    saving.value = false
  }
}

// 进入编辑时填充表单
import { watch } from 'vue'
watch(editing, (val) => {
  if (val) resetForm()
})
</script>

<style scoped>
.student-family {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.page-header { padding: 8px 0; }
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-title {
  font-size: 14px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.182px;
}
.btn-group {
  display: flex;
  gap: 8px;
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
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.form-group.full-width {
  grid-column: span 2;
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
  .info-grid, .form-grid { grid-template-columns: 1fr; }
  .form-group.full-width { grid-column: span 1; }
}
</style>
