/**
 * 前端验证工具类
 */

/**
 * 验证身份证号格式（18位）
 * @param {string} idCard 身份证号
 * @returns {boolean} 是否有效
 */
export const validateIdCard = (idCard) => {
  if (!idCard || typeof idCard !== 'string') return false
  
  const trimmed = idCard.trim()
  
  // 只检查长度：必须是18位
  if (trimmed.length !== 18) return false
  
  // 检查前17位是否为数字，最后一位是数字或X
  const pattern = /^\d{17}[0-9Xx]$/
  return pattern.test(trimmed)
}

/**
 * 验证手机号格式（11位，1开头）
 * @param {string} phone 手机号
 * @returns {boolean} 是否有效
 */
export const validatePhone = (phone) => {
  if (!phone || typeof phone !== 'string') return false
  
  // 11位手机号正则
  const pattern = /^1[3-9]\d{9}$/
  return pattern.test(phone.trim())
}

/**
 * Element Plus 表单验证规则 - 手机号
 */
export const phoneRule = {
  required: true,
  validator: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请输入手机号'))
    } else if (!validatePhone(value)) {
      callback(new Error('请输入正确的11位手机号'))
    } else {
      callback()
    }
  },
  trigger: 'blur'
}

/**
 * 验证身份证号校验位
 * @param {string} idCard 身份证号
 * @returns {boolean} 是否有效
 */
export const validateIdCardChecksum = (idCard) => {
  if (!validateIdCard(idCard)) return false
  
  // 权重因子
  const weights = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
  // 校验码
  const checkCodes = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
  
  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += parseInt(idCard.charAt(i)) * weights[i]
  }
  
  const mod = sum % 11
  const expectedCheckCode = checkCodes[mod]
  const actualCheckCode = idCard.charAt(17).toUpperCase()
  
  return expectedCheckCode === actualCheckCode
}

/**
 * Element Plus 表单验证规则 - 身份证号
 */
export const idCardRule = {
  required: true,
  validator: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请输入身份证号'))
    } else if (!validateIdCard(value)) {
      callback(new Error('请输入正确的18位身份证号'))
    } else {
      callback()
    }
  },
  trigger: 'blur'
}

/**
 * 验证联系方式格式（手机号或固话）
 * @param {string} contact 联系方式
 * @returns {boolean} 是否有效
 */
export const validateContact = (contact) => {
  if (!contact || typeof contact !== 'string') return false
  
  const trimmed = contact.trim()
  
  // 手机号格式：11位，1开头
  const phonePattern = /^1[3-9]\d{9}$/
  // 固话格式：区号-号码 或 纯号码（7-8位）
  const landlinePattern = /^(\d{3,4}-?)?\d{7,8}$/
  
  return phonePattern.test(trimmed) || landlinePattern.test(trimmed)
}

/**
 * Element Plus 表单验证规则 - 联系方式
 */
export const contactRule = {
  required: true,
  validator: (rule, value, callback) => {
    if (!value) {
      callback(new Error('请输入联系方式'))
    } else if (!validateContact(value)) {
      callback(new Error('请输入正确的手机号或固话'))
    } else {
      callback()
    }
  },
  trigger: 'blur'
}