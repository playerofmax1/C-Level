package com.clevel.kudu.model;

import java.util.HashMap;
import java.util.Map;

public enum SystemConfig {
    AUTO_EMAIL_CC("app.auto.email.cc", InputType.TEXT),
    EMAIL_PASSWORD("app.external.mail.password", InputType.TEXT),
    EMAIL_PORT("app.external.mail.port", InputType.INTEGER),
    EMAIL_SENDER_NAME("app.external.mail.sender.name", InputType.TEXT),
    EMAIL_SERVER("app.external.mail.server", InputType.TEXT),
    EMAIL_SMTP_AUTH("app.external.mail.smtp.auth", InputType.YESNO),
    EMAIL_TLS_ENABLE("app.external.mail.tls.enable", InputType.ENABLED),
    EMAIL_USERNAME("app.external.mail.username", InputType.TEXT),

    DEFAULT_TARGET_UTILIZATION("app.pf.default.target.utilization", InputType.PERCENT),
    PF_YEAR("app.pf.year", InputType.INTEGER),

    TS_CUTOFF_DATE("app.ts.cutoff.date", InputType.INTEGER),
    TS_CUTOFF_DATE_ENABLE("app.ts.cutoff.date.enable", InputType.ENABLED),

    FORCE_RELOAD_CSS("app.force.reload.css", InputType.YESNO),
    ;

    private String code;
    private InputType inputType;

    SystemConfig(String code, InputType inputType) {
        this.code = code;
        this.inputType = inputType;
    }

    public String getCode() {
        return code;
    }

    public InputType getInputType() {
        return inputType;
    }

    public static SystemConfig lookup(String value) {
        for (SystemConfig systemConfig : SystemConfig.values()) {
            if (systemConfig.code.equals(value))
                return systemConfig;
        }
        return null;
    }

    private static Map<String, SystemConfig> parserMap;

    public static SystemConfig parse(String name) {
        String upperKey;
        if (parserMap == null) {
            parserMap = new HashMap<>();
            for (SystemConfig systemConfig : SystemConfig.values()) {
                upperKey = systemConfig.getCode().toUpperCase();
                parserMap.put(upperKey, systemConfig);
            }
        }

        upperKey = name.toUpperCase();
        return parserMap.get(upperKey);
    }

}
