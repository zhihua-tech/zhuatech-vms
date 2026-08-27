<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ -->
<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { api } from './api'
import { domain } from './domain'

const mode = ref('admin')
const active = ref('运营总览')
const resourceTab = ref('访客档案')
const enterpriseTab = ref('审批治理')
const statusFilter = ref('全部状态')
const search = ref('')
const loading = ref(false)
const toast = ref('')
const modal = ref('')
const selected = ref(null)
const mobileView = ref('首页')
const appointments = ref([...domain.appointments])
const visitors = ref([...domain.visitors])
const resources = ref([...domain.resources])
const alerts = ref([...domain.alerts])
const auditLogs = ref([])
const approvalTasks = ref([])
const badges = ref([])
const accessEvents = ref([])
const notifications = ref([])
const sites = ref([])
const contractorCredentials = ref([])
const appointmentDocuments = ref([])
const complianceAssessment = ref(null)
const muster = reactive({ siteCode: 'SH-HQ', siteName: '', assemblyPoint: '', visitorsOnSite: 0, appointmentCount: 0, people: [] })
const musterSiteCode = ref('SH-HQ')
const approvalBoard = reactive({ pending: 0, overdue: 0, securityReview: 0, highRisk: 0, recentTasks: [] })
const fieldDashboard = reactive({ availableBadges: 0, issuedBadges: 0, lostBadges: 0, deniedAccessEvents: 0, pendingNotifications: 0, failedNotifications: 0 })
const retentionPreview = reactive({ retentionDays: 180, threshold: '', visitorProfiles: 0, auditLogs: 0, action: '' })
const report = reactive({ totalAppointments: 0, appointmentStatus: {}, totalVisitors: 0, unverifiedVisitors: 0, blacklistedVisitors: 0, totalAlerts: 0, openAlerts: 0, totalResources: 0, availableResources: 0 })
const settings = reactive({ ...domain.settings })
const nav = [
  { label: '运营总览', icon: '总' }, { label: '业务协同', icon: '协' },
  { label: '资源中心', icon: '资' }, { label: '风险预警', icon: '险' },
  { label: '企业管控', icon: '企' }, { label: '审计报表', icon: '报' }, { label: '基础设置', icon: '设' }
]
const appointmentForm = reactive({ visitorName: '', visitorCompany: '', visitorPhone: '', hostName: '', purpose: '', visitDate: '2026-08-27', timeSlot: '09:00-11:00', accessArea: 'A座会议中心', visitorCount: 1, siteCode: 'SH-HQ' })
const exceptionForm = reactive({ type: '现场异常', level: '中', title: '', relatedNo: '', assignee: '园区安保中心' })
const resourceForm = reactive({ id: null, type: '接待人', code: '', name: '', department: '', status: '启用' })
const siteForm = reactive({ siteCode: '', siteName: '', address: '', timezone: 'Asia/Shanghai', slotCapacity: 100, assemblyPoint: '', status: '启用' })
const credentialForm = reactive({ companyName: '', credentialType: '施工服务资质', credentialNo: '', validUntil: '2027-12-31', safetyTrainingCompleted: true, status: '有效' })
const passCredential = ref('VMS-20260826-102')
const passVerification = ref(null)
const accessForm = reactive({ appointmentNo: 'VMS-20260826-102', gateCode: 'GATE-01', direction: 'IN' })
const accessDecision = ref(null)

const metrics = computed(() => [
  { label: '今日预约', value: appointments.value.length, unit: '单', note: '全部预约' },
  { label: '待审批', value: appointments.value.filter(v => v.status === '待审批').length, unit: '单', note: '需要处理' },
  { label: '当前在园', value: appointments.value.filter(v => v.status === '已到访').reduce((s, v) => s + v.visitorCount, 0), unit: '人', note: '已完成签到' },
  { label: '待处理预警', value: alerts.value.filter(v => v.status === '待处理').length, unit: '条', note: '风险与异常' }
])
const filteredAppointments = computed(() => appointments.value.filter(item => {
  const statusOk = statusFilter.value === '全部状态' || item.status === statusFilter.value
  const key = search.value.trim().toLowerCase()
  const searchOk = !key || [item.appointmentNo, item.visitorName, item.visitorCompany, item.hostName, item.purpose].some(v => String(v).toLowerCase().includes(key))
  return statusOk && searchOk
}))
const currentPass = computed(() => appointments.value.find(v => v.passCode && ['已审批', '已到访'].includes(v.status)) || appointments.value[1])

