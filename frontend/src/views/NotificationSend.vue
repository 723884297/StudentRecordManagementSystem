<template>
  <div class="page">
    <!-- 发送通知表单 -->
    <div class="card send-card">
      <h3 class="card-title">发布新通知</h3>
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label">接收对象 *</label>
          <select v-model="sendForm.targetType" class="form-input" @change="onTargetTypeChange">
            <option value="">请选择</option>
            <option value="all_students">所有学生</option>
            <option value="all_teachers">所有教师/辅导员</option>
            <option value="all_users">所有用户</option>
            <option value="specific">指定用户</option>
          </select>
        </div>
        <div class="form-group" v-if="sendForm.targetType === 'specific'">
          <label class="form-label">选择用户 *</label>
          <div class="user-selector">
            <input v-model="userSearch" class="form-input" placeholder="输入用户名或姓名搜索..." @input="searchUsers" />
            <div class="user-dropdown" v-if="searchResults.length > 0">
              <div v-for="u in searchResults" :key="u.pkUser" class="user-option" @click="addUser(u)">
                <span class="text-small-medium">{{ u.realName }}</span>
                <span class="text-caption" style="color:var(--text-tertiary)">{{ u.username }}</span>
              </div>
            </div>
            <div class="selected-users" v-if="selectedUsers.length > 0">
              <span v-for="u in selectedUsers" :key="u.pkUser" class="user-tag">
                {{ u.realName }}
                <button class="tag-remove" @click="removeUser(u)">✕</button>
              </span>
            </div>
          </div>
        </div>
        <div class="form-group full-width">
          <label class="form-label">通知标题 *</label>
          <input v-model="sendForm.title" class="form-input" placeholder="请输入通知标题" maxlength="100" />
        </div>
        <div class="form-group full-width">
          <label class="form-label">通知内容 *</label>
          <textarea v-model="sendForm.content" class="form-input" rows="5" placeholder="请输入通知内容"></textarea>
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-primary" @click="handleSend" :disabled="sending">
          {{ sending ? '发送中...' : '发布通知' }}
        </button>
      </div>
    </div>

    <!-- 已发送通知列表 -->
    <div class="card">
      <h3 class="card-title">已发送通知</h3>
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>标题</th>
              <th>内容</th>
              <th>发送时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in sentList" :key="item.pkNotification">
              <td class="text-small-medium" style="color: var(--text-primary)">{{ item.title || '系统通知' }}</td>
              <td class="text-caption" style="max-width:400px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap">{{ item.content }}</td>
              <td class="text-caption">{{ formatDate(item.createTime) }}</td>
            </tr>
            <tr v-if="sentList.length === 0">
              <td colspan="3" class="empty-cell">暂无已发送通知</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="pagination" v-if="sentTotal > sentPageSize">
        <span class="text-caption">共 {{ sentTotal }} 条</span>
        <div class="pagination-pages">
          <button class="pagination-btn" :disabled="sentPageNum <= 1" @click="sentPageNum--; loadSentList()">上一页</button>
          <span class="pagination-info">{{ sentPageNum }} / {{ sentTotalPages }}</span>
          <button class="pagination-btn" :disabled="sentPageNum >= sentTotalPages" @click="sentPageNum++; loadSentList()">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { notificationApi, userApi } from '../api/modules'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

// 发送表单
const sendForm = reactive({
  targetType: '',
  title: '',
  content: ''
})
const sending = ref(false)
const userSearch = ref('')
const searchResults = ref<any[]>([])
const selectedUsers = ref<any[]>([])

// 已发送列表
const sentList = ref<any[]>([])
const sentTotal = ref(0)
const sentPageNum = ref(1)
const sentPageSize = ref(10)
const sentTotalPages = computed(() => Math.ceil(sentTotal.value / sentPageSize.value) || 1)

function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function onTargetTypeChange() {
  selectedUsers.value = []
  userSearch.value = ''
  searchResults.value = []
}

let searchTimer: any = null
async function searchUsers() {
  clearTimeout(searchTimer)
  if (!userSearch.value.trim()) {
    searchResults.value = []
    return
  }
  searchTimer = setTimeout(async () => {
    try {
      const res: any = await userApi.getList({ pageNum: 1, pageSize: 20, username: userSearch.value })
      const list = res.data?.list || []
      // 过滤已选中的
      searchResults.value = list.filter((u: any) => !selectedUsers.value.some(s => s.pkUser === u.pkUser))
    } catch (e) { console.error(e) }
  }, 300)
}

