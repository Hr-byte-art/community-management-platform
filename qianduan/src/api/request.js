import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const isAiEnhanceRequest = (config) => typeof config?.url === 'string' && config.url.includes('/ai/complete')

const createAiTraceId = () => `ai-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`

const getDurationMs = (startedAt) => {
  if (!startedAt) return 0
  return Date.now() - startedAt
}

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  if (isAiEnhanceRequest(config)) {
    const traceId = createAiTraceId()
    config.headers = config.headers || {}
    config.headers['X-AI-Trace-Id'] = traceId
    config.metadata = {
      ...(config.metadata || {}),
      aiTraceId: traceId,
      aiStartedAt: Date.now()
    }
    console.info('[ai-complete][frontend] request-start', {
      traceId,
      url: config.url,
      method: config.method,
      startedAt: new Date(config.metadata.aiStartedAt).toISOString()
    })
  }

  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data

    if (isAiEnhanceRequest(response.config)) {
      const metadata = response.config?.metadata || {}
      console.info('[ai-complete][frontend] request-finish', {
        traceId: metadata.aiTraceId,
        url: response.config?.url,
        status: response.status,
        code: res.code,
        durationMs: getDurationMs(metadata.aiStartedAt)
      })
    }

    if (res.code !== 200) {
      ElMessage.error(res.message || '\u8bf7\u6c42\u5931\u8d25')
      return Promise.reject(res)
    }
    return res
  },
  error => {
    if (isAiEnhanceRequest(error.config)) {
      const metadata = error.config?.metadata || {}
      console.warn('[ai-complete][frontend] request-error', {
        traceId: metadata.aiTraceId,
        url: error.config?.url,
        status: error.response?.status,
        message: error.message,
        durationMs: getDurationMs(metadata.aiStartedAt)
      })
    }

    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('permissions')
      router.push('/login')
      ElMessage.error('\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55')
    } else {
      ElMessage.error(error.message || '\u7f51\u7edc\u9519\u8bef')
    }
    return Promise.reject(error)
  }
)

export default request
