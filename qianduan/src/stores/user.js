import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const permissionStorage = localStorage.getItem('permissions')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || '')
  const permissions = ref(permissionStorage ? JSON.parse(permissionStorage) : null)

  const setUser = (userData, tokenData, permissionCodes = permissions.value) => {
    user.value = userData
    token.value = tokenData
    permissions.value = Array.isArray(permissionCodes) ? permissionCodes : null
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('token', tokenData)
    if (permissions.value === null) {
      localStorage.removeItem('permissions')
    } else {
      localStorage.setItem('permissions', JSON.stringify(permissions.value))
    }
  }

  const setPermissions = (permissionCodes = []) => {
    permissions.value = Array.isArray(permissionCodes) ? permissionCodes : []
    localStorage.setItem('permissions', JSON.stringify(permissions.value))
  }

  const logout = () => {
    user.value = null
    token.value = ''
    permissions.value = null
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    localStorage.removeItem('permissions')
  }

  const isAdmin = () => user.value?.role === 'ADMIN'
  const hasPerm = (permissionCode) => {
    if (!permissionCode) return true
    if (isAdmin()) return true
    if (permissions.value === null) return true
    return permissions.value.includes(permissionCode)
  }
  const hasAnyPerm = (permissionCodes = []) => {
    if (!permissionCodes.length) return true
    if (isAdmin()) return true
    if (permissions.value === null) return true
    return permissionCodes.some(code => permissions.value.includes(code))
  }

  return { user, token, permissions, setUser, setPermissions, logout, isAdmin, hasPerm, hasAnyPerm }
})