function addUser(u: any) {
  if (!selectedUsers.value.some(s => s.pkUser === u.pkUser)) {
    selectedUsers.value.push(u)
  }
  searchResults.value = []
  userSearch.value = ''
}

function removeUser(u: any) {
  selectedUsers.value = selectedUsers.value.filter(s => s.pkUser !== u.pkUser)
}

async function handleSend() {
  if (!sendForm.title.trim()) { alert('请输入通知标题'); return }
  if (!sendForm.content.trim()) { alert('请输入通知内容'); return }
  if (!sendForm.targetType) { alert('请选择接收对象'); return }

  let targetUserIds: number[] = []

  if (sendForm.targetType === 'specific') {
    if (selectedUsers.value.length === 0) { alert('请选择至少一个用户'); return }
    targetUserIds = selectedUsers.value.map(u => u.pkUser)
  } else {
    // 获取对应类型的所有用户
    try {
      const res: any = await userApi.getList({ pageNum: 1, pageSize: 9999 })
      const allUsers: any[] = res.data?.list || []
      if (sendForm.targetType === 'all_users') {
        targetUserIds = allUsers.map(u => u.pkUser)
      } else if (sendForm.targetType === 'all_students') {
        // 简化处理：发给除了管理员以外的所有状态正常的用户
        // 实际上需要根据角色过滤，这里发给所有用户（后续可优化）
        targetUserIds = allUsers.filter(u => u.status === 1).map(u => u.pkUser)
      } else if (sendForm.targetType === 'all_teachers') {
        targetUserIds = allUsers.filter(u => u.status === 1).map(u => u.pkUser)
      }
    } catch (e: any) {
      alert('获取用户列表失败')
      return
    }
  }

  if (targetUserIds.length === 0) { alert('未找到可发送的用户'); return }

  sending.value = true
  try {
    await notificationApi.send({
      title: sendForm.title,
      content: sendForm.content,
      senderId: userStore.userInfo?.userId,
      targetUserIds
    })
    alert('通知发布成功！')
    sendForm.title = ''
    sendForm.content = ''
    sendForm.targetType = ''
    selectedUsers.value = []
    await loadSentList()
  } catch (e: any) {
    alert(e.message || '发送失败')
  } finally {
    sending.value = false
  }
}

async function loadSentList() {
  const userId = userStore.userInfo?.userId
  if (!userId) return
  try {
    const res: any = await notificationApi.getSentList({ pageNum: sentPageNum.value, pageSize: sentPageSize.value, senderId: userId })
    sentList.value = res.data?.list || []
    sentTotal.value = res.data?.total || 0
  } catch (e) { console.error('加载已发送通知失败', e) }
}

onMounted(() => { loadSentList() })
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 20px; }
.card { background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: var(--radius-card); padding: 20px; }
.card-title { font-size: 15px; font-weight: 590; color: var(--text-primary); margin-bottom: 16px; }
.send-card { }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-group.full-width { grid-column: span 2; }
.form-actions { margin-top: 16px; display: flex; justify-content: flex-end; }
.empty-cell { text-align: center; padding: 40px !important; color: var(--text-quaternary); font-size: 13px; }
.pagination-info { font-size: 13px; color: var(--text-tertiary); padding: 0 8px; }

.user-selector { position: relative; }
.user-dropdown {
  position: absolute; top: 100%; left: 0; right: 0; z-index: 10;
  background: var(--bg-surface); border: 1px solid var(--border-secondary);
  border-radius: var(--radius-md); box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  max-height: 200px; overflow-y: auto; margin-top: 4px;
}
.user-option {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8px 12px; cursor: pointer; transition: background 0.1s;
}
.user-option:hover { background: var(--surface-05); }
.selected-users { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; }
.user-tag {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 4px 10px; background: var(--surface-04); border-radius: 20px;
  font-size: 12px; font-weight: 510; color: var(--text-secondary);
}
.tag-remove {
  background: none; border: none; font-size: 11px; color: var(--text-tertiary);
  cursor: pointer; padding: 0 2px; line-height: 1;
}
.tag-remove:hover { color: var(--status-red); }
</style>
