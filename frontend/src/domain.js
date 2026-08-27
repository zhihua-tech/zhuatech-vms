/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
export const domain = {
  code: 'VMS', product: '访客预约与通行管理平台', shortName: '知华 VMS',
  company: '上海如静知华信息科技有限公司', website: 'https://www.zhuatech.cn/',
  accent: '#176b57', accentSoft: '#e7f3ef',
  appointments: [
    { id: 1, appointmentNo: 'VMS-20260826-101', visitorName: '陈伟', visitorCompany: '启明设备服务有限公司', visitorPhone: '13800001231', hostName: '周敏', purpose: '数据中心空调维保', visitDate: '2026-08-26', timeSlot: '14:00-16:00', accessArea: '受限区-数据中心', visitorCount: 3, status: '待审批', riskLevel: '关注', passCode: null },
    { id: 2, appointmentNo: 'VMS-20260826-102', visitorName: '林悦', visitorCompany: '远见咨询', visitorPhone: '13900004562', hostName: '王诚', purpose: '项目方案交流', visitDate: '2026-08-26', timeSlot: '10:00-11:30', accessArea: 'A座会议中心', visitorCount: 5, status: '已审批', riskLevel: '正常', passCode: '482916' },
    { id: 3, appointmentNo: 'VMS-20260826-103', visitorName: '赵磊', visitorCompany: '城运工程', visitorPhone: '13600007894', hostName: '徐亮', purpose: '弱电施工复检', visitDate: '2026-08-26', timeSlot: '09:00-18:00', accessArea: 'B座工程区', visitorCount: 8, status: '已到访', riskLevel: '关注', passCode: '306528' },
    { id: 4, appointmentNo: 'VMS-20260825-088', visitorName: '孙静', visitorCompany: '融科伙伴', visitorPhone: '13700002216', hostName: '吴晓', purpose: '合作洽谈', visitDate: '2026-08-25', timeSlot: '15:00-16:30', accessArea: 'A座会客区', visitorCount: 2, status: '已离场', riskLevel: '正常', passCode: '710463' }
  ],
  visitors: [
    { id: 1, visitorName: '陈伟', company: '启明设备服务有限公司', phone: '13800001231', identityVerified: true, blacklisted: false, visitCount: 6, lastVisitAt: '2026-08-08T16:20:00', note: '设备维保服务商' },
    { id: 2, visitorName: '林悦', company: '远见咨询', phone: '13900004562', identityVerified: true, blacklisted: false, visitCount: 3, lastVisitAt: '2026-08-19T10:15:00', note: '常规商务来访' },
    { id: 3, visitorName: '赵磊', company: '城运工程', phone: '13600007894', identityVerified: true, blacklisted: false, visitCount: 11, lastVisitAt: '2026-08-26T09:02:00', note: '施工人员，需佩戴访客证' },
    { id: 4, visitorName: '高峰', company: '个人', phone: '13500001999', identityVerified: false, blacklisted: true, visitCount: 1, lastVisitAt: '2026-06-12T13:10:00', note: '证件信息不一致，待安保复核' }
  ],
  resources: [
    { id: 1, type: '接待人', code: 'HOST-001', name: '周敏', department: '信息技术部', status: '启用' },
    { id: 2, type: '接待人', code: 'HOST-002', name: '王诚', department: '行政管理部', status: '启用' },
    { id: 3, type: '访问区域', code: 'AREA-A01', name: 'A座会议中心', department: '行政管理部', status: '开放' },
    { id: 4, type: '访问区域', code: 'AREA-D01', name: '受限区-数据中心', department: '信息技术部', status: '审批开放' },
    { id: 5, type: '门禁点', code: 'GATE-01', name: '园区北门访客闸机', department: '安保中心', status: '在线' },
    { id: 6, type: '门禁点', code: 'GATE-02', name: 'A座一层前台', department: '安保中心', status: '在线' }
  ],
  alerts: [
    { id: 1, alertNo: 'ALT-20260826-01', type: '超时未离场', level: '高', title: '施工访客已超预约时段 25 分钟', relatedNo: 'VMS-20260826-103', status: '待处理', assignee: '园区安保中心', createdAt: '2026-08-26T18:25:00' },
    { id: 2, alertNo: 'ALT-20260826-02', type: '资料待补充', level: '中', title: '设备维保预约缺少施工人员附件', relatedNo: 'VMS-20260826-101', status: '待处理', assignee: '行政接待组', createdAt: '2026-08-26T09:18:00' },
    { id: 3, alertNo: 'ALT-20260825-06', type: '通行异常', level: '低', title: '访客在未授权门禁点尝试刷码', relatedNo: 'VMS-20260825-088', status: '已处理', assignee: '园区安保中心', createdAt: '2026-08-25T16:08:00', resolution: '已核对路线并由接待人陪同' }
  ],
  settings: { siteName: '上海创新园区', approvalMode: '接待人审批 + 安保复核', passValidity: '预约时段前后 30 分钟', retentionDays: '180', notificationChannel: '站内消息', slotCapacity: '100', approvalSlaHours: '4' }
}
