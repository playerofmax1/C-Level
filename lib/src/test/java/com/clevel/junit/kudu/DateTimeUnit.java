package com.clevel.junit.kudu;

import com.clevel.kudu.util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

class DateTimeUnit {

    private Logger log = LoggerFactory.getLogger(DateTimeUnit.class);

    @Test
    void netWorkdays() {
        for (int month = 11; month < 13; month++) {
            Date date = Date.from(Instant.parse("2019-" + month + "-01T00:00:00.00Z"));
            log.debug("------- Month {} 2019 -------", month);
            printMonth(date);
        }
        for (int month = 1; month < 10; month++) {
            Date date = Date.from(Instant.parse("2020-0" + month + "-01T00:00:00.00Z"));
            log.debug("------- Month {} 2020 -------", month);
            printMonth(date);
        }
        for (int month = 10; month < 12; month++) {
            Date date = Date.from(Instant.parse("2020-" + month + "-01T00:00:00.00Z"));
            log.debug("------- Month {} 2020 -------", month);
            printMonth(date);
        }

        Date firstDayOfYear;
        Date lastDayOfYear;

        for (int year = 2016; year < 2026; year++) {
            firstDayOfYear = Date.from(Instant.parse(year + "-01-01T00:00:00.00Z"));
            lastDayOfYear = Date.from(Instant.parse(year + "-12-31T00:00:00.00Z"));
            log.debug("------- Year {} -------", year);
            printYear(firstDayOfYear, lastDayOfYear);
        }

        for (int year = 2016; year < 2026; year++) {
            firstDayOfYear = Date.from(Instant.parse((year - 1) + "-11-01T00:00:00.00Z"));
            lastDayOfYear = Date.from(Instant.parse(year + "-10-31T00:00:00.00Z"));
            log.debug("------- Performance Year {} -------", year);
            printYear(firstDayOfYear, lastDayOfYear);
        }

    }

    private void printYear(Date firstDayOfYear, Date lastDayOfYear) {
        long days = daysBetween(firstDayOfYear, lastDayOfYear);
        long workdays = workdays(firstDayOfYear, lastDayOfYear);
        long weekends = days - workdays;
        long weeks = weekends / 2;
        log.debug("firstDayOfYear = {}", firstDayOfYear);
        log.debug("lastDayOfYear = {}", lastDayOfYear);
        log.debug("days = {}", days);
        log.debug("workdays = {}", workdays);
        log.debug("weekends = {}", weekends);
        log.debug("weeks = {}", weeks);
    }

    long netWorkdaysTotal = 0;
    long daysInMonthTotal = 0;
    long netWeekendsTotal = 0;
    long workdaysTotal = 0;
    long weekendsTotal = 0;

    void printMonth(Date date) {
        Date firstDayOfMonth = DateTimeUtil.getFirstDateOfMonth(date);
        Date lastDayOfMonth = DateTimeUtil.getLastDateOfMonth(date);
        long netWorkdays = netWorkdays(firstDayOfMonth, lastDayOfMonth);
        long daysInMonth = daysBetween(firstDayOfMonth, lastDayOfMonth);
        long netWeekends = daysInMonth - netWorkdays;
        long workdays = workdays(firstDayOfMonth, lastDayOfMonth);
        long weekends = daysInMonth - workdays;

        log.debug("firstDayOfMonth = {}", firstDayOfMonth);
        log.debug("lastDayOfMonth = {}", lastDayOfMonth);
        log.debug("daysInMonth = {}", daysInMonth);
        log.debug("netWorkdays = {}", netWorkdays);
        log.debug("netWeekends = {}", netWeekends);
        log.debug("workdays = {}", workdays);
        log.debug("weekends = {}", weekends);

        netWorkdaysTotal += netWorkdays;
        daysInMonthTotal += daysInMonth;
        netWeekendsTotal += netWeekends;
        workdaysTotal += workdays;
        weekendsTotal += weekends;

        log.debug("netWorkdaysTotal = {}", netWorkdaysTotal);
        log.debug("daysInMonthTotal = {}", daysInMonthTotal);
        log.debug("netWeekendsTotal = {}", netWeekendsTotal);
        log.debug("workdaysTotal = {}", workdaysTotal);
        log.debug("weekendsTotal = {}", weekendsTotal);
    }

    int getDay(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_MONTH);
    }

    long daysBetween(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
    }

    /**
     * This code is modified of: DateTimeUtil.countWorkingDay()
     */
    long workdays(Date startDate, Date endDate) {
        final String DEFAULT_ZONE = "Asia/Bangkok";
        final LocalDate start = startDate.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDate().withDayOfMonth(1);
        final LocalDate end = endDate.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).withDayOfMonth(start.lengthOfMonth()).plusDays(1).toLocalDate();

        final long days = daysBetween(startDate, endDate);
        /**
         * workingInDay(ofMAY) = 31 - (2 * ( (31 + 6) / 7 ))
         *                     = 31 - (2 * ( 37 / 7 ))
         *                     = 31 - (2 * ( 5 ))
         *                     = 31 - 10
         *                     = 21
         * workingInDay = dayInAMonth - [weekendDayInAMonth]
         * weekendDayInAMonth =
         **/
        final long daysWithoutWeekends = days - 2 * ((days + start.getDayOfWeek().getValue()) / 7);
        return daysWithoutWeekends + (start.getDayOfWeek() == DayOfWeek.SUNDAY ? 1 : 0) + (end.getDayOfWeek() == DayOfWeek.SUNDAY ? 1 : 0);
    }

    /**
     * This code come from: Solution without Loop> https://stackoverflow.com/questions/4600034/calculate-number-of-weekdays-between-two-dates-in-java
     */
    long netWorkdays(Date start, Date end) {
        //Ignore argument check

        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        int w1 = c1.get(Calendar.DAY_OF_WEEK);
        c1.add(Calendar.DAY_OF_WEEK, -w1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        int w2 = c2.get(Calendar.DAY_OF_WEEK);
        c2.add(Calendar.DAY_OF_WEEK, -w2);

        //end Saturday to start Saturday
        long days = (c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 60 * 60 * 24);
        long daysWithoutWeekendDays = days - (days * 2 / 7);

        // Adjust days to add on (w2) and days to subtract (w1) so that Saturday
        // and Sunday are not included
        if (w1 == Calendar.SUNDAY && w2 != Calendar.SATURDAY) {
            w1 = Calendar.MONDAY;
        } else if (w1 == Calendar.SATURDAY && w2 != Calendar.SUNDAY) {
            w1 = Calendar.FRIDAY;
        }

        if (w2 == Calendar.SUNDAY) {
            w2 = Calendar.MONDAY;
        } else if (w2 == Calendar.SATURDAY) {
            w2 = Calendar.FRIDAY;
        }

        return daysWithoutWeekendDays - w1 + w2;
    }


}
