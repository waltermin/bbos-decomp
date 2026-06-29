package net.rim.device.apps.internal.api.serialformats;

import java.util.Date;

public interface ICalendarProvider extends ParserTypes {
   int VERSION_1_0 = 1;
   int VERSION_2_0 = 2;
   int CALENDAR_COMPONENT_NOT_SPECIFIED = 0;
   int VCALENDAR = -217959020;
   int VEVENT = -1766506524;
   int VTODO = 82003356;
   int VALARM = -1770502245;
   int VJOURNAL = -1143648767;
   int VFREEBUSY = -1577546565;
   int VTIMEZONE = -2115861937;
   int DAYLIGHT = -1569357062;
   int STANDARD = 2095255229;
   int DATE = 2090926;
   int DATE_TIME = -1773854324;
   int PERIOD = -1938396735;
   int FREQ = 2166392;
   int UNTIL = 80906046;
   int COUNT = 64313583;
   int INTERVAL = 1353045189;
   int BYSECOND = -1113169013;
   int BYMINUTE = -1280916181;
   int BYHOUR = 1973940923;
   int BYDAY = 63671237;
   int BYMONTHDAY = -1571028365;
   int BYYEARDAY = 879786472;
   int BYWEEKNO = -998596660;
   int BYMONTH = 1067237481;
   int BYSETPOS = -1112661559;
   int WKST = 2666549;
   int NO_RECURRENCE = -746168210;
   int SECONDLY = 1726084353;
   int MINUTELY = -565154143;
   int HOURLY = 2136870513;
   int DAILY = 64808441;
   int WEEKLY = -1738378111;
   int MONTHLY = 1954618349;
   int YEARLY = -1681232246;
   int MONTH_NOT_SPECIFIED = 0;
   int JANUARY = 1;
   int FEBRUARY = 2;
   int MARCH = 3;
   int APRIL = 4;
   int MAY = 5;
   int JUNE = 6;
   int JULY = 7;
   int AUGUST = 8;
   int SEPTEMEBER = 9;
   int OCTOBER = 10;
   int NOVEMBER = 11;
   int DECEMBER = 12;
   int LAST_MONTH = 13;
   int AUDIO = 62628790;
   int DISPLAY = -1905220446;
   int EMAIL = 66081660;
   int PROCEDURE = 1691390643;

   void setCalendarComponent(int var1);

   int getCalendarComponent();

   boolean isValidCalendarComponent(int var1);

   void setVCalendarBeginTag(int var1);

   int getVCalendarBeginTag();

   void setAlarmNestedBeginTag(int var1);

   int getAlarmNestedBeginTag();

   void setCalendarScale(String var1);

   String getCalendarScale();

   void setProductId(String var1);

   String getProdId();

   void setVersion(String var1);

   String getVersion();

   void setUID(String var1);

   String getUID();

   void setDescription(int var1, String var2, String var3);

   int getDescriptionType();

   String getDescriptionParamValue();

   String getDescriptionValue();

   void setSummary(int var1, String var2, String var3);

   int getSummaryType();

   String getSummaryParamValue();

   String getSummaryValue();

   void setLocation(int var1, String var2, String var3);

   int getLocationType();

   String getLocationParamValue();

   String getLocationValue();

   void setPriority(int var1);

   int getPriority();

   void setDateTimeStart(int var1, int var2, Date var3);

   int getDateTimeStartType();

   int getDateTimeStartParamType();

   Date getDateTimeStartValue();

   void setDateTimeEnd(int var1, int var2, Date var3);

   int getDateTimeEndType();

   int getDateTimeEndParamType();

   Date getDateTimeEndValue();

   void setDateTimeCompleted(Date var1);

   Date getDateTimeCompleted();

   void setDateTimeDue(int var1, int var2, Date var3);

   int getDateTimeDueType();

   int getDateTimeDueParamType();

   Date getDateTimeDueValue();

   void setAction(int var1);

   int getAction();

   boolean isValidActionValue(int var1);

   void setTrigger(int var1, int var2, Date var3);

   Date getTrigger();

   void setRelativeTrigger(int var1, int var2, int var3);

   int getRelativeTrigger();

   int getTriggerType();

   int getTriggerParamType();

   void setAlarmDescription(int var1, String var2, String var3);

   int getAlarmDescriptionType();

   String getAlarmDescriptionParamValue();

   String getAlarmDescriptionValue();

   void setExceptionDateTime(int var1, int var2, Date[] var3, int var4);

   void setExceptionDateTime(int var1, int var2, Date var3);

   int getExceptionDateTimeType();

   int getExceptionDateTimeParamType();

   Date[] getExceptionDateTimeValue();

   int getExceptionDateLength();

   void setFreq(int var1);

   int getFreq();

   boolean isFreqSpecified();

   void setUntil(int var1, Date var2);

   Date getUntil();

   int getUntilEndDateType();

   boolean isUntilSpecified();

   void setCount(int var1);

   int getCount();

   boolean isCountSpecified();

   void setInterval(int var1);

   int getInterval();

   boolean isIntervalSpecified();

   void setBySecond(int[] var1, int var2);

   void setBySecond(int var1);

   int[] getBySecond();

   int getBySecondLength();

   boolean isSecondsSpecified();

   void setByMinute(int[] var1, int var2);

   void setByMinute(int var1);

   int[] getByMinute();

   int getByMinuteLength();

   boolean isMinutesSpecified();

   void setByHour(int[] var1, int var2);

   void setByHour(int var1);

   int[] getByHour();

   int getByHourLength();

   boolean isHoursSpecified();

   void setByDay(int[] var1, int[] var2, int var3);

   void setByDay(int var1, int var2);

   int[] getByDayOrderWeek();

   int[] getByDayWeekDay();

   int getByDayLength();

   void setByMonthDay(int[] var1, int var2);

   void setByMonthDay(int var1);

   int[] getByMonthDay();

   int getByMonthDayLength();

   boolean isMonthDaysSpecified();

   void setByYearDay(int[] var1, int var2);

   void setByYearDay(int var1);

   int[] getByYearDay();

   int getByYearDayLength();

   boolean isYearDaysSpecified();

   void setByWeekNo(int[] var1, int var2);

   void setByWeekNo(int var1);

   int[] getByWeekNo();

   int getByWeekNoLength();

   boolean isValidWeekNo(int var1);

   boolean isWeekNoSpecified();

   void setByMonth(int[] var1, int var2);

   void setByMonth(int var1);

   int[] getByMonth();

   int getByMonthLength();

   boolean isMonthsSpecified();

   void setBySetPos(int[] var1, int var2);

   void setBySetPos(int var1);

   int[] getBySetPos();

   int getBySetPosLength();

   boolean isSetPosSpecified();

   void setWeekStart(int var1);

   int getWeekStart();

   boolean isWeekStartSpecified(int var1);

   boolean isEmptyString(String var1);
}
