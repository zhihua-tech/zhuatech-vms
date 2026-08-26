#!/usr/bin/env python3
"""生成知华科技 ZhuaTech VMS 面向最终用户的 PDF 产品使用手册。

Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
"""
from pathlib import Path
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (BaseDocTemplate, Frame, Image, KeepTogether, PageBreak,
                               PageTemplate, Paragraph, Spacer, Table, TableStyle)

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "output" / "pdf" / "知华科技-ZhuaTech-VMS-产品使用手册-V1.2.pdf"
IMG = ROOT / "docs" / "images"

GREEN = colors.HexColor("#176B57")
DARK = colors.HexColor("#173C33")
MINT = colors.HexColor("#E7F3EF")
PALE = colors.HexColor("#F3F7F5")
GOLD = colors.HexColor("#D5A044")
RED = colors.HexColor("#A64235")
INK = colors.HexColor("#18221F")
MUTED = colors.HexColor("#65736E")
LINE = colors.HexColor("#DCE4E0")

FONT_FILE = "/System/Library/Fonts/Supplemental/Arial Unicode.ttf"
pdfmetrics.registerFont(TTFont("ZHCN", FONT_FILE))


class ManualDoc(BaseDocTemplate):
    def afterFlowable(self, flowable):
        if isinstance(flowable, Paragraph):
            style = flowable.style.name
            if style in ("H1", "H2"):
                level = 0 if style == "H1" else 1
                text = flowable.getPlainText()
                key = "bk-" + str(abs(hash((text, self.page))))
                self.canv.bookmarkPage(key)
                self.canv.addOutlineEntry(text, key, level=level, closed=False)


def styles():
    base = getSampleStyleSheet()
    return {
        "body": ParagraphStyle("BodyCN", parent=base["BodyText"], fontName="ZHCN", fontSize=9.6,
            leading=15, textColor=INK, spaceAfter=6),
        "small": ParagraphStyle("SmallCN", fontName="ZHCN", fontSize=8, leading=12,
            textColor=MUTED, spaceAfter=4),
        "kicker": ParagraphStyle("Kicker", fontName="ZHCN", fontSize=7.8, leading=10,
            textColor=GREEN, spaceAfter=4, tracking=1.2),
        "h1": ParagraphStyle("H1", fontName="ZHCN", fontSize=17, leading=23,
            textColor=DARK, spaceBefore=4, spaceAfter=10),
        "h2": ParagraphStyle("H2", fontName="ZHCN", fontSize=12.2, leading=17,
            textColor=GREEN, spaceBefore=8, spaceAfter=6),
        "coverbrand": ParagraphStyle("CoverBrand", fontName="ZHCN", fontSize=15, leading=20,
            textColor=DARK, alignment=TA_CENTER, spaceAfter=8),
        "covertitle": ParagraphStyle("CoverTitle", fontName="ZHCN", fontSize=29, leading=35,
            textColor=DARK, alignment=TA_CENTER, spaceAfter=6),
        "coversub": ParagraphStyle("CoverSub", fontName="ZHCN", fontSize=17, leading=23,
            textColor=GREEN, alignment=TA_CENTER, spaceAfter=10),
        "center": ParagraphStyle("Center", fontName="ZHCN", fontSize=9, leading=14,
            textColor=MUTED, alignment=TA_CENTER, spaceAfter=4),
        "caption": ParagraphStyle("CaptionCN", fontName="ZHCN", fontSize=7.7, leading=11,
            textColor=MUTED, alignment=TA_CENTER, spaceBefore=3, spaceAfter=8),
        "bullet": ParagraphStyle("BulletCN", fontName="ZHCN", fontSize=9.4, leading=14,
            leftIndent=14, firstLineIndent=-9, bulletIndent=2, textColor=INK, spaceAfter=4),
        "cell": ParagraphStyle("CellCN", fontName="ZHCN", fontSize=8, leading=11,
            textColor=INK),
        "cellhead": ParagraphStyle("CellHeadCN", fontName="ZHCN", fontSize=8, leading=11,
            textColor=colors.white),
        "calltitle": ParagraphStyle("CallTitle", fontName="ZHCN", fontSize=9.2, leading=13,
            textColor=GREEN, spaceAfter=2),
        "callbody": ParagraphStyle("CallBody", fontName="ZHCN", fontSize=8.5, leading=13,
            textColor=INK),
    }