function announce(message) {
  toast.value = message
  window.setTimeout(() => { toast.value = '' }, 2600)
}
function localAction(item, action) {
  const map = { APPROVE: '已审批', REJECT: '已驳回', CANCEL: '已取消', CHECK_IN: '已到访', CHECK_OUT: '已离场' }
  item.status = map[action]
  if (action === 'APPROVE' && !item.passCode) item.passCode = String(Math.floor(100000 + Math.random() * 900000))
}
async function loadData() {
  loading.value = true
  const calls = await Promise.allSettled([api.appointments(), api.visitors(), api.resources(), api.alerts(), api.settings(), api.report(), api.auditLogs(), api.approvalTasks(), api.approvalBoard(), api.badges(), api.accessEvents(), api.fieldDashboard(), api.notifications(), api.retentionPreview(), api.sites(), api.contractorCredentials(), api.muster(musterSiteCode.value)])
  if (calls[0].status === 'fulfilled') appointments.value = calls[0].value
  if (calls[1].status === 'fulfilled') visitors.value = calls[1].value
  if (calls[2].status === 'fulfilled') resources.value = calls[2].value
  if (calls[3].status === 'fulfilled') alerts.value = calls[3].value
  if (calls[4].status === 'fulfilled') Object.assign(settings, calls[4].value)
  if (calls[5].status === 'fulfilled') Object.assign(report, calls[5].value)
  if (calls[6].status === 'fulfilled') auditLogs.value = calls[6].value
  if (calls[7].status === 'fulfilled') approvalTasks.value = calls[7].value
  if (calls[8].status === 'fulfilled') Object.assign(approvalBoard, calls[8].value)
  if (calls[9].status === 'fulfilled') badges.value = calls[9].value
  if (calls[10].status === 'fulfilled') accessEvents.value = calls[10].value
  if (calls[11].status === 'fulfilled') Object.assign(fieldDashboard, calls[11].value)
  if (calls[12].status === 'fulfilled') notifications.value = calls[12].value
  if (calls[13].status === 'fulfilled') Object.assign(retentionPreview, calls[13].value)
  if (calls[14].status === 'fulfilled') sites.value = calls[14].value
  if (calls[15].status === 'fulfilled') contractorCredentials.value = calls[15].value
  if (calls[16].status === 'fulfilled') Object.assign(muster, calls[16].value)
  loading.value = false
}
function openAppointment(item) { selected.value = item; modal.value = 'detail' }
async function runAction(item, action) {
  try {
    const updated = await api.appointmentAction(item.id, action, '管理端操作')
    Object.assign(item, updated)
  } catch { localAction(item, action) }
  modal.value = ''
  announce({ APPROVE: '预约已审批并签发通行码', REJECT: '预约已驳回', CANCEL: '预约已取消', CHECK_IN: '签到成功，访客已入园', CHECK_OUT: '签退成功，通行权限已回收' }[action])
}
async function createAppointment() {
  if (!appointmentForm.visitorName || !appointmentForm.visitorPhone || !appointmentForm.hostName || !appointmentForm.purpose) return announce('请完整填写访客、手机、接待人与来访事由')
  let created
  try { created = await api.createAppointment({ ...appointmentForm, visitorCount: Number(appointmentForm.visitorCount), clientRequestId: window.crypto?.randomUUID?.() || `WEB-${Date.now()}` }) }
  catch {
    created = { ...appointmentForm, id: Date.now(), appointmentNo: `VMS-DEMO-${String(Date.now()).slice(-4)}`, visitorCount: Number(appointmentForm.visitorCount), status: '待审批', riskLevel: appointmentForm.accessArea.includes('受限') ? '关注' : '正常' }
  }
  appointments.value.unshift(created)
  modal.value = ''
  announce('预约已提交，等待接待人审批')
}
async function toggleBlacklist(visitor) {
  const blacklisted = !visitor.blacklisted
  try { Object.assign(visitor, await api.blacklist(visitor.id, blacklisted, blacklisted ? '管理端人工加入黑名单' : '复核通过，解除黑名单')) }
  catch { visitor.blacklisted = blacklisted }
  announce(blacklisted ? '已加入黑名单，后续预约将触发拦截' : '已解除黑名单')
}
async function verifyVisitor(visitor) {
  try { Object.assign(visitor, await api.verifyIdentity(visitor.id, !visitor.identityVerified, visitor.identityVerified ? '管理员撤销核验状态' : '前台核验证件原件')) }
  catch { visitor.identityVerified = !visitor.identityVerified }
  announce(visitor.identityVerified ? '身份核验已完成并留痕' : '已撤销身份核验状态')
}
function openResource(item = null, type = '接待人') {
  Object.assign(resourceForm, item ? { ...item } : { id: null, type, code: '', name: '', department: '', status: type === '门禁点' ? '在线' : '启用' })
  modal.value = 'resource'
}
async function saveResource() {
  if (!resourceForm.code || !resourceForm.name || !resourceForm.department) return announce('请完整填写资源编码、名称和责任部门')
  const payload = { type: resourceForm.type, code: resourceForm.code, name: resourceForm.name, department: resourceForm.department, status: resourceForm.status }
  try {
    const saved = resourceForm.id ? await api.updateResource(resourceForm.id, payload) : await api.createResource(payload)
    const index = resources.value.findIndex(item => item.id === saved.id)
    if (index >= 0) resources.value[index] = saved
    else resources.value.push(saved)
    modal.value = ''
    announce(resourceForm.id ? '园区资源已更新' : '园区资源已新增')
  } catch (error) { announce(error.message) }
}
async function removeResource(item) {
  try { await api.deleteResource(item.id); resources.value = resources.value.filter(value => value.id !== item.id); announce('园区资源已删除') }
  catch (error) { announce(error.message) }
}
async function verifyCredential() {
  if (!passCredential.value.trim()) return announce('请输入预约编号或六位通行码')
  try { passVerification.value = await api.verifyPass(passCredential.value.trim()); announce(passVerification.value.message) }
  catch (error) { passVerification.value = { valid: false, message: error.message }; announce(error.message) }
}
async function resolveAlert(alert) {
  try { Object.assign(alert, await api.resolveAlert(alert.id, '已联系接待人并完成现场核验')) }
  catch { alert.status = '已处理'; alert.resolution = '已联系接待人并完成现场核验' }
  announce('预警已处置并留痕')
}
async function reportException() {
  if (!exceptionForm.title) return announce('请填写异常说明')
  let created
  try { created = await api.reportAlert({ ...exceptionForm }) }
  catch { created = { ...exceptionForm, id: Date.now(), alertNo: `ALT-DEMO-${String(Date.now()).slice(-4)}`, status: '待处理', createdAt: new Date().toISOString() } }
  alerts.value.unshift(created)
  modal.value = ''
  announce('异常已上报，安保中心将跟进处理')
}
async function saveSettings() {
  try { await api.saveSettings({ ...settings, retentionDays: Number(settings.retentionDays), slotCapacity: Number(settings.slotCapacity), approvalSlaHours: Number(settings.approvalSlaHours) }) } catch { /* 离线演示保留本地设置 */ }
  announce('基础设置已保存')
}
async function runOverstayInspection() {
  try {
    const result = await api.inspectOverstay()
    await loadData()
    announce(`巡检完成：检查 ${result.inspected} 条在园记录，新增 ${result.alertsGenerated} 条预警`)
  } catch (error) { announce(error.message) }
}
async function issueAvailableBadge(item) {
  const badge = badges.value.find(value => value.status === '可用')
  if (!badge) return announce('当前没有可发放的访客证')
  try {
    await api.issueBadge(item.id, badge.badgeNo)
    await loadData()
    announce(`${badge.badgeNo} 已发放给 ${item.visitorName}`)
  } catch (error) { announce(error.message) }
}
async function operateBadge(item, action) {
  try {
    await api.badgeAction(item.id, action, action === 'RETURN' ? '前台人工归还' : '现场确认遗失并冻结权限')
    await loadData()
    announce(action === 'RETURN' ? '访客证已归还并恢复可用' : '访客证已挂失并生成高风险预警')
  } catch (error) { announce(error.message) }
}
async function recordGateAccess() {
  if (!accessForm.appointmentNo.trim()) return announce('请输入预约编号')
  try {
    accessDecision.value = await api.recordAccess({ ...accessForm })
    await loadData()
    announce(accessDecision.value.message)
  } catch (error) { accessDecision.value = { allowed: false, message: error.message }; announce(error.message) }
}
async function dispatchNotifications() {
  try {
    const result = await api.dispatchNotifications()
    await loadData()
    announce(`派发完成：成功 ${result.sent} 条，失败 ${result.failed} 条`)
  } catch (error) { announce(error.message) }
}
async function retryNotification(item) {
  try { await api.retryNotification(item.id); await loadData(); announce('通知已进入立即重试队列') }
  catch (error) { announce(error.message) }
}
async function openCompliance(item) {
  selected.value = item
  modal.value = 'compliance'
  try {
    const [documents, assessment] = await Promise.all([api.appointmentDocuments(item.id), api.compliance(item.id)])
    appointmentDocuments.value = documents
    complianceAssessment.value = assessment
  } catch (error) { announce(error.message) }
}
async function submitComplianceDocument(type) {
  const checksum = String(Date.now()).padEnd(64, '0').slice(0, 64)
  try {
    await api.submitDocument(selected.value.id, { documentType: type, fileName: `${selected.value.appointmentNo}-${type}.pdf`, checksum })
    await openCompliance(selected.value)
    announce(`${type}已提交审核`)
  } catch (error) { announce(error.message) }
}
async function reviewComplianceDocument(item, approved) {
  try {
    await api.reviewDocument(item.id, approved, approved ? '管理端核验材料真实有效' : '材料内容不完整，请重新提交')
    await openCompliance(selected.value)
    announce(approved ? '材料审核通过' : '材料已驳回')
  } catch (error) { announce(error.message) }
}
async function refreshMuster() {
  try { Object.assign(muster, await api.muster(musterSiteCode.value)); announce('应急清点名单已按实时在园状态更新') }
  catch (error) { announce(error.message) }
}
async function exportSiteAppointments() {
  try { await api.exportAppointments(musterSiteCode.value, '2026-01-01', '2026-12-31'); announce('预约 CSV 已生成') }
  catch (error) { announce(error.message) }
}
async function saveSite() {
  if (!siteForm.siteCode || !siteForm.siteName || !siteForm.address || !siteForm.assemblyPoint) return announce('请完整填写园区编码、名称、地址和集合点')
  try {
    await api.createSite({ ...siteForm, slotCapacity: Number(siteForm.slotCapacity) })
    modal.value = ''
    await loadData()
    announce('企业园区已新增并启用')
  } catch (error) { announce(error.message) }
}
async function toggleSite(item) {
  try {
    await api.updateSite(item.id, { siteCode: item.siteCode, siteName: item.siteName, address: item.address,
      timezone: item.timezone, slotCapacity: item.slotCapacity, assemblyPoint: item.assemblyPoint,
      status: item.status === '启用' ? '停用' : '启用' })
    await loadData()
    announce(item.status === '启用' ? '园区已停用，新的预约将被拦截' : '园区已恢复启用')
  } catch (error) { announce(error.message) }
}
async function saveCredential() {
  if (!credentialForm.companyName || !credentialForm.credentialNo) return announce('请填写承包商名称和证书编号')
  try {
    await api.createCredential({ ...credentialForm })
    modal.value = ''
    await loadData()
    announce('承包商资质已登记')
  } catch (error) { announce(error.message) }
}
async function toggleCredential(item) {
  try {
    await api.updateCredential(item.id, { companyName: item.companyName, credentialType: item.credentialType,
      credentialNo: item.credentialNo, validUntil: item.validUntil,
      safetyTrainingCompleted: item.safetyTrainingCompleted, status: item.status === '有效' ? '冻结' : '有效' })
    await loadData()
    announce(item.status === '有效' ? '承包商资质已冻结' : '承包商资质已恢复')
  } catch (error) { announce(error.message) }
}
function maskPhone(phone) { return phone ? phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '-' }
function showMobile(view) { mobileView.value = view; mode.value = 'mobile' }
onMounted(loadData)
</script>

