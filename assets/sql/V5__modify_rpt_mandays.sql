-- ----------------------------------------------
-- TODO: need solve for the change of Next Year
-- set Target Year by Year.
-- need relation table to link between user and performance_year
-- ----------------------------------------------

-- Add column (Target %CU) to User Profile.
alter table wrk_user
    add targetPercentCU DOUBLE default 0.8;

--
-- Table structure for table `vw_rpt_mandays`
-- version 2: add planMD from project-task
--
CREATE OR REPLACE VIEW vw_rpt_mandays AS (
    select ts.userId,
           usr.targetPercentCU,
           pjt.id                                                                                   as projectId,
           pfy.year                                                                                 as workYear,

           sum(ts.chargeMinute)                                                                     as chargeMinutes,
           sum(ts.chargeMinute) / 60                                                                as chargeHours,
           sum(ts.chargeMinute) / 480                                                               as chargeDays,
           count(ts.workDate)                                                                       as workDays,

           min(ts.workDate)                                                                         as firstChargeDate,
           max(ts.workDate)                                                                         as lastChargeDate,
           DATEDIFF(max(ts.workDate), min(ts.workDate)) + 1                                         as firstToLastDays,

           projectTask.PMD,
           if(projectTask.AMD is null, /*chargeDays*/(sum(ts.chargeMinute) / 480), projectTask.AMD) as AMD,
           (projectTask.PMD - projectTask.AMD) / projectTask.PMD * 100.00                           as RPMDPercent

        /* -- Require
        projectTask.PMD - projectTask.AMD                              as RMD,

        '|',

        100.00                                                         as PMDPercentOfPMD,
        projectTask.AMD / projectTask.PMD * 100.00                     as AMDPercentOfPMD,
        (projectTask.PMD - projectTask.AMD) / projectTask.PMD * 100.00 as RMDPercentOfPMD,

        '|',

        projectTask.PCTAMD,
        projectTask.PCTAMDCount,
        projectTask.PCTAMD / projectTask.PCTAMDCount                   as percentAMD,
        projectTask.percentAMD                                         as percentAMDBefore
        */

    from wrk_timesheet ts
             left join wrk_user usr on (ts.userId = usr.id)
             left join wrk_project pjt on (ts.projectId = pjt.id)
             left join wrk_performance_year pfy on (ts.workDate between pfy.startDate and pfy.endDate)
             left join (
        select ptk.projectId,
               ptk.userId,
               sum(ptk.planMD + ptk.extendMD) as PMD,
               sum(ptk.actualMD)              as AMD
            /*
            sum(ptk.percentAMD)                                                                        as PCTAMD,
            sum((((ptk.planMD + ptk.extendMD) - ptk.actualMD) / (ptk.planMD + ptk.extendMD)) * 100.00) as percentAMD,
            count(ptk.id)                                                                              as PCTAMDCount
            */
        from wrk_prj_task ptk
        GROUP by ptk.projectId, ptk.userId
    ) projectTask on (projectTask.projectId = ts.projectId and projectTask.userId = ts.userId)

    where chargeMinute > 0

    group by userId, workYear, projectId
);
