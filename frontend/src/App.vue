<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ -->
<script setup>
import { ref } from 'vue'
import { domain } from './domain'

const mode = ref('admin')
const active = ref('运营总览')
const nav = ['运营总览', '业务协同', '资源中心', '风险预警', '基础设置']
</script>

<template>
  <div class="app" :style="{ '--accent': domain.accent, '--accent-soft': domain.accentSoft }">
    <template v-if="mode === 'admin'">
      <aside class="sidebar">
        <div class="brand"><span class="mark">ZH</span><div><b>{{ domain.shortName }}</b><small>{{ domain.product }}</small></div></div>
        <nav><button v-for="item in nav" :key="item" :class="{ active: active === item }" @click="active = item"><span class="nav-dot"></span>{{ item }}</button></nav>
        <div class="company"><span>社区源码版</span><p>上海如静知华信息科技有限公司</p></div>
      </aside>
      <main class="main">
        <header class="topbar">
          <div><span class="crumb">运营中心 /</span> {{ active }}</div>
          <div class="top-actions"><button class="mode-button" @click="mode='mobile'">查看移动工作台</button><span class="bell">●</span><span class="avatar">管</span></div>
        </header>
        <div class="content">
          <section class="page-title"><div><h1>{{ domain.greeting }}</h1><p>{{ domain.scene }} · 数据更新于 09:36</p></div><button class="primary">＋ 新建协同事项</button></section>
          <section class="notice"><span>运营提示</span><p>{{ domain.notice }}</p><a>查看详情 →</a></section>
          <section class="metrics">
            <article v-for="metric in domain.metrics" :key="metric.label"><p>{{ metric.label }}</p><strong>{{ metric.value }} <small>{{ metric.unit }}</small></strong><span>{{ metric.trend }}</span></article>
          </section>
          <section class="workspace-grid">
            <article class="panel flow-panel">
              <div class="panel-head"><div><h2>业务运行态势</h2><p>核心流程今日完成进度</p></div><button>今日⌄</button></div>
              <div class="bars"><div v-for="stage in domain.stages" :key="stage.name"><span>{{ stage.name }}</span><div><i :style="{ width: stage.value + '%' }"></i></div><b>{{ stage.value }}%</b></div></div>
            </article>
            <article class="panel risk-panel">
              <div class="panel-head"><div><h2>运行健康度</h2><p>基于容量、时效与数据质量</p></div><span class="tag">实时</span></div>
              <div class="score"><strong>86</strong><span>运行良好</span></div>
              <ul><li><span>服务容量</span><b>91%</b></li><li><span>流程时效</span><b>82%</b></li><li><span>数据完整度</span><b>88%</b></li></ul>
            </article>
          </section>
          <section class="panel task-panel">
            <div class="panel-head"><div><h2>重点协同事项</h2><p>需要运营团队持续跟踪的工作</p></div><button>全部事项 →</button></div>
            <div class="table"><div class="tr head"><span>编号</span><span>事项</span><span>责任团队</span><span>优先级</span><span>状态</span></div><div class="tr" v-for="task in domain.tasks" :key="task.no"><span class="mono">{{ task.no }}</span><strong>{{ task.title }}</strong><span>{{ task.owner }}</span><span><i class="priority" :class="task.priority">{{ task.priority }}</i></span><span><i class="status">{{ task.status }}</i></span></div></div>
          </section>
        </div>
      </main>
    </template>

    <div v-else class="mobile-stage">
      <div class="mobile-controls"><button @click="mode='admin'">← 返回管理端</button><span>移动工作台预览</span></div>
      <div class="phone">
        <div class="phone-status"><span>09:41</span><span>5G 87%</span></div>
        <div class="mobile-head"><div><small>{{ domain.shortName }}</small><h1>{{ domain.greeting.replace('，运营中心','') }}</h1></div><span class="avatar">运</span></div>
        <div class="mobile-card"><small>今日运营</small><strong>{{ domain.metrics[0].value }}<em>{{ domain.metrics[0].unit }}</em></strong><p>{{ domain.notice }}</p></div>
        <div class="mobile-stats"><div v-for="item in domain.mobileStats" :key="item.label"><strong>{{ item.value }}</strong><span>{{ item.label }}</span></div></div>
        <section class="mobile-section"><h2>快捷工作</h2><div class="quick"><button v-for="(item,index) in domain.mobileActions" :key="item"><i>{{ ['协','路','交','报'][index] }}</i><span>{{ item }}</span></button></div></section>
        <section class="mobile-section"><div class="mobile-title"><h2>我的重点事项</h2><a>全部</a></div><article class="mobile-task" v-for="task in domain.tasks.slice(0,3)" :key="task.no"><i></i><div><strong>{{ task.title }}</strong><span>{{ task.owner }} · {{ task.no }}</span></div><em>{{ task.status }}</em></article></section>
        <footer><button class="on">首页</button><button>事项</button><button>消息</button><button>我的</button></footer>
      </div>
    </div>
  </div>
</template>
