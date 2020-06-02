/*AUTO_EMAIL_CC*/
INSERT INTO sys_config (description, name, value)
VALUES ('All emails will CC to this email list (comma separated value).', 'app.auto.email.cc', 'clv_pmo_admin@the-c-level.com');

/*CHANGE NAME to group setting*/
UPDATE sys_config
set name        = 'app.pf.default.target.utilization',
    description = 'Default value of Target Utilization for unspecified/new-employee.'
where name = 'app.default.target.utilization';
UPDATE sys_config
SET description = 'Password used to authenticate to SMTP Server.'
WHERE name = 'app.external.mail.password';
UPDATE sys_config
SET description = 'SMTP Server Port Number.'
WHERE name = 'app.external.mail.port';
UPDATE sys_config
SET description = 'All emails will send by this name.'
WHERE name = 'app.external.mail.sender.name';
UPDATE sys_config
SET description = 'SMTP Server (DNS / IP-Address).'
WHERE name = 'app.external.mail.server';
UPDATE sys_config
SET description = 'SMTP Server requires authentication.'
WHERE name = 'app.external.mail.smtp.auth';
UPDATE sys_config
SET description = 'SMTP Server requires TLS.'
WHERE name = 'app.external.mail.tls.enable';
UPDATE sys_config
SET description = 'User-name(email) used to authenticate to SMTP Server.'
WHERE name = 'app.external.mail.username';
UPDATE sys_config
SET description = 'Current year (performance year).'
WHERE name = 'app.pf.year';
UPDATE sys_config
SET description = 'Day number used to cut date for start month of new employee.'
WHERE name = 'app.ts.cutoff.date';
UPDATE sys_config
SET description = 'Use specified cutoff date above.'
WHERE name = 'app.ts.cutoff.date.enable';

COMMIT;
