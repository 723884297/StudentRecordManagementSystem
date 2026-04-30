<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
          <rect width="40" height="40" rx="10" fill="#5e6ad2"/>
          <path d="M12 20L17.5 25.5L28 15" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <h1 class="login-title">学生档案管理系统</h1>
        <p class="login-subtitle">Student Record Management System</p>
      </div>

      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">账号</label>
          <input
            v-model="form.username"
            type="text"
            class="form-input"
            placeholder="请输入登录账号"
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label class="form-label">密码</label>
          <input
            v-model="form.password"
            type="password"
            class="form-input"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>
        <p v-if="errorMsg" :class="['login-error', errorType]">{{ errorMsg }}</p>
        <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { authApi } from '../api/modules'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const errorMsg = ref('')
const errorType = ref('') // 'error-input' | 'error-account' | 'error-server'

const form = reactive({
  username: '',
  password: ''
})

function setError(msg: string, type: string = 'error-account') {
  errorMsg.value = msg
  errorType.value = type
}

function clearError() {
  errorMsg.value = ''
  errorType.value = ''
}

async function handleLogin() {
  clearError()
  if (!form.username.trim()) {
    setError('请输入登录账号', 'error-input')
    return
  }
  if (!form.password) {
    setError('请输入密码', 'error-input')
    return
  }
  if (form.password.length < 4) {
    setError('密码长度不能少于4位', 'error-input')
    return
  }
  loading.value = true
  try {
    const res: any = await authApi.login({
      username: form.username.trim(),
      password: form.password
    })
    const data = res.data
    userStore.setToken(data.token)
    userStore.setUserInfo({
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      roles: data.roles || []
    })
    // 根据角色跳转不同首页
    const roles: string[] = data.roles || []
    if (roles.includes('student')) {
      router.push('/student/profile')
    } else if (roles.includes('counselor') || roles.includes('archive_manager')) {
      router.push('/teacher/dashboard')
    } else {
      router.push('/dashboard')
    }
  } catch (e: any) {
    const code = e.code
    const msg = e.message || '登录失败'
    // 根据错误码分类
    if (code === 1003) {
      setError(msg, 'error-account')    // 账号不存在
    } else if (code === 1005) {
      setError(msg, 'error-password')   // 密码错误
    } else if (code === 1004) {
      setError(msg, 'error-disabled')   // 账号禁用
    } else if (msg.includes('无法连接') || msg.includes('超时') || msg.includes('服务器')) {
      setError(msg, 'error-server')     // 服务器/网络错误
    } else {
      setError(msg, 'error-account')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-base);
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: var(--bg-panel);
  border: 1px solid var(--border-standard);
  border-radius: var(--radius-panel);
  padding: 40px 32px;
  box-shadow: var(--shadow-elevated);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.login-title {
  font-size: 20px;
  font-weight: 590;
  color: var(--text-primary);
  letter-spacing: -0.24px;
}

.login-subtitle {
  font-size: 13px;
  font-weight: 400;
  color: var(--text-quaternary);
  letter-spacing: -0.13px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.login-btn {
  width: 100%;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 510;
  margin-top: 8px;
}
.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-error {
  font-size: 13px;
  text-align: center;
  padding: 8px 12px;
  border-radius: 6px;
  line-height: 1.4;
}

.error-input {
  color: var(--text-tertiary);
  background: var(--surface-03);
  border: 1px solid var(--border-standard);
}

.error-account,
.error-password {
  color: #dc2626;
  background: rgba(220, 38, 38, 0.06);
  border: 1px solid rgba(220, 38, 38, 0.15);
}

.error-disabled {
  color: #c2410c;
  background: rgba(194, 65, 12, 0.06);
  border: 1px solid rgba(194, 65, 12, 0.15);
}

.error-server {
  color: #b45309;
  background: rgba(180, 83, 9, 0.06);
  border: 1px solid rgba(180, 83, 9, 0.15);
}

.spinner {
  display: inline-block;
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}
</style>