S = styles()


def header_footer(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(LINE)
    canvas.setLineWidth(.4)
    canvas.line(doc.leftMargin, letter[1] - .52 * inch, letter[0] - doc.rightMargin, letter[1] - .52 * inch)
    canvas.setFont("ZHCN", 7.5)
    canvas.setFillColor(MUTED)
    canvas.drawString(doc.leftMargin, letter[1] - .43 * inch, "知华科技 · ZhuaTech VMS 产品使用手册")
    canvas.drawRightString(letter[0] - doc.rightMargin, .42 * inch, f"第 {doc.page} 页")
    canvas.restoreState()


def cover_footer(canvas, doc):
    canvas.saveState()
    canvas.setFillColor(GREEN)
    canvas.rect(0, 0, letter[0], 8, fill=1, stroke=0)
    canvas.setFont("ZHCN", 7.5)
    canvas.setFillColor(MUTED)
    canvas.drawCentredString(letter[0] / 2, .42 * inch, "上海如静知华信息科技有限公司 · https://www.zhuatech.cn/")
    canvas.restoreState()


def P(text, style="body"):
    return Paragraph(text, S[style])


def page(title, kicker=None):
    out = [PageBreak()]
    if kicker:
        out.append(P(kicker.upper(), "kicker"))
    out.append(P(title, "h1"))
    return out


def callout(title, body, fill=MINT, accent=GREEN):
    box = Table([[P(title, "calltitle")], [P(body, "callbody")]], colWidths=[6.45 * inch])
    box.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), fill), ("BOX", (0, 0), (-1, -1), .5, accent),
        ("LINEBEFORE", (0, 0), (0, -1), 3, accent), ("LEFTPADDING", (0, 0), (-1, -1), 12),
        ("RIGHTPADDING", (0, 0), (-1, -1), 12), ("TOPPADDING", (0, 0), (-1, 0), 9),
        ("BOTTOMPADDING", (0, -1), (-1, -1), 9), ("TOPPADDING", (0, 1), (-1, 1), 0),
    ]))
    return KeepTogether([box, Spacer(1, 7)])


def bullet(text):
    return P("•  " + text, "bullet")


def data_table(headers, rows, widths):
    data = [[P(h, "cellhead") for h in headers]]
    data += [[P(str(v), "cell") for v in row] for row in rows]
    t = Table(data, colWidths=[w * inch for w in widths], repeatRows=1, hAlign="LEFT")
    style = [
        ("BACKGROUND", (0, 0), (-1, 0), DARK), ("GRID", (0, 0), (-1, -1), .35, LINE),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"), ("LEFTPADDING", (0, 0), (-1, -1), 7),
        ("RIGHTPADDING", (0, 0), (-1, -1), 7), ("TOPPADDING", (0, 0), (-1, -1), 6),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]
    for i in range(1, len(data)):
        if i % 2 == 0:
            style.append(("BACKGROUND", (0, i), (-1, i), PALE))
    t.setStyle(TableStyle(style))
    return t


def step(n, title, body):
    t = Table([[P(str(n), "cellhead"), P(f"<b>{title}</b><br/><font color='#65736E'>{body}</font>", "cell")]],
              colWidths=[.55 * inch, 5.9 * inch])
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (0, 0), GREEN), ("BACKGROUND", (1, 0), (1, 0), PALE),
        ("ALIGN", (0, 0), (0, 0), "CENTER"), ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("BOX", (0, 0), (-1, -1), .35, LINE), ("LEFTPADDING", (0, 0), (-1, -1), 8),
        ("RIGHTPADDING", (0, 0), (-1, -1), 8), ("TOPPADDING", (0, 0), (-1, -1), 7),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 7),
    ]))
    return KeepTogether([t, Spacer(1, 5)])


def screenshot(filename, caption, width=6.35 * inch):
    path = IMG / filename
    im = Image(str(path))
    ratio = im.imageHeight / im.imageWidth
    im.drawWidth = width
    im.drawHeight = width * ratio
    return KeepTogether([im, P(caption, "caption")])


