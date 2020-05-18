package com.clevel.kudu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {
    private static final Logger log = LoggerFactory.getLogger(DateTimeUtil.class);
    public static final String DEFAULT_ZONE = "Asia/Bangkok";
    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DEFAULT_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final BigDecimal MANDAYS_HOUR = new BigDecimal(8);
    public static final BigDecimal MANDAYS_MINUTE = new BigDecimal(60);
    public static final int DEFAULT_SCALE = 2;

    public static Date now() {
        return Date.from(Instant.now());
    }

    public static Date currentDate() {
        Instant instant = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date yesterdayDate() {
        Instant instant = LocalDate.now().atStartOfDay().minusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date getDatePlusHoursAndMinutes(Date date, long hours, long minutes) {
        Instant instant = LocalDate.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_ZONE)).atStartOfDay().plusHours(hours).plusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date getDatePlusDays(Date date, int days) {
        Instant instant = LocalDate.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_ZONE)).atStartOfDay().plusDays(days).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date getDatePlusMonths(Date date, int months) {
        Instant instant = LocalDate.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_ZONE)).atStartOfDay().plusMonths(months).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static Date setTime(Date date, int hour, int minute, int second) {
        Instant instant = LocalDate.ofInstant(date.toInstant(), ZoneId.of(DEFAULT_ZONE)).atTime(hour, minute, second).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public static String getDateStr(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.of(DEFAULT_ZONE)).format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }

    public static String getDateStr(Date date, String pattern) {
        return LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.of(DEFAULT_ZONE)).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getDateTimeStr(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(),
                ZoneId.of(DEFAULT_ZONE)).format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT));
    }

    public static Date getFirstDateOfMonth(Date month) {
        LocalDate initial = month.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDate();
        return Date.from(initial.withDayOfMonth(1).atStartOfDay(ZoneId.of(DEFAULT_ZONE)).toInstant());
    }

    public static Date getLastDateOfMonth(Date month) {
        LocalDate initial = month.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDate();
        return Date.from(initial.withDayOfMonth(initial.lengthOfMonth()).atStartOfDay(ZoneId.of(DEFAULT_ZONE)).toInstant());
    }

    public static Date getLastDateOfYear(Date year) {
        LocalDate initial = year.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDate();
        return Date.from(initial.withDayOfYear(initial.lengthOfYear()).atStartOfDay(ZoneId.of(DEFAULT_ZONE)).toInstant());
    }

    public static boolean isSameDate(Date date1, Date date2) {
        Instant instant1 = LocalDate.ofInstant(date1.toInstant(), ZoneId.of(DEFAULT_ZONE)).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Instant instant2 = LocalDate.ofInstant(date2.toInstant(), ZoneId.of(DEFAULT_ZONE)).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return instant1.compareTo(instant2) == 0;
    }

    public static Duration diffTime(Date time1, Date time2) {
        LocalDateTime t1 = time1.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDateTime();
        LocalDateTime t2 = time2.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDateTime();

        return Duration.between(t1, t2);
    }

    public static String durationToString(Duration duration) {
        long hour = duration.toHours();
        long minute = duration.toMinutesPart();

        return String.format("%02d:%02d", hour, minute);
    }

    // pattern = HH:mm
    public static Duration stringToDuration(String str) {
        String[] tmp = str.split(":");
        if (tmp.length == 1) {
            return Duration.parse("PT" + tmp[0] + "M");
        } else {
            return Duration.parse("PT" + tmp[0] + "H" + tmp[1] + "M");
        }
    }

    public static BigDecimal getManDays(long minutes) {
        BigDecimal m = new BigDecimal(minutes);
        BigDecimal result = m.divide(MANDAYS_MINUTE,DEFAULT_SCALE, RoundingMode.HALF_UP)
                .divide(MANDAYS_HOUR,DEFAULT_SCALE, RoundingMode.HALF_UP);
//        log.debug("getManDay. (minutes: {}, manDays: {} MD.)",minutes,result);
        return result;
    }

    public static BigDecimal getTotalMD(Long... minutes) {
        long total = getTotalMinute(minutes);
        return getManDays(total);
    }

    public static Duration getTotalDuration(Long... minutes) {
        Duration duration = Duration.ZERO;
        for (Long l:minutes) {
            duration = duration.plus(Duration.ofMinutes(l));
        }
        return duration;
    }

    public static long getTotalMinute(Long... minutes) {
        long total = 0L;
        for (Long l:minutes) {
            total = total+l;
        }
        return total;
    }

    public static long countWorkingDay(final Date month) {
        final LocalDate start = month.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDate().withDayOfMonth(1);
        final LocalDate end = month.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).withDayOfMonth(start.lengthOfMonth()).plusDays(1).toLocalDate();
//        log.debug("start: {}, end: {}",start,end);

        final long days = start.lengthOfMonth();
//        log.debug("days: {}",days);

//        log.debug("start.getDayOfWeek(): {}",start.getDayOfWeek());
//        log.debug("days - 2 * ((days + start.getDayOfWeek().getValue())/7)");
//        log.debug("{} - 2 * (({} + {})/{})",days,days,start.getDayOfWeek().getValue(),7);
        final long daysWithoutWeekends = days - 2 * ((days + start.getDayOfWeek().getValue())/7);
        return daysWithoutWeekends + (start.getDayOfWeek() == DayOfWeek.SUNDAY ? 1 : 0) + (end.getDayOfWeek() == DayOfWeek.SUNDAY ? 1 : 0);

//        Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
//        return start.datesUntil(end)
//                .filter(d -> !weekend.contains(d.getDayOfWeek()))
//                .count();
    }

    public static boolean isHoliday(final Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDate();
        return localDate.getDayOfWeek() == DayOfWeek.SUNDAY || localDate.getDayOfWeek() == DayOfWeek.SATURDAY;
    }

    public static Duration getWorkHour(Date timeIn, Date timeOut) {
        log.debug("getWorkHour. (timeIn: {}, timeOut: {})",timeIn,timeOut);
        LocalDateTime in = timeIn.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDateTime();
        LocalDateTime out = timeOut.toInstant().atZone(ZoneId.of(DEFAULT_ZONE)).toLocalDateTime();

        Date lunchStart = getDatePlusHoursAndMinutes(timeIn,12,0);
        Date lunchEnd = getDatePlusHoursAndMinutes(timeOut,13,0);
        if (timeIn.after(lunchStart) && timeIn.before(lunchEnd)) {
            lunchStart = timeIn;
        }
        if (timeOut.after(lunchStart) && timeOut.before(lunchEnd)) {
            lunchEnd = timeOut;
        }
        Duration lunchDuration = diffTime(lunchStart,lunchEnd);
//        log.debug("lunchStart: {}, lunchEnd: {}, lunchDuration: {}",lunchStart,lunchEnd,lunchDuration);
        Duration workHour = Duration.between(in,out);
//        log.debug("workHour: {}, minutes: {}",workHour,workHour.toMinutes());
        if (timeIn.before(lunchEnd)) {
            workHour = workHour.minusMinutes(lunchDuration.toMinutes());
//            log.debug("[-lunch time] workHour: {}, minutes: {}",workHour,workHour.toMinutes());
        }

        return workHour;
    }
}
