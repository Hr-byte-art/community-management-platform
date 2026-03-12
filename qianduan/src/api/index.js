import request from './request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data)
}

export const userApi = {
  getInfo: () => request.get('/user/info'),
  update: (data) => request.put('/user/update', data),
  updatePassword: (data) => request.put('/user/password', data),
  list: (params) => request.get('/user/list', { params }),
  add: (data) => request.post('/user', data),
  edit: (id, data) => request.put(`/user/${id}`, data),
  del: (id) => request.delete(`/user/${id}`)
}

export const residentApi = {
  list: (params) => request.get('/resident/list', { params }),
  get: (id) => request.get(`/resident/${id}`),
  add: (data) => request.post('/resident', data),
  edit: (id, data) => request.put(`/resident/${id}`, data),
  del: (id) => request.delete(`/resident/${id}`),
  getFamily: (id) => request.get(`/resident/${id}/family`),
  addFamily: (data) => request.post('/resident/family', data),
  delFamily: (id) => request.delete(`/resident/family/${id}`)
}

export const activityApi = {
  list: (params) => request.get('/activity/list', { params }),
  get: (id) => request.get(`/activity/${id}`),
  add: (data) => request.post('/activity', data),
  edit: (id, data) => request.put(`/activity/${id}`, data),
  del: (id) => request.delete(`/activity/${id}`)
}

export const workOrderApi = {
  list: (params) => request.get('/workorder/list', { params }),
  get: (id) => request.get(`/workorder/${id}`),
  enhance: (data) => request.post('/workorder/ai/complete', data),
  add: (data) => request.post('/workorder', data),
  edit: (id, data) => request.put(`/workorder/${id}`, data),
  handle: (id, data) => request.put(`/workorder/${id}/handle`, data),
  del: (id) => request.delete(`/workorder/${id}`)
}

export const volunteerApi = {
  list: (params) => request.get('/volunteer/list', { params }),
  get: (id) => request.get(`/volunteer/${id}`),
  apply: (data) => request.post('/volunteer/apply', data),
  audit: (id, status) => request.put(`/volunteer/${id}/audit`, null, { params: { status } }),
  edit: (id, data) => request.put(`/volunteer/${id}`, data),
  del: (id) => request.delete(`/volunteer/${id}`)
}

export const floatingApi = {
  list: (params) => request.get('/floating/list', { params }),
  get: (id) => request.get(`/floating/${id}`),
  add: (data) => request.post('/floating', data),
  edit: (id, data) => request.put(`/floating/${id}`, data),
  del: (id) => request.delete(`/floating/${id}`)
}

export const hazardApi = {
  list: (params) => request.get('/hazard/list', { params }),
  get: (id) => request.get(`/hazard/${id}`),
  enhance: (data) => request.post('/hazard/ai/complete', data),
  add: (data) => request.post('/hazard', data),
  edit: (id, data) => request.put(`/hazard/${id}`, data),
  handle: (id, data) => request.put(`/hazard/${id}/handle`, data),
  del: (id) => request.delete(`/hazard/${id}`)
}

export const guideApi = {
  list: (params) => request.get('/guide/list', { params }),
  get: (id) => request.get(`/guide/${id}`),
  add: (data) => request.post('/guide', data),
  edit: (id, data) => request.put(`/guide/${id}`, data),
  del: (id) => request.delete(`/guide/${id}`)
}

export const appointmentApi = {
  list: (params) => request.get('/appointment/list', { params }),
  my: (params) => request.get('/appointment/my', { params }),
  get: (id) => request.get(`/appointment/${id}`),
  enhance: (data) => request.post('/appointment/ai/complete', data),
  add: (data) => request.post('/appointment', data),
  edit: (id, data) => request.put(`/appointment/${id}`, data),
  updateStatus: (id, status) => request.put(`/appointment/${id}/status`, null, { params: { status } }),
  del: (id) => request.delete(`/appointment/${id}`)
}

export const neighborHelpApi = {
  list: (params) => request.get('/neighborhelp/list', { params }),
  get: (id) => request.get(`/neighborhelp/${id}`),
  preview: (id) => request.get(`/neighborhelp/preview/${id}`),
  enhance: (data) => request.post('/neighborhelp/ai/complete', data),
  add: (data) => request.post('/neighborhelp', data),
  edit: (id, data) => request.put(`/neighborhelp/${id}`, data),
  updateStatus: (id, status) => request.put(`/neighborhelp/${id}/status`, null, { params: { status } }),
  del: (id) => request.delete(`/neighborhelp/${id}`)
}

export const noticeApi = {
  list: (params) => request.get('/notice/list', { params }),
  get: (id) => request.get(`/notice/${id}`),
  getStats: (id) => request.get(`/notice/${id}/stats`),
  add: (data) => request.post('/notice', data),
  edit: (id, data) => request.put(`/notice/${id}`, data),
  del: (id) => request.delete(`/notice/${id}`)
}

export const pointsApi = {
  my: () => request.get('/points/my'),
  records: (params) => request.get('/points/records', { params }),
  rank: (limit = 10) => request.get('/points/rank', { params: { limit } })
}

export const messageApi = {
  my: (params) => request.get('/message/my', { params }),
  unreadCount: () => request.get('/message/unread/count'),
  read: (id) => request.put(`/message/${id}/read`),
  readAll: () => request.put('/message/read-all'),
  adminList: (params) => request.get('/message/admin/list', { params })
}

export const evaluationApi = {
  myWorkOrders: (params) => request.get('/evaluation/my/workorders', { params }),
  submit: (workOrderId, data) => request.post(`/evaluation/workorder/${workOrderId}`, data),
  getByWorkOrder: (workOrderId) => request.get(`/evaluation/workorder/${workOrderId}`),
  my: (params) => request.get('/evaluation/my', { params }),
  adminList: (params) => request.get('/evaluation/admin/list', { params }),
  stats: () => request.get('/evaluation/stats')
}

export const dispatchRuleApi = {
  list: (params) => request.get('/dispatch/rule/list', { params }),
  add: (data) => request.post('/dispatch/rule', data),
  edit: (id, data) => request.put(`/dispatch/rule/${id}`, data),
  del: (id) => request.delete(`/dispatch/rule/${id}`),
  preview: (params) => request.get('/dispatch/rule/preview', { params })
}

export const statisticsApi = {
  overview: () => request.get('/statistics/overview'),
  workorder: () => request.get('/statistics/workorder'),
  activity: () => request.get('/statistics/activity'),
  resident: () => request.get('/statistics/resident')
}

export const fileApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export const exportApi = {
  resident: () => '/api/export/resident',
  workorder: () => '/api/export/workorder',
  activity: () => '/api/export/activity',
  floating: () => '/api/export/floating'
}

export const logApi = {
  list: (params) => request.get('/log/list', { params }),
  del: (id) => request.delete(`/log/${id}`),
  clear: () => request.delete('/log/clear')
}
