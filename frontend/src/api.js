/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
const authorization = `Basic ${btoa('admin:admin123')}`

export async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { Authorization: authorization, 'Content-Type': 'application/json', ...(options.headers || {}) }
  })
  const body = await response.json().catch(() => ({ message: '服务返回格式异常' }))
  if (!response.ok || body.success === false) throw new Error(body.message || `请求失败（${response.status}）`)
  return body.data
}

export const api = {
  overview: () => request('/api/vms/overview'),
  appointments: () => request('/api/vms/appointments'),
  createAppointment: payload => request('/api/vms/appointments', { method: 'POST', body: JSON.stringify(payload) }),
  appointmentAction: (id, action, remark = '') => request(`/api/vms/appointments/${id}/actions`, { method: 'POST', body: JSON.stringify({ action, remark }) }),
  visitors: () => request('/api/vms/visitors'),
  blacklist: (id, blacklisted, reason) => request(`/api/admin/vms/visitors/${id}/blacklist`, { method: 'POST', body: JSON.stringify({ blacklisted, reason }) }),
  resources: () => request('/api/vms/resources'),
  alerts: () => request('/api/vms/alerts'),
  reportAlert: payload => request('/api/vms/alerts', { method: 'POST', body: JSON.stringify(payload) }),
  resolveAlert: (id, resolution) => request(`/api/vms/alerts/${id}/resolve`, { method: 'POST', body: JSON.stringify({ resolution }) }),
  settings: () => request('/api/admin/vms/settings'),
  saveSettings: payload => request('/api/admin/vms/settings', { method: 'PUT', body: JSON.stringify(payload) })
}
