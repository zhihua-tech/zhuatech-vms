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
  createAppointmentsBatch: appointments => request('/api/vms/appointments/batch', { method: 'POST', body: JSON.stringify({ appointments }) }),
  appointmentAction: (id, action, remark = '') => request(`/api/vms/appointments/${id}/actions`, { method: 'POST', body: JSON.stringify({ action, remark }) }),
  verifyPass: credential => request('/api/vms/passes/verify', { method: 'POST', body: JSON.stringify({ credential }) }),
  visitors: () => request('/api/vms/visitors'),
  blacklist: (id, blacklisted, reason) => request(`/api/admin/vms/visitors/${id}/blacklist`, { method: 'POST', body: JSON.stringify({ blacklisted, reason }) }),
  verifyIdentity: (id, verified, note) => request(`/api/vms/visitors/${id}/identity`, { method: 'POST', body: JSON.stringify({ verified, note }) }),
  resources: () => request('/api/vms/resources'),
  createResource: payload => request('/api/admin/vms/resources', { method: 'POST', body: JSON.stringify(payload) }),
  updateResource: (id, payload) => request(`/api/admin/vms/resources/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteResource: id => request(`/api/admin/vms/resources/${id}`, { method: 'DELETE' }),
  alerts: () => request('/api/vms/alerts'),
  reportAlert: payload => request('/api/vms/alerts', { method: 'POST', body: JSON.stringify(payload) }),
  resolveAlert: (id, resolution) => request(`/api/vms/alerts/${id}/resolve`, { method: 'POST', body: JSON.stringify({ resolution }) }),
  settings: () => request('/api/admin/vms/settings'),
  saveSettings: payload => request('/api/admin/vms/settings', { method: 'PUT', body: JSON.stringify(payload) }),
  report: () => request('/api/admin/vms/reports/operations'),
  auditLogs: () => request('/api/admin/vms/audit-logs'),
  approvalTasks: () => request('/api/vms/approval-tasks'),
  approvalBoard: () => request('/api/admin/vms/enterprise/approval-board'),
  inspectOverstay: () => request('/api/admin/vms/enterprise/overstay-inspections', { method: 'POST' }),
  badges: () => request('/api/vms/badges'),
  issueBadge: (appointmentId, badgeNo) => request(`/api/vms/appointments/${appointmentId}/badges`, { method: 'POST', body: JSON.stringify({ badgeNo }) }),
  badgeAction: (id, action, remark) => request(`/api/vms/badges/${id}/actions`, { method: 'POST', body: JSON.stringify({ action, remark }) }),
  accessEvents: () => request('/api/vms/access-events'),
  recordAccess: payload => request('/api/vms/access-events', { method: 'POST', body: JSON.stringify(payload) }),
  fieldDashboard: () => request('/api/admin/vms/field-dashboard'),
  notifications: () => request('/api/admin/vms/notifications'),
  dispatchNotifications: () => request('/api/admin/vms/notifications/dispatch', { method: 'POST' }),
  retryNotification: id => request(`/api/admin/vms/notifications/${id}/retry`, { method: 'POST' }),
  retentionPreview: () => request('/api/admin/vms/compliance/retention-preview')
}