<template>
  <div class="app" :style="{ '--accent': domain.accent, '--accent-soft': domain.accentSoft }">
    <template v-if="mode === 'admin'">
      <aside class="sidebar">
        <div class="brand"><span class="mark">ZH</span><div><b>{{ domain.shortName }}</b><small>访客预约与通行管理</small></div></div>
        <nav><button v-for="item in nav" :key="item.label" :class="{ active: active === item.label }" @click="active = item.label"><i>{{ item.icon }}</i><span>{{ item.label }}</span></button></nav>
        <div class="side-foot"><span>社区源码版 · V1.7</span><p>{{ domain.company }}</p><a :href="domain.website" target="_blank">访问知华科技官网</a></div>
      </aside>

      <main class="main">
        <header class="topbar"><div><span>园区运营中心</span><b>/ {{ active }}</b></div><div class="top-actions"><button class="outline" @click="showMobile('首页')">移动端预览</button><span class="message-dot">2</span><span class="avatar">管</span><div class="account"><b>系统管理员</b><small>admin</small></div></div></header>
        <div class="content" :class="{ loading }">
          <template v-if="active === '运营总览'">
            <section class="page-title"><div><p class="eyebrow">VISITOR OPERATIONS</p><h1>园区接待运行总览</h1><p>聚合预约、审批、在园访客和风险处置状态</p></div><button class="primary" @click="modal='appointment'">＋ 新建预约</button></section>
            <section class="notice"><i>!</i><div><b>今日运营提示</b><p>受限区域预约需完成实名核验与安保复核；超时未离场记录请及时闭环。</p></div><button @click="active='风险预警'">查看预警</button></section>
            <section class="metrics"><article v-for="metric in metrics" :key="metric.label"><div><p>{{ metric.label }}</p><strong>{{ metric.value }}<small>{{ metric.unit }}</small></strong></div><span>{{ metric.note }}</span></article></section>
            <section class="dashboard-grid">
              <article class="panel"><div class="panel-head"><div><h2>来访流程进度</h2><p>按当前预约状态汇总</p></div><span class="subtle-tag">今日</span></div><div class="process"><div v-for="(stage,index) in ['预约提交','接待审批','身份核验','门禁通行','离场确认']" :key="stage"><i>{{ index+1 }}</i><span>{{ stage }}</span><b>{{ [100,82,71,64,46][index] }}%</b></div></div></article>
              <article class="panel"><div class="panel-head"><div><h2>现场接待状态</h2><p>园区前台与门禁设备</p></div><span class="online">系统正常</span></div><div class="health"><strong>96</strong><span>运行健康度</span></div><ul class="health-list"><li><span>在线门禁</span><b>2 / 2</b></li><li><span>平均审批时长</span><b>12 分钟</b></li><li><span>身份核验率</span><b>92%</b></li></ul></article>
            </section>
            <section class="panel recent"><div class="panel-head"><div><h2>最近预约</h2><p>优先处理待审批及风险关注记录</p></div><button class="text-button" @click="active='业务协同'">全部预约 →</button></div><div class="data-table"><div class="row head"><span>预约编号</span><span>访客 / 单位</span><span>接待人</span><span>访问区域</span><span>状态</span><span>操作</span></div><div class="row" v-for="item in appointments.slice(0,4)" :key="item.id"><span class="mono">{{ item.appointmentNo }}</span><span><b>{{ item.visitorName }}</b><small>{{ item.visitorCompany }}</small></span><span>{{ item.hostName }}</span><span>{{ item.accessArea }}</span><span><i class="status" :class="item.status">{{ item.status }}</i></span><span><button class="link" @click="openAppointment(item)">查看</button></span></div></div></section>
          </template>

          <template v-else-if="active === '业务协同'">
            <section class="page-title"><div><p class="eyebrow">APPOINTMENT WORKFLOW</p><h1>预约与接待协同</h1><p>统一处理预约、审批、签到、通行和离场</p></div><button class="primary" @click="modal='appointment'">＋ 新建预约</button></section>
            <section class="toolbar"><div class="search"><span>⌕</span><input v-model="search" placeholder="搜索预约编号、访客、单位或接待人" /></div><select v-model="statusFilter"><option v-for="item in ['全部状态','待审批','安保复核','已审批','已到访','已离场','已驳回','已取消']" :key="item">{{ item }}</option></select><span class="result">共 {{ filteredAppointments.length }} 条</span></section>
            <section class="panel"><div class="data-table appointment-table"><div class="row head"><span>预约信息</span><span>访客</span><span>来访安排</span><span>接待与区域</span><span>风险</span><span>状态</span><span>操作</span></div><div class="row" v-for="item in filteredAppointments" :key="item.id"><span><b class="mono">{{ item.appointmentNo }}</b><small>{{ item.purpose }}</small></span><span><b>{{ item.visitorName }} · {{ item.visitorCount }}人</b><small>{{ maskPhone(item.visitorPhone) }}</small></span><span><b>{{ item.visitDate }}</b><small>{{ item.timeSlot }}</small></span><span><b>{{ item.hostName }}</b><small>{{ item.accessArea }}</small></span><span><i class="risk" :class="item.riskLevel">{{ item.riskLevel }}</i></span><span><i class="status" :class="item.status">{{ item.status }}</i></span><span><button class="link" @click="openAppointment(item)">处理</button></span></div></div></section>
          </template>

          <template v-else-if="active === '资源中心'">
            <section class="page-title"><div><p class="eyebrow">RESOURCE CENTER</p><h1>访客与园区资源</h1><p>维护访客档案、接待人员、访问区域和门禁点</p></div></section>
            <div class="tabs"><button v-for="tab in ['访客档案','接待人与区域']" :key="tab" :class="{ active: resourceTab === tab }" @click="resourceTab=tab">{{ tab }}</button></div>
            <section v-if="resourceTab === '访客档案'" class="panel"><div class="data-table visitor-table"><div class="row head"><span>访客</span><span>联系方式</span><span>身份核验</span><span>累计来访</span><span>最近到访</span><span>名单状态</span><span>操作</span></div><div class="row" v-for="item in visitors" :key="item.id"><span><b>{{ item.visitorName }}</b><small>{{ item.company }}</small></span><span>{{ maskPhone(item.phone) }}</span><span><i class="verify" :class="{ ok:item.identityVerified }">{{ item.identityVerified ? '已核验' : '待核验' }}</i></span><span>{{ item.visitCount }} 次</span><span>{{ item.lastVisitAt?.slice(0,10) || '暂无' }}</span><span><i class="status" :class="item.blacklisted ? '已驳回' : '已审批'">{{ item.blacklisted ? '黑名单' : '正常' }}</i></span><span class="row-actions"><button class="link" @click="verifyVisitor(item)">{{ item.identityVerified ? '撤销核验' : '身份核验' }}</button><button class="link" @click="toggleBlacklist(item)">{{ item.blacklisted ? '解除名单' : '加入名单' }}</button></span></div></div></section>
            <section v-else class="resource-grid"><article v-for="group in ['接待人','访问区域','门禁点']" :key="group" class="panel resource-card"><div class="panel-head"><div><h2>{{ group }}</h2><p>{{ resources.filter(v=>v.type===group).length }} 项可用资源</p></div><button class="icon-button" @click="openResource(null,group)">＋</button></div><div class="resource-item" v-for="item in resources.filter(v=>v.type===group)" :key="item.id"><span class="resource-icon">{{ group.slice(0,1) }}</span><div><b>{{ item.name }}</b><small>{{ item.code }} · {{ item.department }}</small></div><div class="resource-actions"><i>{{ item.status }}</i><button @click="openResource(item)">编辑</button><button @click="removeResource(item)">删除</button></div></div></article></section>
          </template>

          <template v-else-if="active === '风险预警'">
            <section class="page-title"><div><p class="eyebrow">RISK & INCIDENT</p><h1>风险预警与异常处置</h1><p>跟踪超时、资料缺失、通行异常和应急清点事项</p></div><button class="primary" @click="modal='exception'">＋ 上报异常</button></section>
            <section class="risk-summary"><article><small>待处理预警</small><strong>{{ alerts.filter(v=>v.status==='待处理').length }}</strong><span>需责任人跟进</span></article><article><small>高风险</small><strong>{{ alerts.filter(v=>v.level==='高'&&v.status==='待处理').length }}</strong><span>优先核验</span></article><article><small>今日闭环</small><strong>{{ alerts.filter(v=>v.status==='已处理').length }}</strong><span>保留处置记录</span></article><article class="risk-note"><b>准入判断规则</b><p>黑名单命中直接拦截；夜间、受限区域或身份未核验进入人工复核。</p></article></section>
            <section class="panel"><div class="data-table alert-table"><div class="row head"><span>预警编号</span><span>风险内容</span><span>关联预约</span><span>等级</span><span>责任人</span><span>状态</span><span>操作</span></div><div class="row" v-for="item in alerts" :key="item.id"><span class="mono">{{ item.alertNo }}</span><span><b>{{ item.title }}</b><small>{{ item.type }} · {{ item.createdAt?.replace('T',' ').slice(0,16) }}</small></span><span class="mono">{{ item.relatedNo }}</span><span><i class="risk" :class="item.level">{{ item.level }}</i></span><span>{{ item.assignee }}</span><span><i class="status" :class="item.status">{{ item.status }}</i></span><span><button v-if="item.status==='待处理'" class="link" @click="resolveAlert(item)">完成处置</button><span v-else class="done">已留痕</span></span></div></div></section>
            <section class="panel emergency"><div><span class="emergency-icon">应</span><div><h2>应急疏散访客清点</h2><p>按入场、离场、集合点签到和接待人确认人数计算未清点访客。</p></div></div><button class="outline" @click="announce('演示：当前在园访客均已纳入应急清点名单')">生成清点名单</button></section>
          </template>

          <template v-else-if="active === '企业管控'">
            <section class="page-title"><div><p class="eyebrow">ENTERPRISE CONTROL</p><h1>企业接待控制台</h1><p>从准入审批延伸到访客证、门禁事件、消息派发和数据合规的完整现场闭环</p></div><button class="primary" @click="loadData">刷新运行数据</button></section>
            <div class="tabs enterprise-tabs"><button v-for="tab in ['审批治理','现场通行','消息与合规','资质与应急']" :key="tab" :class="{ active: enterpriseTab === tab }" @click="enterpriseTab=tab">{{ tab }}</button></div>

            <template v-if="enterpriseTab === '审批治理'">
              <section class="metrics enterprise-metrics"><article><div><p>待处理审批</p><strong>{{ approvalBoard.pending }}<small>项</small></strong></div><span>接待人与安保任务</span></article><article><div><p>审批已超时</p><strong>{{ approvalBoard.overdue }}<small>项</small></strong></div><span>超过 {{ settings.approvalSlaHours }} 小时 SLA</span></article><article><div><p>安保复核</p><strong>{{ approvalBoard.securityReview }}<small>项</small></strong></div><span>受限区与高风险预约</span></article><article><div><p>风险关注</p><strong>{{ approvalBoard.highRisk }}<small>单</small></strong></div><span>尚未完成准入审批</span></article></section>
              <section class="enterprise-grid"><article class="panel"><div class="panel-head"><div><h2>审批任务中心</h2><p>任务按创建时间倒序展示，所有决策保留操作人和意见</p></div><span class="subtle-tag">最近 50 项</span></div><div class="data-table approval-table"><div class="row head"><span>预约编号</span><span>审批阶段</span><span>责任人</span><span>截止时间</span><span>状态</span><span>处理人</span></div><div class="row" v-for="item in approvalBoard.recentTasks" :key="item.id"><span class="mono">{{ item.appointmentNo }}</span><span><b>{{ item.stage }}</b><small>{{ item.comment || '等待处理意见' }}</small></span><span>{{ item.assignee }}</span><span :class="{ overdue:item.overdue }">{{ item.dueAt?.replace('T',' ').slice(0,16) }}</span><span><i class="status" :class="item.status">{{ item.status }}</i></span><span>{{ item.decisionBy || '-' }}</span></div><div v-if="!approvalBoard.recentTasks.length" class="empty-state">新建预约后，审批任务会在这里自动生成。</div></div></article><aside class="panel policy-card"><div class="panel-head"><div><h2>当前管控策略</h2><p>{{ settings.siteName }} · SH-HQ</p></div><span class="online">策略生效</span></div><dl><div><dt>普通预约</dt><dd>接待人单级审批</dd></div><div><dt>受限区 / 8人以上</dt><dd>接待人 + 安保二级审批</dd></div><div><dt>时段容量上限</dt><dd>{{ settings.slotCapacity }} 人</dd></div><div><dt>审批处理 SLA</dt><dd>{{ settings.approvalSlaHours }} 小时</dd></div><div><dt>重复请求保护</dt><dd>客户端请求号幂等</dd></div><div><dt>在园风险</dt><dd>超时巡检自动生成预警</dd></div></dl><button class="outline" @click="runOverstayInspection">执行在园超时巡检</button></aside></section>
            </template>

            <template v-else-if="enterpriseTab === '现场通行'">
              <section class="metrics field-metrics"><article><div><p>可用访客证</p><strong>{{ fieldDashboard.availableBadges }}<small>张</small></strong></div><span>可立即发放</span></article><article><div><p>已发放</p><strong>{{ fieldDashboard.issuedBadges }}<small>张</small></strong></div><span>在园持证</span></article><article><div><p>挂失证件</p><strong>{{ fieldDashboard.lostBadges }}<small>张</small></strong></div><span>权限已冻结</span></article><article><div><p>拒绝通行</p><strong>{{ fieldDashboard.deniedAccessEvents }}<small>次</small></strong></div><span>防尾随与状态拦截</span></article></section>
              <section class="field-grid">
                <article class="panel gate-console"><div class="panel-head"><div><h2>门禁通行工作台</h2><p>模拟闸机上报，系统根据预约实时决定准入并阻止重复进出</p></div><span class="online">GATEWAY ONLINE</span></div><div class="gate-form"><label>预约编号<input v-model="accessForm.appointmentNo" placeholder="VMS-..." /></label><label>门禁点<select v-model="accessForm.gateCode"><option v-for="item in resources.filter(v=>v.type==='门禁点')" :key="item.code" :value="item.code">{{ item.name }} · {{ item.code }}</option></select></label><label>通行方向<select v-model="accessForm.direction"><option value="IN">入场 IN</option><option value="OUT">离场 OUT</option></select></label><button class="primary" @click="recordGateAccess">执行通行核验</button></div><div v-if="accessDecision" class="access-decision" :class="{ denied: !accessDecision.allowed }"><strong>{{ accessDecision.allowed ? '允许通行' : '拒绝通行' }}</strong><p>{{ accessDecision.message }}</p></div></article>
                <aside class="panel badge-issue"><div class="panel-head"><div><h2>待发证预约</h2><p>审批完成后绑定实体访客证</p></div></div><div class="issue-list"><div v-for="item in appointments.filter(v=>['已审批','已到访'].includes(v.status) && !badges.some(b=>b.appointmentNo===v.appointmentNo)).slice(0,5)" :key="item.id"><span><b>{{ item.visitorName }}</b><small>{{ item.appointmentNo }}</small></span><button class="link" @click="issueAvailableBadge(item)">发放访客证</button></div><p v-if="!appointments.some(v=>['已审批','已到访'].includes(v.status) && !badges.some(b=>b.appointmentNo===v.appointmentNo))" class="empty-copy">当前没有待发证预约</p></div></aside>
              </section>
              <section class="panel"><div class="panel-head"><div><h2>访客证台账</h2><p>覆盖领用、归还、自动回收和挂失冻结全过程</p></div><span class="subtle-tag">{{ badges.length }} 张</span></div><div class="data-table badge-table"><div class="row head"><span>证件编号</span><span>状态</span><span>持有人 / 预约</span><span>发放时间</span><span>最近说明</span><span>操作</span></div><div class="row" v-for="item in badges" :key="item.id"><span class="mono">{{ item.badgeNo }}</span><span><i class="status" :class="item.status">{{ item.status }}</i></span><span><b>{{ item.holderName || '-' }}</b><small>{{ item.appointmentNo || '未绑定预约' }}</small></span><span>{{ item.issuedAt?.replace('T',' ').slice(0,16) || '-' }}</span><span>{{ item.remark || '-' }}</span><span class="row-actions"><button v-if="item.status==='已发放'" class="link" @click="operateBadge(item,'RETURN')">归还</button><button v-if="item.status==='已发放'" class="link danger-link" @click="operateBadge(item,'REPORT_LOST')">挂失</button></span></div></div></section>
              <section class="panel event-panel"><div class="panel-head"><div><h2>最近门禁事件</h2><p>允许与拒绝均持久化留痕，便于事后核查</p></div><span class="subtle-tag">最近 100 条</span></div><div class="data-table event-table"><div class="row head"><span>时间</span><span>预约编号</span><span>门禁 / 方向</span><span>判定</span><span>判定依据</span><span>操作人</span></div><div class="row" v-for="item in accessEvents" :key="item.id"><span>{{ item.occurredAt?.replace('T',' ').slice(0,16) }}</span><span class="mono">{{ item.appointmentNo }}</span><span><b>{{ item.gateCode }}</b><small>{{ item.direction === 'IN' ? '入场' : '离场' }}</small></span><span><i class="status" :class="item.result">{{ item.result }}</i></span><span>{{ item.reason }}</span><span>{{ item.operatorName }}</span></div><div v-if="!accessEvents.length" class="empty-state">执行一次通行核验后，门禁事件将在这里展示。</div></div></section>
            </template>

            <template v-else-if="enterpriseTab === '消息与合规'">
              <section class="metrics field-metrics"><article><div><p>待发送通知</p><strong>{{ fieldDashboard.pendingNotifications }}<small>条</small></strong></div><span>等待消息工作器</span></article><article><div><p>发送失败</p><strong>{{ fieldDashboard.failedNotifications }}<small>条</small></strong></div><span>支持人工重试</span></article><article><div><p>待清理档案</p><strong>{{ retentionPreview.visitorProfiles }}<small>份</small></strong></div><span>只预检不自动删除</span></article><article><div><p>待清理日志</p><strong>{{ retentionPreview.auditLogs }}<small>条</small></strong></div><span>{{ retentionPreview.retentionDays }} 天留存策略</span></article></section>
              <section class="message-grid"><article class="panel"><div class="panel-head"><div><h2>可靠消息任务</h2><p>业务操作写入通知任务，外部通道失败后保留原因与重试时间</p></div><button class="primary compact" @click="dispatchNotifications">执行消息派发</button></div><div class="data-table notice-table"><div class="row head"><span>创建时间</span><span>业务编号 / 模板</span><span>渠道</span><span>接收人</span><span>状态 / 次数</span><span>操作</span></div><div class="row" v-for="item in notifications" :key="item.id"><span>{{ item.createdAt?.replace('T',' ').slice(0,16) }}</span><span><b class="mono">{{ item.referenceNo }}</b><small>{{ item.templateCode }}</small></span><span>{{ item.channel }}</span><span>{{ item.recipient }}</span><span><i class="status" :class="item.status">{{ item.status }}</i><small>尝试 {{ item.attempts }} 次</small></span><span><button v-if="item.status==='失败'" class="link" @click="retryNotification(item)">立即重试</button><small v-else>{{ item.lastError || '-' }}</small></span></div></div></article><aside class="panel retention-card"><div class="panel-head"><div><h2>数据留存预检</h2><p>COMPLIANCE PREVIEW</p></div><span class="online">只读检查</span></div><strong>{{ retentionPreview.retentionDays }}</strong><span>天留存周期</span><dl><div><dt>计算阈值</dt><dd>{{ retentionPreview.threshold?.replace('T',' ').slice(0,16) }}</dd></div><div><dt>超期访客档案</dt><dd>{{ retentionPreview.visitorProfiles }} 份</dd></div><div><dt>超期审计日志</dt><dd>{{ retentionPreview.auditLogs }} 条</dd></div></dl><p>{{ retentionPreview.action }}</p><button class="outline" @click="active='基础设置'">调整留存策略</button></aside></section>
            </template>

            <template v-else>
              <div class="section-command"><div><h2>企业园区</h2><p>园区停用后，新预约和清点请求会立即被拦截。</p></div><button class="primary" @click="modal='site'">＋ 新增园区</button></div><section class="site-strip"><article v-for="item in sites" :key="item.id" class="panel site-card"><div><span class="site-code">{{ item.siteCode }}</span><i class="status" :class="item.status">{{ item.status }}</i></div><h2>{{ item.siteName }}</h2><p>{{ item.address }}</p><dl><div><dt>单时段容量</dt><dd>{{ item.slotCapacity }} 人</dd></div><div><dt>应急集合点</dt><dd>{{ item.assemblyPoint }}</dd></div></dl><button class="text-button" @click="toggleSite(item)">{{ item.status==='启用' ? '停用园区' : '恢复园区' }}</button></article></section>
              <section class="compliance-grid"><article class="panel"><div class="panel-head"><div><h2>承包商资质台账</h2><p>按企业维护证书有效期、安全培训和冻结状态</p></div><button class="primary compact" @click="modal='credential'">＋ 登记资质</button></div><div class="data-table credential-table"><div class="row head"><span>承包商</span><span>资质类型 / 编号</span><span>有效期</span><span>安全培训</span><span>状态</span><span>操作</span></div><div class="row" v-for="item in contractorCredentials" :key="item.id"><span><b>{{ item.companyName }}</b></span><span><b>{{ item.credentialType }}</b><small class="mono">{{ item.credentialNo }}</small></span><span>{{ item.validUntil }}</span><span>{{ item.safetyTrainingCompleted ? '已完成' : '未完成' }}</span><span><i class="status" :class="item.status">{{ item.status }}</i></span><span><button class="link" @click="toggleCredential(item)">{{ item.status==='有效' ? '冻结' : '恢复' }}</button></span></div></div></article><aside class="panel muster-card"><div class="panel-head"><div><h2>应急在园清点</h2><p>基于真实签到状态、访客证和最后门禁事件</p></div><span class="online">实时名单</span></div><label>清点园区<select v-model="musterSiteCode" @change="refreshMuster"><option v-for="item in sites.filter(v=>v.status==='启用')" :key="item.siteCode" :value="item.siteCode">{{ item.siteName }}</option></select></label><div class="muster-total"><strong>{{ muster.visitorsOnSite }}</strong><span>名访客在园 · {{ muster.appointmentCount }} 单预约</span></div><p><b>集合点：</b>{{ muster.assemblyPoint }}</p><div class="muster-people"><div v-for="item in muster.people" :key="item.appointmentNo"><span><b>{{ item.visitorName }} · {{ item.visitorCount }}人</b><small>{{ item.hostName }} / {{ item.badgeNo }}</small></span><i>{{ item.lastGate }}</i></div><small v-if="!muster.people.length">当前园区暂无在园访客</small></div><div class="muster-actions"><button class="outline" @click="refreshMuster">刷新名单</button><button class="primary" @click="exportSiteAppointments">导出预约 CSV</button></div></aside></section>
            </template>
          </template>

          <template v-else-if="active === '审计报表'">
            <section class="page-title"><div><p class="eyebrow">REPORT & AUDIT</p><h1>运营报表与操作审计</h1><p>查看预约结构、访客风险、资源可用率以及最近业务操作</p></div><button class="outline" @click="loadData">刷新数据</button></section>
            <section class="metrics report-metrics"><article><div><p>预约总量</p><strong>{{ report.totalAppointments }}<small>单</small></strong></div><span>全状态汇总</span></article><article><div><p>访客档案</p><strong>{{ report.totalVisitors }}<small>人</small></strong></div><span>{{ report.unverifiedVisitors }} 人待核验</span></article><article><div><p>待处理风险</p><strong>{{ report.openAlerts }}<small>条</small></strong></div><span>共 {{ report.totalAlerts }} 条记录</span></article><article><div><p>可用资源</p><strong>{{ report.availableResources }}<small>/ {{ report.totalResources }}</small></strong></div><span>接待人、区域与门禁</span></article></section>
            <section class="report-grid"><article class="panel"><div class="panel-head"><div><h2>预约状态分布</h2><p>用于识别审批积压和未离场记录</p></div></div><div class="status-bars"><div v-for="(value,key) in report.appointmentStatus" :key="key"><span>{{ key }}</span><i><b :style="{width:`${Math.max(6, value / Math.max(1,report.totalAppointments) * 100)}%`}"></b></i><strong>{{ value }}</strong></div></div></article><article class="panel risk-card"><div class="panel-head"><div><h2>档案风险</h2><p>需要前台或安保继续处理</p></div></div><strong>{{ report.blacklistedVisitors }}</strong><span>黑名单访客</span><strong>{{ report.unverifiedVisitors }}</strong><span>身份待核验</span></article></section>
            <section class="panel audit-panel"><div class="panel-head"><div><h2>最近操作日志</h2><p>记录预约、核验、资源、预警和设置变更</p></div><span class="subtle-tag">最近 100 条</span></div><div class="data-table audit-table"><div class="row head"><span>时间</span><span>模块 / 操作</span><span>业务编号</span><span>操作人</span><span>说明</span></div><div class="row" v-for="item in auditLogs" :key="item.id"><span>{{ item.occurredAt?.replace('T',' ').slice(0,16) }}</span><span><b>{{ item.module }}</b><small>{{ item.action }}</small></span><span class="mono">{{ item.businessNo }}</span><span>{{ item.operatorName }}</span><span>{{ item.detail }}</span></div><div v-if="!auditLogs.length" class="empty-state">完成一次预约或资源操作后，审计记录将在这里展示。</div></div></section>
          </template>

          <template v-else>
            <section class="page-title"><div><p class="eyebrow">SYSTEM SETTINGS</p><h1>基础设置</h1><p>配置园区、审批、通行、通知和数据留存规则</p></div><button class="primary" @click="saveSettings">保存设置</button></section>
            <section class="settings-layout"><article class="panel settings-card"><div class="panel-head"><div><h2>园区与审批</h2><p>决定预约进入园区前的审批方式</p></div></div><label>园区名称<input v-model="settings.siteName" /></label><label>审批模式<select v-model="settings.approvalMode"><option>接待人审批 + 安保复核</option><option>仅接待人审批</option><option>管理员统一审批</option></select></label><label>通行码有效范围<select v-model="settings.passValidity"><option>预约时段前后 30 分钟</option><option>仅预约时段内</option><option>当日有效</option></select></label><label>单时段容量上限（人）<input v-model="settings.slotCapacity" type="number" min="1" max="10000" /></label><label>审批 SLA（小时）<input v-model="settings.approvalSlaHours" type="number" min="1" max="72" /></label></article><article class="panel settings-card"><div class="panel-head"><div><h2>数据与通知</h2><p>演示版采用站内消息，外部渠道预留对接</p></div></div><label>访客记录保留天数<input v-model="settings.retentionDays" type="number" min="30" /></label><label>默认通知渠道<select v-model="settings.notificationChannel"><option>站内消息</option><option>企业微信（预留）</option><option>短信（预留）</option></select></label><div class="setting-hint"><b>安全提示</b><p>生产环境请更换默认账号密码，并按隐私政策配置证件、照片和访问记录的保存期限。</p></div></article></section>
            <section class="panel account-panel"><div class="panel-head"><div><h2>演示账号与权限</h2><p>管理员负责配置与风控，运营人员负责日常预约协同</p></div></div><div class="account-row"><span class="avatar">管</span><div><b>admin</b><small>系统管理员 · 全部模块</small></div><i>已启用</i></div><div class="account-row"><span class="avatar operator">运</span><div><b>operator</b><small>运营人员 · 预约、访客、预警处置</small></div><i>已启用</i></div></section>
          </template>
        </div>
      </main>
    </template>

    <div v-else class="mobile-stage">
      <div class="mobile-tools"><button @click="mode='admin'">← 返回管理端</button><span>移动工作台交互预览</span></div>
      <div class="phone">
        <div class="phone-status"><span>09:41</span><span>5G&nbsp;&nbsp;87%</span></div>
        <template v-if="mobileView==='首页'">
          <header class="mobile-head"><div><small>{{ settings.siteName }}</small><h1>上午好，王诚</h1></div><span class="avatar">王</span></header>
          <section class="mobile-hero"><span>今日接待任务</span><strong>9<small>位访客</small></strong><p>3 项预约待审批，下一位访客预计 10:30 到达。</p></section>
          <section class="mobile-stats"><div><b>3</b><span>待我审批</span></div><div><b>2</b><span>接待提醒</span></div><div><b>1</b><span>风险关注</span></div></section>
          <section class="mobile-section"><h2>快捷服务</h2><div class="quick"><button @click="mobileView='预约'"><i>预</i><span>发起预约</span></button><button @click="mobileView='签到'"><i>签</i><span>访客签到</span></button><button @click="mobileView='通行码'"><i>码</i><span>通行码</span></button><button @click="modal='exception'"><i>报</i><span>异常上报</span></button></div></section>
          <section class="mobile-section"><div class="mobile-title"><h2>待办事项</h2><a @click="mobileView='事项'">查看全部</a></div><article class="mobile-task" v-for="item in appointments.filter(v=>['待审批','已审批','已到访'].includes(v.status)).slice(0,3)" :key="item.id" @click="selected=item;mobileView='详情'"><i :class="item.riskLevel"></i><div><b>{{ item.visitorName }} · {{ item.purpose }}</b><span>{{ item.timeSlot }} / {{ item.accessArea }}</span></div><em>{{ item.status }}</em></article></section>
        </template>
        <template v-else-if="mobileView==='预约'">
          <header class="mobile-page-head"><button @click="mobileView='首页'">←</button><h1>发起访客预约</h1><span></span></header><section class="mobile-form"><label>访客姓名<input v-model="appointmentForm.visitorName" placeholder="请输入姓名" /></label><label>访客手机<input v-model="appointmentForm.visitorPhone" placeholder="用于到访核验" /></label><label>访客单位<input v-model="appointmentForm.visitorCompany" placeholder="公司或组织名称" /></label><label>接待人<input v-model="appointmentForm.hostName" placeholder="内部接待人" /></label><label>来访事由<input v-model="appointmentForm.purpose" placeholder="请说明来访目的" /></label><div class="form-pair"><label>到访日期<input v-model="appointmentForm.visitDate" type="date" /></label><label>人数<input v-model="appointmentForm.visitorCount" type="number" /></label></div><label>预约园区<select v-model="appointmentForm.siteCode"><option v-for="item in sites.filter(v=>v.status==='启用')" :key="item.siteCode" :value="item.siteCode">{{ item.siteName }}</option></select></label><label>访问区域<select v-model="appointmentForm.accessArea"><option>A座会议中心</option><option>A座会客区</option><option>B座工程区</option><option>受限区-数据中心</option></select></label><button class="mobile-primary" @click="createAppointment();mobileView='首页'">提交预约</button></section>
        </template>
        <template v-else-if="mobileView==='签到'">
          <header class="mobile-page-head"><button @click="mobileView='首页'">←</button><h1>访客签到</h1><span></span></header><section class="scan-card"><div class="scan-frame"><span></span><b>请扫描访客预约码</b></div><p>或由前台输入预约编号 / 六位通行码完成核验</p><input v-model="passCredential" placeholder="输入预约编号或六位通行码" /><button class="mobile-primary" @click="verifyCredential">查询预约</button><div v-if="passVerification" class="verify-result" :class="{ invalid:!passVerification.valid }"><b>{{ passVerification.valid ? '凭证有效' : '核验未通过' }}</b><p>{{ passVerification.message }}</p><span v-if="passVerification.appointment">{{ passVerification.appointment.visitorName }} · {{ passVerification.appointment.accessArea }}</span></div></section><section class="mobile-tip"><b>签到核验要求</b><p>请核对访客姓名、接待人、访问区域和有效时段。受限区域还需安保人员复核。</p></section>
        </template>
        <template v-else-if="mobileView==='通行码'">
          <header class="mobile-page-head"><button @click="mobileView='首页'">←</button><h1>我的访客通行码</h1><span></span></header><section class="pass-card"><div class="pass-top"><span>VISITOR PASS</span><i>{{ currentPass?.status }}</i></div><div class="qr-demo"><span v-for="n in 49" :key="n" :class="{ on:[1,2,3,5,7,8,9,10,12,14,16,18,20,21,22,25,28,30,31,34,36,37,39,41,43,45,47,48,49].includes(n) }"></span></div><strong>{{ currentPass?.passCode || '482916' }}</strong><p>{{ currentPass?.visitorName }} · {{ currentPass?.accessArea }}</p><dl><div><dt>有效日期</dt><dd>{{ currentPass?.visitDate }}</dd></div><div><dt>有效时段</dt><dd>{{ currentPass?.timeSlot }}</dd></div><div><dt>接待人</dt><dd>{{ currentPass?.hostName }}</dd></div></dl></section><p class="pass-note">通行码仅限本人使用，请勿截屏转发。离场后权限自动回收。</p>
        </template>
        <template v-else>
          <header class="mobile-page-head"><button @click="mobileView='首页'">←</button><h1>{{ mobileView==='详情' ? '预约详情' : '全部事项' }}</h1><span></span></header><section class="mobile-section list-page"><article class="mobile-task" v-for="item in (mobileView==='详情' ? [selected] : appointments)" :key="item.id"><i :class="item.riskLevel"></i><div><b>{{ item.visitorName }} · {{ item.purpose }}</b><span>{{ item.visitDate }} {{ item.timeSlot }}</span><span>{{ item.accessArea }} / 接待人 {{ item.hostName }}</span></div><em>{{ item.status }}</em></article><div v-if="mobileView==='详情' && selected" class="mobile-actions"><button v-if="selected.status==='待审批'" @click="runAction(selected,'REJECT');mobileView='首页'">驳回</button><button v-if="selected.status==='待审批'" class="mobile-primary" @click="runAction(selected,'APPROVE');mobileView='首页'">通过审批</button><button v-if="selected.status==='已审批'" class="mobile-primary" @click="runAction(selected,'CHECK_IN');mobileView='首页'">确认签到</button><button v-if="selected.status==='已到访'" class="mobile-primary" @click="runAction(selected,'CHECK_OUT');mobileView='首页'">确认离场</button></div></section>
        </template>
        <footer class="mobile-nav"><button :class="{on:mobileView==='首页'}" @click="mobileView='首页'"><i>⌂</i>首页</button><button :class="{on:mobileView==='事项'}" @click="mobileView='事项'"><i>□</i>事项</button><button @click="announce('当前有 2 条接待提醒')"><i>◦</i>消息</button><button @click="announce('当前登录：王诚（接待人）')"><i>人</i>我的</button></footer>
      </div>
    </div>

    <div v-if="modal" class="modal-mask" @click.self="modal=''">
      <section v-if="modal==='detail' && selected" class="modal detail-modal"><div class="modal-head"><div><small>预约详情</small><h2>{{ selected.appointmentNo }}</h2></div><button @click="modal=''">×</button></div><div class="detail-status"><i class="status" :class="selected.status">{{ selected.status }}</i><span>风险等级：<b>{{ selected.riskLevel }}</b></span><span>园区：<b>{{ selected.siteCode }}</b></span></div><dl class="detail-grid"><div><dt>访客</dt><dd>{{ selected.visitorName }} · {{ selected.visitorCount }} 人</dd><small>{{ selected.visitorCompany }} / {{ maskPhone(selected.visitorPhone) }}</small></div><div><dt>接待人</dt><dd>{{ selected.hostName }}</dd><small>{{ selected.accessArea }}</small></div><div><dt>来访安排</dt><dd>{{ selected.visitDate }}</dd><small>{{ selected.timeSlot }}</small></div><div><dt>来访事由</dt><dd>{{ selected.purpose }}</dd><small v-if="selected.passCode">通行码 {{ selected.passCode }}</small></div></dl><div class="flow-line"><span class="done">提交预约</span><span :class="{done:['已审批','已到访','已离场'].includes(selected.status)}">接待审批</span><span :class="{done:['已到访','已离场'].includes(selected.status)}">到访签到</span><span :class="{done:selected.status==='已离场'}">离场确认</span></div><div class="modal-actions"><button class="danger-ghost" v-if="!['已离场','已取消'].includes(selected.status)" @click="runAction(selected,'CANCEL')">取消预约</button><button v-if="selected.riskLevel==='关注'" class="outline" @click="openCompliance(selected)">材料合规</button><span></span><button v-if="selected.status==='待审批'" class="outline" @click="runAction(selected,'REJECT')">驳回</button><button v-if="selected.status==='待审批'" class="primary" @click="runAction(selected,'APPROVE')">通过并发码</button><button v-if="selected.status==='已审批'" class="primary" @click="runAction(selected,'CHECK_IN')">确认签到</button><button v-if="selected.status==='已到访'" class="primary" @click="runAction(selected,'CHECK_OUT')">确认离场</button></div></section>
      <section v-else-if="modal==='compliance' && selected" class="modal compliance-modal"><div class="modal-head"><div><small>COMPLIANCE GATE</small><h2>预约材料与准入校验</h2></div><button @click="modal=''">×</button></div><div class="compliance-result" :class="{ ready:complianceAssessment?.ready }"><strong>{{ complianceAssessment?.ready ? '合规校验通过' : '存在准入阻断项' }}</strong><p>{{ complianceAssessment?.conclusion }}</p><ul v-if="complianceAssessment?.blockers?.length"><li v-for="item in complianceAssessment.blockers" :key="item">{{ item }}</li></ul></div><div class="document-actions"><span>快捷提交所需材料</span><button v-for="type in complianceAssessment?.requiredDocuments || []" :key="type" class="outline" :disabled="appointmentDocuments.some(v=>v.documentType===type && v.status!=='已驳回')" @click="submitComplianceDocument(type)">＋ {{ appointmentDocuments.some(v=>v.documentType===type && v.status==='已驳回') ? '重新提交' : '' }}{{ type }}</button></div><div class="document-list"><div v-for="item in appointmentDocuments" :key="item.id"><span><b>{{ item.documentType }}</b><small>{{ item.fileName }} · SHA-256 {{ item.checksum.slice(0,10) }}…</small></span><i class="status" :class="item.status">{{ item.status }}</i><span class="review-actions"><button v-if="item.status==='待审核'" @click="reviewComplianceDocument(item,false)">驳回</button><button v-if="item.status==='待审核'" class="primary" @click="reviewComplianceDocument(item,true)">通过</button><small v-else>{{ item.reviewedBy || '-' }} · {{ item.reviewComment || '-' }}</small></span></div></div></section>
      <section v-else-if="modal==='site'" class="modal form-modal"><div class="modal-head"><div><small>ENTERPRISE SITE</small><h2>新增企业园区</h2></div><button @click="modal=''">×</button></div><div class="form-grid"><label>园区编码<input v-model="siteForm.siteCode" placeholder="例如 BJ-HQ" /></label><label>园区名称<input v-model="siteForm.siteName" placeholder="例如 北京运营中心" /></label><label class="full">园区地址<input v-model="siteForm.address" placeholder="办公园区地址" /></label><label>时区<input v-model="siteForm.timezone" /></label><label>单时段容量<input v-model="siteForm.slotCapacity" type="number" min="1" max="10000" /></label><label class="full">应急集合点<input v-model="siteForm.assemblyPoint" placeholder="例如 园区南广场 A 集合点" /></label></div><div class="modal-actions"><span></span><button class="outline" @click="modal=''">取消</button><button class="primary" @click="saveSite">保存园区</button></div></section>
      <section v-else-if="modal==='credential'" class="modal form-modal"><div class="modal-head"><div><small>CONTRACTOR CREDENTIAL</small><h2>登记承包商资质</h2></div><button @click="modal=''">×</button></div><div class="form-grid"><label class="full">承包商名称<input v-model="credentialForm.companyName" placeholder="必须与预约来访单位一致" /></label><label>资质类型<input v-model="credentialForm.credentialType" /></label><label>证书编号<input v-model="credentialForm.credentialNo" /></label><label>有效期<input v-model="credentialForm.validUntil" type="date" /></label><label>安全培训<select v-model="credentialForm.safetyTrainingCompleted"><option :value="true">已完成</option><option :value="false">未完成</option></select></label></div><div class="form-note">施工、维保和承包作业在最终安保复核时会校验企业名称、证书有效期、冻结状态和安全培训。</div><div class="modal-actions"><span></span><button class="outline" @click="modal=''">取消</button><button class="primary" @click="saveCredential">保存资质</button></div></section>
      <section v-else-if="modal==='resource'" class="modal form-modal"><div class="modal-head"><div><small>SITE RESOURCE</small><h2>{{ resourceForm.id ? '编辑园区资源' : '新增园区资源' }}</h2></div><button @click="modal=''">×</button></div><div class="form-grid"><label>资源类型<select v-model="resourceForm.type"><option>接待人</option><option>访问区域</option><option>门禁点</option></select></label><label>资源编码<input v-model="resourceForm.code" :disabled="Boolean(resourceForm.id)" placeholder="例如 HOST-003" /></label><label>资源名称<input v-model="resourceForm.name" placeholder="资源显示名称" /></label><label>责任部门<input v-model="resourceForm.department" placeholder="责任部门或团队" /></label><label>运行状态<select v-model="resourceForm.status"><option>启用</option><option>停用</option><option>开放</option><option>审批开放</option><option>在线</option><option>离线</option></select></label></div><div class="modal-actions"><span></span><button class="outline" @click="modal=''">取消</button><button class="primary" @click="saveResource">保存资源</button></div></section>
      <section v-else-if="modal==='appointment'" class="modal form-modal"><div class="modal-head"><div><small>NEW APPOINTMENT</small><h2>新建访客预约</h2></div><button @click="modal=''">×</button></div><div class="form-grid"><label>访客姓名<input v-model="appointmentForm.visitorName" placeholder="请输入姓名" /></label><label>手机号码<input v-model="appointmentForm.visitorPhone" placeholder="用于签到核验" /></label><label>访客单位<input v-model="appointmentForm.visitorCompany" placeholder="公司或组织名称" /></label><label>接待人<input v-model="appointmentForm.hostName" placeholder="内部接待人员" /></label><label class="full">来访事由<input v-model="appointmentForm.purpose" placeholder="请准确填写来访目的" /></label><label>预约园区<select v-model="appointmentForm.siteCode"><option v-for="item in sites.filter(v=>v.status==='启用')" :key="item.siteCode" :value="item.siteCode">{{ item.siteName }} · {{ item.siteCode }}</option></select></label><label>到访日期<input v-model="appointmentForm.visitDate" type="date" /></label><label>预约时段<select v-model="appointmentForm.timeSlot"><option>09:00-11:00</option><option>10:00-11:30</option><option>14:00-16:00</option><option>09:00-18:00</option></select></label><label>访问区域<select v-model="appointmentForm.accessArea"><option>A座会议中心</option><option>A座会客区</option><option>B座工程区</option><option>受限区-数据中心</option></select></label><label>来访人数<input v-model="appointmentForm.visitorCount" type="number" min="1" max="200" /></label></div><div class="form-note">受限区域、施工维保或多人来访会自动进入风险复核；最终安保放行前必须完成所需材料与承包商资质校验。</div><div class="modal-actions"><span></span><button class="outline" @click="modal=''">取消</button><button class="primary" @click="createAppointment">提交预约</button></div></section>
      <section v-else class="modal form-modal"><div class="modal-head"><div><small>INCIDENT REPORT</small><h2>上报现场异常</h2></div><button @click="modal=''">×</button></div><div class="form-grid"><label>异常类型<select v-model="exceptionForm.type"><option>现场异常</option><option>超时未离场</option><option>通行异常</option><option>证件异常</option></select></label><label>风险等级<select v-model="exceptionForm.level"><option>低</option><option>中</option><option>高</option></select></label><label class="full">异常说明<input v-model="exceptionForm.title" placeholder="说明时间、地点和具体情况" /></label><label>关联预约<input v-model="exceptionForm.relatedNo" placeholder="VMS-..." /></label><label>处理责任人<input v-model="exceptionForm.assignee" /></label></div><div class="modal-actions"><span></span><button class="outline" @click="modal=''">取消</button><button class="primary" @click="reportException">确认上报</button></div></section>
    </div>
    <div v-if="toast" class="toast"><span>✓</span>{{ toast }}</div>
  </div>
</template>
