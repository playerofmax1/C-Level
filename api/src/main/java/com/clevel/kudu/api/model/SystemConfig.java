package com.clevel.kudu.api.model;

public enum SystemConfig {
    EMAIL_SERVER("app.external.mail.server"),
    EMAIL_PORT("app.external.mail.port"),
    EMAIL_SMTP_AUTH("app.external.mail.smtp.auth"),
    EMAIL_TLS_ENABLE("app.external.mail.tls.enable"),
    EMAIL_USERNAME("app.external.mail.username"),
    EMAIL_PASSWORD("app.external.mail.password"),
    EMAIL_SENDER_NAME("app.external.mail.sender.name"),

    TS_CUTOFF_DATE_ENABLE("app.ts.cutoff.date.enable"),
    TS_CUTOFF_DATE("app.ts.cutoff.date");

    private String value;

    SystemConfig(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static SystemConfig lookup(String value) {
        for (SystemConfig systemConfig : SystemConfig.values()) {
            if (systemConfig.value.equals(value))
                return systemConfig;
        }
        return null;
    }

}
