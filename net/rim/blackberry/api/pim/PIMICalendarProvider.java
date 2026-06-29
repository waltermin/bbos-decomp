package net.rim.blackberry.api.pim;

import java.util.Date;
import net.rim.device.apps.internal.api.serialformats.ICalendarProvider;

class PIMICalendarProvider implements ICalendarProvider {
   private int _calendarComponent;
   private int _alarmNestedCalendarComponent;
   protected int _version = 0;
   private static final String _prodid = "-//Research In Motion//Blackberry//EN";
   private static final String[] _versionArray = new String[]{"VCALENDAR/1.0", "VCALENDAR/2.0"};
   static final int VERSION_1_0 = 0;
   static final int VERSION_2_0 = 1;

   public boolean isDaysSpecified() {
      return false;
   }

   protected String getUIDString() {
      return null;
   }

   @Override
   public boolean isValidCalendarComponent(int type) {
      switch (type) {
         case -1770502245:
         case -1766506524:
         case 82003356:
            return true;
         default:
            return false;
      }
   }

   @Override
   public void setVCalendarBeginTag(int vCalendarBeginTag) {
      if (vCalendarBeginTag == -217959020) {
         this._calendarComponent = vCalendarBeginTag;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int getVCalendarBeginTag() {
      return this._calendarComponent;
   }

   @Override
   public void setAlarmNestedBeginTag(int alarmNestedBeginTag) {
      if (alarmNestedBeginTag == -1770502245) {
         this._alarmNestedCalendarComponent = alarmNestedBeginTag;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int getAlarmNestedBeginTag() {
      return this._alarmNestedCalendarComponent;
   }

   @Override
   public void setCalendarScale(String calendarScale) {
   }

   @Override
   public String getCalendarScale() {
      return null;
   }

   @Override
   public void setProductId(String prodid) {
   }

   @Override
   public String getProdId() {
      return "-//Research In Motion//Blackberry//EN";
   }

   @Override
   public void setVersion(String version) {
      if ("2.0".equals(version) || version.equals("VCALENDAR/2.0")) {
         this._version = 1;
      } else {
         if (!"1.0".equals(version) && !version.equals("VCALENDAR/1.0")) {
            throw new IllegalArgumentException("invalid value");
         }

         this._version = 0;
      }
   }

   @Override
   public String getVersion() {
      return _versionArray[this._version];
   }

   @Override
   public int getCalendarComponent() {
      return 0;
   }

   @Override
   public void setUID(String value) {
   }

   @Override
   public String getUID() {
      return this._version == 1 ? this.getUIDString() : null;
   }

   @Override
   public void setDescription(int type, String paramValue, String description) {
   }

   @Override
   public int getDescriptionType() {
      return 0;
   }

   @Override
   public String getDescriptionParamValue() {
      return null;
   }

   @Override
   public String getDescriptionValue() {
      return null;
   }

   @Override
   public void setSummary(int type, String paramValue, String value) {
   }

   @Override
   public int getSummaryType() {
      return 0;
   }

   @Override
   public String getSummaryParamValue() {
      return null;
   }

   @Override
   public String getSummaryValue() {
      return null;
   }

   @Override
   public void setLocation(int type, String paramValue, String value) {
   }

   @Override
   public int getLocationType() {
      return 0;
   }

   @Override
   public String getLocationParamValue() {
      return null;
   }

   @Override
   public String getLocationValue() {
      return null;
   }

   @Override
   public void setPriority(int value) {
   }

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public void setDateTimeStart(int type, int paramType, Date dateTimeStartValue) {
   }

   @Override
   public int getDateTimeStartType() {
      return 0;
   }

   @Override
   public int getDateTimeStartParamType() {
      return 0;
   }

   @Override
   public Date getDateTimeStartValue() {
      return null;
   }

   @Override
   public void setDateTimeEnd(int type, int paramType, Date dateTimeEndValue) {
   }

   @Override
   public int getDateTimeEndType() {
      return 0;
   }

   @Override
   public int getDateTimeEndParamType() {
      return 0;
   }

   @Override
   public Date getDateTimeEndValue() {
      return null;
   }

   @Override
   public void setDateTimeCompleted(Date date) {
   }

   @Override
   public Date getDateTimeCompleted() {
      return null;
   }

   @Override
   public void setDateTimeDue(int type, int paramType, Date dateTimeDueValue) {
   }

   @Override
   public int getDateTimeDueType() {
      return 81434961;
   }

   @Override
   public int getDateTimeDueParamType() {
      return -1773854324;
   }

   @Override
   public Date getDateTimeDueValue() {
      return null;
   }

   @Override
   public boolean isValidActionValue(int actionValue) {
      return actionValue >= 1 && actionValue <= 4;
   }

   @Override
   public void setAction(int actionValue) {
   }

   @Override
   public int getAction() {
      return 0;
   }

   @Override
   public void setTrigger(int type, int paramType, Date date) {
   }

   @Override
   public Date getTrigger() {
      return null;
   }

   @Override
   public void setRelativeTrigger(int type, int paramType, int offset) {
   }

   @Override
   public int getRelativeTrigger() {
      return 0;
   }

   @Override
   public int getTriggerType() {
      return 0;
   }

   @Override
   public int getTriggerParamType() {
      return 0;
   }

   @Override
   public void setAlarmDescription(int type, String paramValue, String displayDescription) {
   }

   @Override
   public int getAlarmDescriptionType() {
      return 0;
   }

   @Override
   public String getAlarmDescriptionParamValue() {
      return null;
   }

   @Override
   public String getAlarmDescriptionValue() {
      return null;
   }

   @Override
   public void setExceptionDateTime(int type, int paramType, Date[] date, int dateLength) {
   }

   @Override
   public void setExceptionDateTime(int type, int paramType, Date date) {
   }

   @Override
   public int getExceptionDateTimeType() {
      return 0;
   }

   @Override
   public int getExceptionDateTimeParamType() {
      return 0;
   }

   @Override
   public Date[] getExceptionDateTimeValue() {
      return null;
   }

   @Override
   public int getExceptionDateLength() {
      return 0;
   }

   @Override
   public void setFreq(int freqType) {
   }

   @Override
   public int getFreq() {
      return 0;
   }

   @Override
   public boolean isFreqSpecified() {
      return false;
   }

   @Override
   public void setUntil(int untilEndDateType, Date untilEndDate) {
   }

   @Override
   public Date getUntil() {
      return null;
   }

   @Override
   public int getUntilEndDateType() {
      return 0;
   }

   @Override
   public boolean isUntilSpecified() {
      return false;
   }

   @Override
   public void setCount(int count) {
   }

   @Override
   public int getCount() {
      return 0;
   }

   @Override
   public boolean isCountSpecified() {
      return false;
   }

   @Override
   public void setInterval(int interval) {
   }

   @Override
   public int getInterval() {
      return 0;
   }

   @Override
   public boolean isIntervalSpecified() {
      return false;
   }

   @Override
   public void setBySecond(int[] seconds, int secondLength) {
   }

   @Override
   public void setBySecond(int second) {
   }

   @Override
   public int[] getBySecond() {
      return null;
   }

   @Override
   public int getBySecondLength() {
      return 0;
   }

   @Override
   public boolean isSecondsSpecified() {
      return false;
   }

   @Override
   public void setByMinute(int[] minute, int minuteLength) {
   }

   @Override
   public void setByMinute(int minute) {
   }

   @Override
   public int[] getByMinute() {
      return null;
   }

   @Override
   public int getByMinuteLength() {
      return 0;
   }

   @Override
   public boolean isMinutesSpecified() {
      return false;
   }

   @Override
   public void setByHour(int[] hours, int hourLength) {
   }

   @Override
   public void setByHour(int hour) {
   }

   @Override
   public int[] getByHour() {
      return null;
   }

   @Override
   public int getByHourLength() {
      return 0;
   }

   @Override
   public boolean isHoursSpecified() {
      return false;
   }

   @Override
   public void setByDay(int[] orderWeek, int[] weekDay, int dayLength) {
   }

   @Override
   public void setByDay(int orderWeek, int weekDay) {
   }

   @Override
   public int[] getByDayOrderWeek() {
      return null;
   }

   @Override
   public int[] getByDayWeekDay() {
      return null;
   }

   @Override
   public int getByDayLength() {
      return 0;
   }

   @Override
   public void setCalendarComponent(int component) {
      if (component != this.getCalendarComponent()) {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public void setByMonthDay(int[] monthDay, int monthDayLength) {
   }

   @Override
   public void setByMonthDay(int monthDay) {
   }

   @Override
   public int[] getByMonthDay() {
      return null;
   }

   @Override
   public int getByMonthDayLength() {
      return 0;
   }

   @Override
   public boolean isMonthDaysSpecified() {
      return false;
   }

   @Override
   public void setByYearDay(int[] yearDay, int yearDayLength) {
   }

   @Override
   public void setByYearDay(int yearDay) {
   }

   @Override
   public int[] getByYearDay() {
      return null;
   }

   @Override
   public int getByYearDayLength() {
      return 0;
   }

   @Override
   public boolean isYearDaysSpecified() {
      return false;
   }

   @Override
   public void setByWeekNo(int[] weekNo, int weekNoLength) {
   }

   @Override
   public void setByWeekNo(int weekNo) {
   }

   @Override
   public int[] getByWeekNo() {
      return null;
   }

   @Override
   public int getByWeekNoLength() {
      return 0;
   }

   @Override
   public boolean isValidWeekNo(int weekNo) {
      return weekNo >= 1 && weekNo <= 53;
   }

   @Override
   public boolean isWeekNoSpecified() {
      return false;
   }

   @Override
   public void setByMonth(int[] month, int monthLength) {
   }

   @Override
   public void setByMonth(int month) {
   }

   @Override
   public int[] getByMonth() {
      return null;
   }

   @Override
   public int getByMonthLength() {
      return 0;
   }

   @Override
   public boolean isMonthsSpecified() {
      return false;
   }

   @Override
   public void setBySetPos(int[] setPos, int setPosLength) {
   }

   @Override
   public void setBySetPos(int setPos) {
   }

   @Override
   public int[] getBySetPos() {
      return null;
   }

   @Override
   public int getBySetPosLength() {
      return 0;
   }

   @Override
   public boolean isSetPosSpecified() {
      return false;
   }

   @Override
   public void setWeekStart(int weekStart) {
   }

   @Override
   public int getWeekStart() {
      return 2;
   }

   @Override
   public boolean isWeekStartSpecified(int weekday) {
      return weekday >= 1 && weekday <= 7;
   }

   @Override
   public boolean isEmptyString(String str) {
      return str == null || str.length() == 0;
   }

   public PIMICalendarProvider() {
   }
}
