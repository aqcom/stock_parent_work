package com.itheima.sh.stock.utils;

import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import com.itheima.sh.stock.pojo.entity.StockMarketIndexInfo;

import java.math.BigDecimal;

/**
 * 数据转化工具类
 */
public class DomainTransferUtil {

    /**
     * 将entity转化成domain
     * @param info
     * @return
     */
    public static InnerMarketDomain transferMarketInfo(StockMarketIndexInfo info){
        InnerMarketDomain domain = InnerMarketDomain.builder().code(info.getMarketCode())
                .name(info.getMarketName())
                .openPoint(info.getOpenPoint())
                .curPoint(info.getCurPoint())
                .preClosePoint(info.getPreClosePoint())
                .tradeAmt(info.getTradeAmount())
                .tradeVol(info.getTradeVolume())
                .upDown(info.getCurPoint().subtract(info.getPreClosePoint()))
                .rose(info.getCurPoint().subtract(info.getPreClosePoint()).divide(info.getPreClosePoint(),20, BigDecimal.ROUND_HALF_UP))
                .amplitude(info.getMaxPoint().subtract(info.getMinPoint()).divide(info.getPreClosePoint(),20,BigDecimal.ROUND_HALF_UP))
                .curTime(info.getCurTime())
                .build();
        return domain;
    }
}
