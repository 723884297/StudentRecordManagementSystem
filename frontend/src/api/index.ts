import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  // FormData 时移除 Content-Type，让浏览器自动设置 multipart/form-data + boundary
  if (config.data instanceof FormData) {
    config.headers.set('Content-Type', false)
  }
  return config
})

api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      const err: any = new Error(res.message || '请求失败')
      err.code = res.code
      return Promise.reject(err)
    }
    return res
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      // 后端返回了业务错误 JSON（如 BusinessException）
      if (data && data.message) {
        const err: any = new Error(data.message)
        err.code = data.code || status
        // 401 且不是登录接口，跳转登录页
        if (status === 401 && !error.config.url?.includes('/auth/login')) {
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
        }
        return Promise.reject(err)
      }
      // HTTP 状态码错误
      const statusMessages: Record<number, string> = {
        400: '请求参数错误',
        401: '未登录或登录已过期',
        403: '没有操作权限',
        404: '请求的资源不存在',
        500: '服务器内部错误',
        502: '服务器网关错误',
        503: '服务暂不可用',
      }
      const msg = statusMessages[status] || `服务器错误 (${status})`
      return Promise.reject(new Error(msg))
    }
    // 网络错误（后端未启动、断网等）
    if (error.code === 'ECONNABORTED') {
      return Promise.reject(new Error('请求超时，请检查网络连接'))
    }
    return Promise.reject(new Error('无法连接服务器，请检查后端服务是否启动'))
  }
)

export default api
