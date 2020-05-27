
/*DEFAULT_TARGET_UTILIZATION*/
INSERT INTO sys_config (description, name, value) VALUES ('DEFAULT_TARGET_UTILIZATION = 80%', 'app.default.target.utilization', '80');

--
-- Table structure for table `wrk_user_performance`
--

DROP TABLE IF EXISTS `wrk_user_performance`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `wrk_user_performance`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,

    `userId`            bigint(20) NOT NULL,
    `performanceYearId` bigint(20) NOT NULL,

    `targetUtilization` double     DEFAULT 80,

    /*more columns about performance put here*/

    `createDate`        datetime(6)         DEFAULT NULL,
    `modifyDate`        datetime(6)         DEFAULT NULL,
    `version`           bigint(20) NOT NULL,
    `createBy`          bigint(20) NOT NULL,
    `modifyBy`          bigint(20) NOT NULL,

    PRIMARY KEY (`id`),
    CONSTRAINT `FKUSERPF_USER` FOREIGN KEY (`userId`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKUSERPF_YEAR` FOREIGN KEY (`performanceYearId`) REFERENCES `wrk_performance_year` (`id`),
    CONSTRAINT `FKUSERPF_CREATE` FOREIGN KEY (`createBy`) REFERENCES `wrk_user` (`id`),
    CONSTRAINT `FKUSERPF_MODIFY` FOREIGN KEY (`modifyBy`) REFERENCES `wrk_user` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrk_user_performance`
--



--
-- Table structure for table `vw_rpt_mandays`
-- version 2: add planMD from project-task
--
CREATE OR REPLACE VIEW vw_rpt_mandays AS (
    select ts.userId,
           pfy.year                                                                                 as workYear,

           if(usr.targetUtilization is null, 0.8, usr.targetUtilization)                            as targetPercentCU,
           pjt.id                                                                                   as projectId,
           if(pjttask.amdCalculation is null, 0, pjttask.amdCalculation)                            as planFlag,

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

    where chargeMinute > 0

    group by userId, workYear, projectId, planFlag
);
