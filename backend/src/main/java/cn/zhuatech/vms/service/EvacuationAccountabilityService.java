/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class EvacuationAccountabilityService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Result reconcile(Request request) {
        int visitorsOnSite = Math.max(0, request.checkedInVisitors() - request.badgedOutVisitors());
        int accounted = Math.min(visitorsOnSite, request.musteredVisitors() + request.hostConfirmedVisitors());
        int unaccounted = Math.max(0, visitorsOnSite - accounted);
        String decision = request.emergencyActive() && unaccounted > 0 ? "LOCATE_NOW"
            : unaccounted > 0 ? "RECONCILE" : "CLEAR";
        List<String> actions = new ArrayList<>();
        if (unaccounted > 0) actions.add("按访客名单联系接待人并核对最后门禁位置");
        if (request.emergencyActive() && unaccounted > 0) actions.add("将未清点访客提交应急指挥员并启动现场查找");
        if (unaccounted == 0) actions.add("完成清点并归档本次疏散记录");
        return new Result(request.siteCode(), visitorsOnSite, accounted, unaccounted,
            decision, actions);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Request(@NotBlank String siteCode, @Min(0) int checkedInVisitors,
                          @Min(0) int badgedOutVisitors, @Min(0) int musteredVisitors,
                          @Min(0) int hostConfirmedVisitors, boolean emergencyActive) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Result(String siteCode, int visitorsOnSite, int accountedVisitors,
                         int unaccountedVisitors, String decision, List<String> actions) {}
}
