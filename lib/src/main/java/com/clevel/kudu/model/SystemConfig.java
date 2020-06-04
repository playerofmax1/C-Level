package com.clevel.kudu.model;

import java.util.HashMap;
import java.util.Map;

public enum SystemConfig {
    AUTO_EMAIL_CC("app.auto.email.cc", InputType.TEXT, false),
    EMAIL_PASSWORD("app.external.mail.password", InputType.TEXT, false),
    EMAIL_PORT("app.external.mail.port", InputType.INTEGER, false),
    EMAIL_SENDER_NAME("app.external.mail.sender.name", InputType.TEXT, false),
    EMAIL_SERVER("app.external.mail.server", InputType.TEXT, false),
    EMAIL_SMTP_AUTH("app.external.mail.smtp.auth", InputType.YESNO, false),
    EMAIL_TLS_ENABLE("app.external.mail.tls.enable", InputType.YESNO, false),
    EMAIL_USERNAME("app.external.mail.username", InputType.TEXT, false),

    DEFAULT_TARGET_UTILIZATION("app.pf.default.target.utilization", InputType.PERCENT, true),
    PF_YEAR("app.pf.year", InputType.INTEGER, false),

    TS_CUTOFF_DATE("app.ts.cutoff.date", InputType.INTEGER, true),
    TS_CUTOFF_DATE_ENABLE("app.ts.cutoff.date.enable", InputType.YESNO, false),
    ;

    private String code;
    private boolean generateAsNewGroup;
    private InputType inputType;

    SystemConfig(String code, InputType inputType, boolean generateAsNewGroup) {
        this.code = code;
        this.inputType = inputType;
        this.generateAsNewGroup = generateAsNewGroup;
    }

    public String getCode() {
        return code;
    }

    public InputType getInputType() {
        return inputType;
    }

    public boolean isGenerateAsNewGroup() {
        return generateAsNewGroup;
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
