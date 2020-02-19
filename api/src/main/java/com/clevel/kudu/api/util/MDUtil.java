package com.clevel.kudu.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MDUtil {
    private static final Logger log = LoggerFactory.getLogger(MDUtil.class);

    public static final int DEFAULT_SCALE = 2;
    private static final BigDecimal BD_100 = new BigDecimal("100.00");

    private MDUtil() {
    }

    public static BigDecimal getPercentAMD(long planMDMinute, long actualMDMinute) {
        log.debug("getPercentAMD. (planMDMinute: {}, actualMDMinute: {})",planMDMinute,actualMDMinute);

        BigDecimal plan = new BigDecimal(planMDMinute);
        BigDecimal actual = new BigDecimal(actualMDMinute);

        BigDecimal percentAMD = plan.subtract(actual).multiply(BD_100).divide(plan,DEFAULT_SCALE, RoundingMode.HALF_UP);

        log.debug("getPercentAMD. (%AMD: {})",percentAMD);
        return percentAMD;
    }

}
