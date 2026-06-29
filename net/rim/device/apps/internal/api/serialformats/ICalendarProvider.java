package net.rim.device.apps.internal.api.serialformats;

import java.util.Date;

public interface ICalendarProvider extends ParserTypes {
   int VERSION_1_0;
   int VERSION_2_0;
   int CALENDAR_COMPONENT_NOT_SPECIFIED;
   int VCALENDAR;
   int VEVENT;
   int VTODO;
   int VALARM;
   int VJOURNAL;
   int VFREEBUSY;
   int VTIMEZONE;
   int DAYLIGHT;
   int STANDARD;
   int DATE;
   int DATE_TIME;
   int PERIOD;
   int FREQ;
   int UNTIL;
   int COUNT;
   int INTERVAL;
   int BYSECOND;
   int BYMINUTE;
   int BYHOUR;
   int BYDAY;
   int BYMONTHDAY;
   int BYYEARDAY;
   int BYWEEKNO;
   int BYMONTH;
   int BYSETPOS;
   int WKST;
   int NO_RECURRENCE;
   int SECONDLY;
   int MINUTELY;
   int HOURLY;
   int DAILY;
   int WEEKLY;
   int MONTHLY;
   int YEARLY;
   int MONTH_NOT_SPECIFIED;
   int JANUARY;
   int FEBRUARY;
   int MARCH;
   int APRIL;
   int MAY;
   int JUNE;
   int JULY;
   int AUGUST;
   int SEPTEMEBER;
   int OCTOBER;
   int NOVEMBER;
   int DECEMBER;
   int LAST_MONTH;
   int AUDIO;
   int DISPLAY;
   int EMAIL;
   int PROCEDURE;

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
