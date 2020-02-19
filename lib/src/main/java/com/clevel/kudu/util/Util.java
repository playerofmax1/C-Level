package com.clevel.kudu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static void listFields(Map<?, ?> fields) {
        for (Map.Entry<?, ?> entry : fields.entrySet()) {
            log.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
        }
    }

    public static boolean isTrue(String str) {
        return str.trim().toLowerCase().matches("[tT]rue|[yY]es|1");
    }

    public static String replaceNewLine(String str) {
        return str.replace(System.lineSeparator(),"<br />");
    }

}