def build():
    OUT.parent.mkdir(parents=True, exist_ok=True)
    doc = ManualDoc(str(OUT), pagesize=letter, leftMargin=1 * inch, rightMargin=1 * inch,
                    topMargin=.72 * inch, bottomMargin=.68 * inch, title="知华科技 ZhuaTech VMS 产品使用手册 V1.2",
                    author="上海如静知华信息科技有限公司", subject="访客预约与通行管理平台用户手册")
    frame = Frame(doc.leftMargin, doc.bottomMargin, doc.width, doc.height, id="normal")
    doc.addPageTemplates([
        PageTemplate(id="cover", frames=frame, onPage=cover_footer, autoNextPageTemplate="normal"),
        PageTemplate(id="normal", frames=frame, onPage=header_footer),
    ])
    story = []

    # Cover
    story += [Spacer(1, 1.15 * inch), P("ZHUATECH  ·  PRODUCT GUIDE", "center"), Spacer(1, 12),
              P("知华科技", "coverbrand"), P("ZhuaTech VMS", "covertitle"),
              P("访客预约与通行管理平台", "coversub"), P("产品使用手册", "coverbrand"),
              Spacer(1, 25), callout("从预约到离场的可追踪闭环", "适用于园区前台、行政接待、内部接待人、安保人员和系统管理员。本手册以社区源码版 V1.4 实际功能为准。"),
              Spacer(1, 28), P("产品手册 V1.2  ·  软件版本 V1.4  ·  2026 年 8 月", "center"),
              P("上海如静知华信息科技有限公司", "coverbrand"),
              P("<link href='https://www.zhuatech.cn/' color='#176B57'>https://www.zhuatech.cn/</link>", "center")]

    story += page("开始使用前", "READ THIS FIRST")
    story += [P("本手册面向日常使用人员，重点说明每个模块解决什么问题、由谁使用、如何操作，以及不同状态代表什么。技术接口和二次开发细节参见项目 docs/API.md 与 docs/ARCHITECTURE.md。"),
              callout("开源版本使用边界", "本工程仅能用于个人非商业学习、研究和技术交流。未经上海如静知华信息科技有限公司书面授权，不得商用、对外收费部署、SaaS 运营、外包交付或投标。", colors.HexColor("#FFF4E1"), GOLD),
              P("角色与工作入口", "h2"),
              data_table(["角色", "主要入口", "典型工作"], [
                  ["系统管理员", "管理端", "设置、权限、风险规则与运营查看"],
                  ["前台 / 行政", "管理端", "预约、资料核对、签到与离场确认"],
                  ["内部接待人", "移动端", "预约审批、接待提醒、访客跟进"],
                  ["安保人员", "管理端 / 移动端", "身份复核、异常、黑名单和应急清点"],
                  ["访客", "移动端", "提交信息、展示通行码、签到与签退"],
              ], [1.15, 1.25, 4.1]), Spacer(1, 8), P("模块导航", "h2"),
              data_table(["模块", "包含能力"], [
                  ["运营总览", "指标、流程进度、现场状态和最近预约"],
                  ["业务协同", "新建、审批、驳回、签到、通行与离场"],
                  ["资源中心", "访客档案、黑名单、接待人、区域和门禁点"],
                  ["风险预警", "准入判断、异常上报、处置留痕与应急清点"],
                  ["审计报表", "预约结构、档案风险、资源可用率和操作日志"],
                  ["基础设置", "园区、审批、通行、通知、留存和账号权限"],
                  ["移动工作台", "预约、签到、通行码、待办、消息与异常"],
              ], [1.35, 5.15])]

    story += page("1. 来访业务全流程", "END-TO-END FLOW")
    story += [P("ZhuaTech VMS 用一条状态链连接访客、接待人、前台和安保。每次关键操作都会改变预约状态，操作者可据此判断下一步。")]
    for i, (title, body) in enumerate([
        ("提交预约", "填写访客、单位、手机、接待人、到访时间、区域和事由。"),
        ("接待审批", "接待人确认来访目的；受限区域或风险关注记录由安保复核。"),
        ("身份核验", "到访时核对预约编号、证件信息和人员名单。"),
        ("签发通行码", "审批通过后生成一次性数字通行码，绑定区域与有效时段。"),
        ("访客签到", "核验通过后确认入园，状态变为“已到访”。"),
        ("离场确认", "访客离场后签退，系统回收通行权限并保留记录。"),
    ], 1): story.append(step(i, title, body))
    story += [P("状态快速判断", "h2"), data_table(["当前状态", "说明", "下一步"], [
        ["待审批", "已提交，尚未同意", "通过并发码 / 驳回 / 取消"],
        ["已审批", "接待已确认，通行码可用", "身份核验后签到"],
        ["已到访", "访客当前在园", "关注异常，离场时签退"],
        ["已离场", "访问结束，权限已回收", "归档或查询历史"],
        ["已驳回 / 已取消", "预约不再生效", "补充资料后重新发起"],
    ], [1.3, 2.45, 2.75])]

    story += page("2. 运营总览", "MODULE 01")
    story += [P("运营总览帮助管理员、前台和安保快速掌握预约、审批、在园访客和风险处置状态。"),
              screenshot("vms-reception-dashboard.png", "图 2-1  管理端运营总览：指标、流程进度、健康度与最近预约"),
              P("主要信息", "h2"), bullet("今日预约：当前预约总量，用于判断前台接待压力。"),
              bullet("待审批：尚未完成接待确认的预约，应优先处理。"),
              bullet("当前在园：已经签到但尚未离场的访客人数。"),
              bullet("待处理预警：超时、资料缺失、通行异常等未闭环事项。"),
              callout("推荐工作习惯", "每天上班后先查看待审批和预警，下班前核对“当前在园”是否仍有访客未签退。")]

    story += page("3. 业务协同：预约管理", "MODULE 02")
    story += [P("业务协同集中展示预约编号、访客、来访时间、接待人、访问区域、风险等级和当前状态，支持关键词搜索与状态筛选。"),
              screenshot("vms-appointment-workflow.png", "图 3-1  预约与接待协同列表：风险和状态一屏可见"),
              P("新建预约", "h2")]
    for i, (title, body) in enumerate([
        ("进入新建", "管理端右上角和移动端快捷服务均可进入。"),
        ("填写访客资料", "至少填写姓名、手机、单位、接待人和来访事由。"),
        ("安排访问", "选择日期、时段、访问区域和来访人数。"),
        ("提交", "系统生成预约编号，初始状态为“待审批”。"),
    ], 1): story.append(step(i, title, body))
    story.append(callout("风险提醒", "受限区域、多人来访、夜间访问、身份未核验或黑名单命中会提高风险等级。", colors.HexColor("#FCEDE9"), RED))

    story += page("4. 审批、签到、通行与离场", "WORKFLOW ACTIONS")
    story += [P("审批或驳回", "h2"), P("在预约列表点击“处理”。核对访客、接待人、时间、区域和事由后，选择“通过并发码”或“驳回”。审批通过后系统签发六位数字通行码。"),
              data_table(["操作", "允许状态", "结果"], [
                  ["通过并发码", "待审批", "变为已审批，并生成通行码"], ["驳回", "待审批", "变为已驳回，不允许签到"],
                  ["取消预约", "未离场且未取消", "变为已取消，通行码失效"], ["确认签到", "已审批", "变为已到访，计入在园人数"],
                  ["确认离场", "已到访", "变为已离场，回收权限"],
              ], [1.45, 1.65, 3.4]), Spacer(1, 8), P("到访核验", "h2"),
              bullet("输入预约编号或六位通行码，系统返回凭证是否有效及对应访客信息。"),
              bullet("核对访问区域和有效时段；受限区域由安保人员再次确认。"),
              bullet("核验完成后确认签到，不得在未审批状态下强制签到。"),
              P("离场签退", "h2"), P("访客离开园区时确认离场。签退后不再计入在园人数；如超过预约时段仍未签退，应先在风险预警中核对。"),
              callout("系统校验", "后端会校验状态顺序，例如“待审批”不能直接签到，“已审批”不能直接离场。")]

    story += page("5. 资源中心", "MODULE 03")
    story += [P("资源中心分为访客档案和接待人与区域两类。日常人员以查询为主；黑名单和基础资源变更应由授权管理员执行。"),
              screenshot("vms-resource-center.png", "图 5-1  访客档案：身份核验、来访次数与黑名单状态"),
              P("访客档案与黑名单", "h2"),
              bullet("查看单位、脱敏手机、身份核验状态、累计来访次数和最近到访日期。"),
              bullet("前台核验证件原件后可标记身份核验通过；撤销核验也会进入操作日志。"),
              bullet("加入黑名单后，后续预约应拦截或进入人工复核；解除前需要记录原因。"),
              P("接待人、区域与门禁点", "h2"),
              data_table(["资源类型", "用途", "示例状态"], [
                  ["接待人", "绑定内部责任人和部门", "启用"], ["访问区域", "限制访客可进入的空间", "开放 / 审批开放"],
                  ["门禁点", "标记闸机或前台核验点", "在线 / 离线"],
              ], [1.35, 3.7, 1.45]), Spacer(1, 8),
              P("管理员可以新增、编辑和删除资源。资源编码必须唯一；资源停用后应同步检查未结束预约。")]

    story += page("6. 风险预警与异常处置", "MODULE 04")
    story += [P("风险预警集中处理超时未离场、资料待补充、通行异常、证件异常和现场异常。每条记录包含关联预约、等级、责任人和处置状态。"),
              screenshot("vms-risk-alerts.png", "图 6-1  风险预警：待处理、高风险、闭环数和异常明细"),
              P("处置步骤", "h2")]
    for i, (title, body) in enumerate([
        ("判断等级", "高风险优先；中风险应在当班内闭环。"), ("核对预约", "确认访客、接待人、最后状态和区域。"),
        ("线下处置", "联系接待人、前台或安保核实现场。"), ("完成处置", "填写结果并完成，系统保留留痕。"),
    ], 1): story.append(step(i, title, body))
    story.append(callout("应急疏散访客清点", "系统可根据已入场、已离场、集合点签到和接待人确认人数计算未清点访客。", colors.HexColor("#FCEDE9"), RED))

    story += page("7. 运营报表与操作审计", "MODULE 05")
    story += [P("审计报表面向系统管理员，用于查看预约状态结构、访客档案风险、园区资源可用情况及最近 100 条关键操作。"),
              screenshot("vms-audit-report.png", "图 7-1  运营报表与审计日志：指标、状态分布和操作责任人"),
              P("运营报表", "h2"), bullet("预约状态分布用于识别待审批积压、当前在园和未完成离场记录。"),
              bullet("档案风险显示黑名单和身份待核验人数；资源指标显示接待人、区域和门禁可用情况。"),
              P("操作审计", "h2"),
              data_table(["字段", "用户如何理解"], [["模块 / 操作", "预约、核验、资源、预警或设置变更"],
                  ["业务编号", "关联预约、资源或预警编号"], ["操作人", "执行操作的登录账号"],
                  ["说明", "操作结果或填写的业务备注"]], [1.6, 4.9]), Spacer(1, 8),
              callout("审计使用建议", "定期查看黑名单、身份核验、资源删除、预约状态变更和风险处置记录。")]

    story += page("8. 基础设置与权限", "MODULE 06")
    story += [P("基础设置决定审批方式、通行码有效范围、消息渠道和数据留存期限，仅系统管理员可修改；保存后写入数据库，服务重启后仍生效。"),
              screenshot("vms-settings.png", "图 8-1  基础设置：园区审批、数据通知和演示账号"),
              data_table(["设置项", "作用", "默认值"], [
                  ["园区名称", "显示当前管理范围", "上海创新园区"], ["审批模式", "决定预约由谁批准", "接待人审批 + 安保复核"],
                  ["通行码有效范围", "限制可通行时段", "预约前后 30 分钟"], ["记录保留天数", "控制隐私数据保存周期", "180 天"],
                  ["通知渠道", "发送待办与提醒", "站内消息"],
              ], [1.55, 2.7, 2.25]), Spacer(1, 8),
              callout("账号说明", "admin 拥有全部模块权限；operator 可处理预约、访客与预警，但不能访问管理员设置。上线前必须更换默认密码。")]

    story += page("9. 移动工作台", "MOBILE WORKSPACE")
    mobile = Table([[Image(str(IMG / "vms-mobile-workbench.png"), width=2.1*inch, height=4.34*inch),
                     Image(str(IMG / "vms-mobile-pass.png"), width=2.1*inch, height=4.34*inch)],
                    [P("移动工作台首页", "caption"), P("访客通行码", "caption")]], colWidths=[3.25*inch, 3.25*inch])
    mobile.setStyle(TableStyle([("ALIGN", (0,0), (-1,-1), "CENTER"), ("VALIGN", (0,0), (-1,-1), "TOP")]))
    story += [P("移动端面向接待人、前台和访客。首页聚合接待任务、待审批、提醒和风险关注，并提供四个高频入口。"), mobile, P("快捷服务", "h2"),
              data_table(["入口", "用户可以完成"], [
                  ["发起预约", "填写访客、接待、时间、区域和事由"], ["访客签到", "输入预约编号或六位通行码，校验通行凭证"],
                  ["通行码", "查看数字码、有效时间、区域和接待人"], ["异常上报", "记录异常、等级、关联预约和责任人"],
                  ["事项 / 消息", "查看待办、接待提醒和风险通知"],
              ], [1.3, 5.2])]

    story += page("10. 移动端操作说明", "MOBILE PROCEDURES")
    story += [P("接待人审批", "h2"), step(1, "打开待办", "首页点击待办事项或底部“事项”。"),
              step(2, "查看详情", "核对访客、时间、区域、人数和风险提示。"),
              step(3, "作出决定", "选择通过审批、驳回或返回补充资料。"),
              P("展示通行码", "h2"), P("审批通过后进入“通行码”。页面显示六位数字通行码、有效日期、时段、区域和接待人。通行码仅限本人使用，不应截屏转发。"),
              P("异常上报", "h2"), P("选择异常类型和风险等级，说明时间、地点和情况，尽量关联预约编号。提交后由指定责任人处理。"),
              P("到访与离场", "h2"), bullet("到访：前台核验身份和预约后确认签到。"),
              bullet("在园：访客按授权区域和时段通行，接待人持续负责。"),
              bullet("离场：由前台或接待人确认签退，通行权限随即回收。")]

    story += page("11. 准入风险判断", "RISK RULES")
    story += [P("社区源码版内置可解释规则示例，用于学习如何把业务条件转换为准入决定。它不是专业安防产品，不替代人工核验。"),
              data_table(["风险条件", "系统处理建议"], [
                  ["黑名单命中", "直接 REJECT，停止预约并由安保复核"], ["夜间访问", "提高风险分，配置夜间时段并通知值班安保"],
                  ["受限区域", "提高风险分，限定门禁并要求陪同"], ["接待人未确认", "进入人工复核，联系内部接待人"],
                  ["身份未核验", "进入人工复核，到访前完成证件核验"], ["普通预约且资料完整", "APPROVE，按时段签发一次性通行码"],
              ], [1.8, 4.7]), Spacer(1, 10), P("输出结果", "h2"),
              data_table(["决定", "含义"], [["APPROVE", "风险较低，可以继续"], ["MANUAL_REVIEW", "需要接待人或安保人工复核"], ["REJECT", "高风险或黑名单命中，不允许继续"]], [1.75, 4.75]),
              Spacer(1, 10), callout("外部能力预留", "身份证件核验、闸机门禁、访客机、企业微信和短信均为预留对接能力，社区源码版未集成真实第三方设备或商业服务。")]

    story += page("12. 数据、隐私与安全", "DATA & PRIVACY")
    story += [P("仅收集完成访问管理所必需的信息。手机号在列表中默认脱敏展示；证件照片、生物特征等敏感数据不应在未具备合规条件时采集。"),
              data_table(["数据类型", "用途", "建议保护措施"], [
                  ["姓名、手机、单位", "预约联系与身份核对", "脱敏展示、权限控制、到期删除"],
                  ["访问时间与区域", "通行授权和事件追溯", "按岗位授权、保留审计记录"],
                  ["状态与异常记录", "接待闭环和风险处置", "限定管理员和安保访问"],
                  ["证件 / 照片（预留）", "实名或人证核验", "加密存储、单独授权、短期保存"],
              ], [1.6, 2.2, 2.7]), Spacer(1, 10), P("上线检查", "h2"),
              bullet("更换 admin、operator 的默认密码，不对公网暴露演示账号。"),
              bullet("启用 HTTPS，限制数据库和后端端口的网络访问范围。"),
              bullet("按本组织隐私政策配置保存期限和删除机制。"),
              bullet("接入真实门禁、短信、企业微信前完成供应商安全评估。"),
              bullet("定期检查超时未离场、黑名单变更和风险处置记录。")]

    story += page("13. 常见问题", "FAQ")
    for q, a in [
        ("为什么预约不能直接签到？", "预约必须先处于“已审批”状态；受限区域还需安保复核。"),
        ("审批通过后在哪里查看通行码？", "移动端点击“通行码”，管理端预约详情也会显示数字码。"),
        ("访客已经离开但仍显示在园？", "在预约详情执行“确认离场”；如已超时，先完成现场核对。"),
        ("如何处理黑名单访客？", "不应继续审批，由安保核实原因后决定是否解除。"),
        ("企业微信、短信和闸机可以直接用吗？", "社区源码版仅预留对接方式，需要自行配置开发或联系授权定制。"),
        ("为什么 operator 看不到基础设置？", "基础设置属于管理员权限，operator 只处理日常业务。"),
        ("在哪里查看谁修改了资源或预约？", "管理员进入审计报表，刷新后可查看业务编号、操作人和说明。"),
        ("在哪里查看谁修改了资源或预约？", "管理员进入审计报表，刷新后可查看业务编号、操作人和说明。"),
        ("可以用于企业正式运营吗？", "未经上海如静知华信息科技有限公司书面授权不得商用或生产部署。"),
    ]:
        story += [P(q, "h2"), P(a)]

    story += page("14. 安装与体验", "INSTALLATION")
    story += [P("最简单的体验方式是使用 Docker Compose 启动 MySQL、Java 后端和前端。"),
              data_table(["组件", "建议版本"], [["Docker / Compose", "支持 compose.yaml 的当前稳定版本"], ["Java", "21（本地开发）"], ["Node.js", "22（本地开发）"], ["MySQL", "8.4"]], [2.0, 4.5]),
              Spacer(1, 10), P("启动步骤", "h2"), step(1, "准备配置", "复制 .env.example 为 .env，修改数据库和演示账号密码。"),
              step(2, "启动服务", "在项目根目录执行 docker compose up --build。"),
              step(3, "打开系统", "浏览器访问 http://localhost:8090。"),
              step(4, "登录体验", "admin / admin123；operator / operator123。"),
              callout("重要", "默认账号和密码仅供本地学习演示，严禁直接用于互联网或生产环境。", colors.HexColor("#FCEDE9"), RED)]

    story += page("15. 许可边界与技术支持", "SUPPORT")
    story += [P("非商业使用说明", "h2"), P("本工程仅能用于个人非商业学习、研究和技术交流。未经上海如静知华信息科技有限公司书面授权，不得用于企业内部生产、商业部署、SaaS、收费下载、售卖、外包交付、投标、品牌替换或任何直接、间接商业用途。具体以项目 LICENSE 为准。"),
              P("深度开发与商业授权", "h2"), P("如需接入身份证件核验、闸机门禁、访客机、企业微信、短信、多园区、单点登录、国产化环境或实施交付，可联系知华科技。"),
              data_table(["联系项目", "信息"], [["公司", "上海如静知华信息科技有限公司"], ["品牌", "知华科技 / ZhuaTech"], ["官网", "https://www.zhuatech.cn/"], ["服务", "中小企业信息化、AI 转型、软件外包、实施与深度定制"]], [1.4, 5.1]), Spacer(1, 12)]
    qrs = Table([[Image(str(IMG / "zhuatech-wechat-consulting.png"), width=2.05*inch, height=2.05*inch),
                  Image(str(IMG / "zhuatech-wechat-consulting-2.png"), width=2.05*inch, height=2.05*inch)],
                 [P("微信咨询二维码 1", "caption"), P("微信咨询二维码 2", "caption")]], colWidths=[3.25*inch, 3.25*inch])
    qrs.setStyle(TableStyle([("ALIGN", (0,0), (-1,-1), "CENTER")]))
    story += [qrs, P("<link href='https://www.zhuatech.cn/' color='#176B57'>访问知华科技官网 https://www.zhuatech.cn/</link>", "center"),
              P("© 2026 上海如静知华信息科技有限公司", "center")]

    doc.build(story)
    print(OUT)


if __name__ == "__main__":
    build()
