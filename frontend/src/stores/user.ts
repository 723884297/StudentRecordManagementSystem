import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
  userId: number
  username: string
  realName: string
  roles: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>((() => {
    try {
      const stored = localStorage.getItem('userInfo')
      return stored ? JSON.parse(stored) : null
    } catch { return null }
  })())

  // 角色判断计算属性
  const isAdmin = computed(() => userInfo.value?.roles?.includes('admin') ?? false)
  const isStudent = computed(() => userInfo.value?.roles?.includes('student') ?? false)
  const isTeacher = computed(() => {
    const roles = userInfo.value?.roles || []
    return roles.includes('counselor') || roles.includes('archive_manager')
  })

  function setToken(t: string) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isAdmin, isStudent, isTeacher, setToken, setUserInfo, logout }
})
