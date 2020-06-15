/*AUTO_EMAIL_CC*/
INSERT INTO sys_config (description, name, value)
VALUES ('All emails will CC to this email list (comma separated value).', 'app.auto.email.cc', 'clv_pmo_admin@the-c-level.com');

/*CHANGE NAME to group settings in Admin Settings Screen*/
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


--
-- Table structure for table `vw_rpt_mandays`
-- version 3: depends on the change of sys_config above.
--
CREATE OR REPLACE VIEW vw_rpt_mandays AS (
    select ts.userId,
           pfy.year                                                                                  as workYear,

           if(usr.targetUtilization is null, cast(defaults.value as decimal), usr.targetUtilization) as targetPercentCU,
           pjt.id                                                                                    as projectId,
           if(pjttask.amdCalculation is null, 0, pjttask.amdCalculation)                             as planFlag,

           sum(ts.chargeMinute)                                                                      as chargeMinutes,
           sum(ts.chargeMinute) / 60                                                                 as chargeHours,
           sum(ts.chargeMinute) / 480                                                                as chargeDays,
           count(ts.workDate)                                                                        as workDays,

           min(ts.workDate)                                                                          as firstChargeDate,
           max(ts.workDate)                                                                          as lastChargeDate,
           DATEDIFF(max(ts.workDate), min(ts.workDate)) + 1                                          as firstToLastDays,

           projectTask.PMD,
           if(projectTask.AMD is null, /*chargeDays*/(sum(ts.chargeMinute) / 480), projectTask.AMD)  as AMD,
           (projectTask.PMD - projectTask.AMD) / projectTask.PMD * 100.00                            as RPMDPercent

    from wrk_timesheet ts
             left join wrk_project pjt on (ts.projectId = pjt.id)
             left join wrk_prj_task pjttask on (ts.projectTaskId = pjttask.id)
             left join wrk_performance_year pfy on (ts.workDate between pfy.startDate and pfy.endDate)
             left join wrk_user_performance usr on (ts.userId = usr.userId and usr.performanceYearId = pfy.id)
             left join (
        select ptk.projectId,
               ptk.userId,
               sum(ptk.planMD + ptk.extendMD) as PMD,
               sum(ptk.actualMD)              as AMD,
               ptk.amdCalculation
        from wrk_prj_task ptk
        GROUP by ptk.projectId, ptk.amdCalculation, ptk.userId
    ) projectTask on (projectTask.projectId = ts.projectId
        and projectTask.userId = ts.userId
        and projectTask.amdCalculation = pjttask.amdCalculation)
             left join sys_config defaults on (defaults.name = 'app.pf.default.target.utilization')

    where chargeMinute > 0

    group by userId, workYear, projectId, planFlag
);