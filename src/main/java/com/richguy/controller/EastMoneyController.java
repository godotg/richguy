package com.richguy.controller;

import com.richguy.model.east.EastMoneyIndustry;
import com.richguy.model.east.EastMoneyResult;
import com.richguy.service.RichGuyService;
import com.richguy.util.HttpUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.manager.SchedulerBus;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author jaysunxiao
 * @version 3.0
 */
@Component
public class EastMoneyController {

    private Set<String> eastMoneyIndustries = new HashSet<>();

    @Autowired
    private RichGuyService richGuyService;

    @Scheduler(cron = "0 0/3 * * * ?")
    public void cronPushGn() throws IOException, InterruptedException {
        var list = getEastMoneyIndustries();

        // 第一次先初始化
        if (CollectionUtils.isEmpty(eastMoneyIndustries)) {
            for (var industry : list) {
                eastMoneyIndustries.add(StringUtils.trim(industry.getGn()));
            }
            return;
        }

        for (var industry : list) {
            var gn = StringUtils.trim(industry.getGn());

            if (eastMoneyIndustries.contains(gn)) {
                continue;
            }

            notifyNewGn(industry);

            eastMoneyIndustries.add(gn);

            SchedulerBus.schedule(() -> notifyNewGn(industry), 1 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 2 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 3 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 4 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 8 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 16 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 32 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
            SchedulerBus.schedule(() -> notifyNewGn(industry), 64 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS);
        }
    }

    public void notifyNewGn(EastMoneyIndustry industry) {
        var gn = StringUtils.trim(industry.getGn());

        var builder = new StringBuilder();
        builder.append("\uD83D\uDCA5紧急广播-东财新概念：");

        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        builder.append(StringUtils.format("{} {} ", gn, industry.getF12()));
        builder.append(FileUtils.LS);
        builder.append(FileUtils.LS);

        var url = StringUtils.format("http://quote.eastmoney.com/bk/{}.{}.html", industry.getF13(), industry.getF12());
        builder.append(url);
        builder.append(FileUtils.LS);

        richGuyService.pushGroupMessage(builder.toString());
    }


    public EastMoneyResult requestForEastMoneyResult() throws IOException, InterruptedException {
        var url = "https://53.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=1000&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50";
        var responseBody = HttpUtils.get(url);

        var result = JsonUtils.string2Object(responseBody, EastMoneyResult.class);
        return result;
    }

    public List<EastMoneyIndustry> getEastMoneyIndustries() throws IOException, InterruptedException {
        var response = requestForEastMoneyResult();
        var data = response.getData();
        if (data == null) {
            return Collections.emptyList();
        }

        var diff = data.getDiff();
        if (CollectionUtils.isEmpty(diff)) {
            return Collections.emptyList();
        }

        return diff;
    }

}