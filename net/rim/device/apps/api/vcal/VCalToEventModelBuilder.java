package net.rim.device.apps.api.vcal;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.reminders.Reminder;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.internal.api.serialformats.ICalendarProvider;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;

public final class VCalToEventModelBuilder implements ICalendarProvider {
   private Event _event;
   private String _version;
   private Date _startTime = (Date)(new Object());
   private Date _endTime = (Date)(new Object());
   private StringBuffer _subject = (StringBuffer)(new Object());
   private StringBuffer _notes = (StringBuffer)(new Object());
   private StringBuffer _location = (StringBuffer)(new Object());
   private int _vCalendarBeginTag;
   private int _alarmNestedBeginTag;
   private int _calendarComponent;
   private Date _triggerTime = (Date)(new Object());
   private int _triggerTimeRelative;
   private int _triggerSet;
   private int _alarmAction;
   private int _recurType;
   private int _recurPeriod;
   private int _recurCount = -1;
   private int _weekStart;
   private int[] _recurDaysInWeek = new int[0];
   private int[] _recurWeeksInMonth = new int[0];
   private int[] _recurDaysInMonth = new int[0];
   private int[] _recurMonthsInYear = new int[0];
   private int[] _recurBySetPos = new int[0];
   private Date _recurEndTime = (Date)(new Object());
   private Date[] _recurExceptions = new Object[0];
   private static final String VERSION_1_0;
   private static final String VERSION_2_0;
   private static final int ABSOLUTE;
   private static final int RELATIVE;

   public final VCal getEventModel() {
      if (this._version == null) {
         return null;
      }

      this._event.setSubject(this._subject.toString());
      this._event.setNotes(this._notes.toString());
      this._event.setLocation(this._location.toString());
      this._event.setStartDate(this._startTime.getTime(), TimeZone.getDefault());
      this._event.setInstanceDuration(this._endTime.getTime() - this._startTime.getTime());
      ReminderModel reminder = this._event.getReminderData();
      if (reminder != null) {
         if (this._triggerSet == 1) {
            reminder.setTime(this._triggerTimeRelative);
         } else if (this._triggerSet == 0) {
            reminder.setTime(this._startTime.getTime() - this._triggerTime.getTime());
         } else {
            reminder.setTime(-1);
         }
      }

      Recur r = this._event.getRecurrenceCopy();
      r.setRecurType((byte)this._recurType);
      r.setRecurPeriod(this._recurPeriod);
      r.setFirstDayOfWeek(this._weekStart);
      if (this.isUntilSpecified()) {
         r.setEndDate(this._recurEndTime.getTime());
      } else if (this._recurCount > 0) {
         CalendarExtensions calEx = (CalendarExtensions)Calendar.getInstance();
         calEx.setTimeLong(this._startTime.getTime());
         if (this._recurType == 1) {
            calEx.add(5, (this._recurCount - 1) * this._recurPeriod);
         } else if (this._recurType == 2) {
            calEx.add(5, 7 * (this._recurCount - 1) * this._recurPeriod);
         } else if (this._recurType == 3) {
            calEx.add(2, (this._recurCount - 1) * this._recurPeriod);
         } else if (this._recurType == 4) {
            calEx.add(1, (this._recurCount - 1) * this._recurPeriod);
         }

         r.setEndDate(calEx.getTimeLong());
      }

      switch (this._recurType) {
         case 1:
            break;
         case 2:
         default:
            for (int i = 0; i < this._recurDaysInWeek.length; i++) {
               RecurUtil.addDayOfWeekModifier(r, this._recurDaysInWeek[i] - 1, 0);
            }
            break;
         case 3:
            if (this.isDaysSpecified()) {
               for (int i = 0; i < this._recurDaysInWeek.length; i++) {
                  if (this._recurBySetPos.length > 0) {
                     RecurUtil.addDayOfWeekModifier(r, this._recurDaysInWeek[i] - 1, this._recurBySetPos[0]);
                  } else {
                     RecurUtil.addDayOfWeekModifier(r, this._recurDaysInWeek[i] - 1, this._recurWeeksInMonth[i]);
                  }
               }
            } else if (this.isMonthDaysSpecified()) {
               RecurUtil.setDayOfMonth(r, this._recurDaysInMonth[0]);
            }
            break;
         case 4:
            if (this.isDaysSpecified()) {
               for (int i = 0; i < this._recurDaysInWeek.length; i++) {
                  if (this._recurBySetPos.length > 0) {
                     RecurUtil.addDayOfWeekModifier(r, this._recurDaysInWeek[i] - 1, this._recurBySetPos[0]);
                  } else {
                     RecurUtil.addDayOfWeekModifier(r, this._recurDaysInWeek[i] - 1, this._recurWeeksInMonth[i]);
                  }
               }

               if (!this.isMonthsSpecified()) {
                  throw new Object("Invalid or unsupported recurrence rule specification");
               }

               RecurUtil.addMonthModifier(r, this._recurMonthsInYear[0]);
            }
      }

      this._event.setRecurrence(r);
      return new VCal(this._event);
   }

   public final void reset() {
      this._triggerSet = -1;
      this._triggerTimeRelative = 0;
      this._triggerTime.setTime(0);
      this._alarmAction = 0;
      this._recurType = 0;
      this._recurPeriod = 0;
      this._recurCount = -1;
      this._weekStart = 1;
      this._recurDaysInWeek = new int[0];
      this._recurWeeksInMonth = new int[0];
      this._recurDaysInMonth = new int[0];
      this._recurMonthsInYear = new int[0];
      this._recurBySetPos = new int[0];
      this._startTime.setTime(0);
      this._endTime.setTime(0);
      this._recurEndTime.setTime(0);
      this._subject.setLength(0);
      this._notes.setLength(0);
      this._location.setLength(0);
      this._vCalendarBeginTag = 0;
      this._alarmNestedBeginTag = 0;
      this._calendarComponent = 0;
      Array.resize(this._recurExceptions, 0);
      this._event = (Event)FactoryUtil.createInstance(-1986287563994289176L, null);
   }

   public final void setEvent(Event event) {
      this.reset();
      long relatedLUID = event.getRelatedLUID();
      if (relatedLUID != 0) {
         CalDB calDB = (CalDB)CalendarServiceManager.getInstance().findCalendarDatabase(event);
         synchronized (calDB.getLockObject()) {
            event = (Event)calDB.get(relatedLUID);
         }
      }

      this._event = event;
      TimeZone tz = TimeZone.getTimeZone(event.getTimeZoneID());
      this.setCalendarComponent(-1766506524);
      this.setVersion("VCALENDAR/1.0");
      this.setSummary(81434961, null, event.getSubject());
      this.setDescription(81434961, null, event.getNotes());
      this.setLocation(81434961, null, event.getLocation());
      long startDate = event.getStart(tz);
      long instanceDuration = event.getInstanceDuration();
      this.setDateTimeStart(81434961, -1773854324, (Date)(new Object(startDate)));
      this.setDateTimeEnd(81434961, -1773854324, (Date)(new Object(startDate + instanceDuration)));
      if (event.getRecurrenceCopy() != null) {
         this.populateRecurrencePropertiesFrom(event);
      }

      if (event instanceof Reminder) {
         ReminderModel rm = event.getReminderData();
         if (rm != null && rm.hasReminder()) {
            this._triggerTime.setTime(startDate - rm.getTime());
            this._triggerSet = 0;
         }
      }
   }

   protected final void populateRecurrencePropertiesFrom(Event event) {
      Recur recurInfo = event.getRecurrenceCopy();
      long[] exclusions = recurInfo.getExclusions(null);
      this._recurExceptions = new Object[exclusions.length];

      for (int i = 0; i < exclusions.length; i++) {
         this._recurExceptions[i] = (Date)(new Object(exclusions[i]));
      }

      this._recurType = recurInfo.getRecurType();
      this._recurPeriod = recurInfo.getRecurPeriod();
      this._weekStart = recurInfo.getFirstDayOfWeek();
      if (recurInfo.isFinite()) {
         this._recurEndTime.setTime(recurInfo.getEndDate());
      } else {
         this.setCount(0);
      }

      Recur$Modifier modifier = (Recur$Modifier)(new Object());
      switch (this._recurType) {
         case 1:
            return;
         case 2:
            break;
         case 4:
         default:
            for (int i = 0; i < recurInfo.numModifierValues(2); i++) {
               recurInfo.getModifierAt(2, i, modifier);
               this.setByMonth(modifier.parm1 + 1);
            }

            if (recurInfo.numModifierValues(2) == 0) {
               Calendar cal = Calendar.getInstance();
               ((CalendarExtensions)cal).setTimeLong(this._startTime.getTime());
               this.setByMonth(cal.get(2) + 1);
            }
         case 3:
            for (int i = 0; i < recurInfo.numModifierValues(3); i++) {
               recurInfo.getModifierAt(3, i, modifier);
               this.setByMonthDay(modifier.parm1);
            }
      }

      for (int i = 0; i < recurInfo.numModifierValues(1); i++) {
         recurInfo.getModifierAt(1, i, modifier);
         int weekDay = modifier.parm1 + 1;
         int orderWeek = modifier.parm2;
         this.setByDay(orderWeek, weekDay);
      }
   }

   public final boolean isDaysSpecified() {
      return this._recurDaysInWeek.length > 0;
   }

   @Override
   public final boolean isValidCalendarComponent(int type) {
      switch (type) {
         case -1766506525:
            return false;
         case -1766506524:
         default:
            return true;
      }
   }

   @Override
   public final void setVCalendarBeginTag(int vCalendarBeginTag) {
      this._vCalendarBeginTag = vCalendarBeginTag;
   }

   @Override
   public final int getVCalendarBeginTag() {
      return this._vCalendarBeginTag;
   }

   @Override
   public final void setAlarmNestedBeginTag(int alarmNestedBeginTag) {
      this._alarmNestedBeginTag = alarmNestedBeginTag;
   }

   @Override
   public final int getAlarmNestedBeginTag() {
      return this._alarmNestedBeginTag;
   }

   @Override
   public final void setCalendarScale(String calendarScale) {
   }

   @Override
   public final String getCalendarScale() {
      return null;
   }

   @Override
   public final void setProductId(String prodid) {
   }

   @Override
   public final String getProdId() {
      return "-//Research In Motion//Blackberry//EN";
   }

   @Override
   public final void setVersion(String version) {
      this._version = version;
   }

   @Override
   public final String getVersion() {
      return this._version;
   }

   @Override
   public final void setUID(String value) {
   }

   @Override
   public final String getUID() {
      return Integer.toString(this._event.getUID());
   }

   @Override
   public final void setDescription(int type, String paramValue, String description) {
      this._notes.setLength(0);
      if (description != null) {
         this._notes.append(description);
      }
   }

   @Override
   public final int getDescriptionType() {
      return 0;
   }

   @Override
   public final String getDescriptionParamValue() {
      return null;
   }

   @Override
   public final String getDescriptionValue() {
      return this._notes.toString();
   }

   @Override
   public final void setLocation(int type, String paramValue, String value) {
      this._location.setLength(0);
      if (value != null) {
         this._location.append(value);
      }
   }

   @Override
   public final int getLocationType() {
      return 0;
   }

   @Override
   public final String getLocationParamValue() {
      return null;
   }

   @Override
   public final String getLocationValue() {
      return this._location.toString();
   }

   @Override
   public final void setPriority(int value) {
   }

   @Override
   public final int getPriority() {
      return 0;
   }

   @Override
   public final void setDateTimeCompleted(Date date) {
   }

   @Override
   public final Date getDateTimeCompleted() {
      return null;
   }

   @Override
   public final void setDateTimeDue(int type, int paramType, Date dateTimeDueValue) {
   }

   @Override
   public final int getDateTimeDueType() {
      return 81434961;
   }

   @Override
   public final int getDateTimeDueParamType() {
      return -1773854324;
   }

   @Override
   public final Date getDateTimeDueValue() {
      return null;
   }

   @Override
   public final void setWeekStart(int weekStart) {
      this._weekStart = weekStart;
   }

   @Override
   public final int getWeekStart() {
      return this._weekStart;
   }

   @Override
   public final boolean isWeekStartSpecified(int weekday) {
      return weekday >= 1 && weekday <= 7;
   }

   @Override
   public final boolean isEmptyString(String str) {
      return str == null || str.length() == 0;
   }

   @Override
   public final int getCalendarComponent() {
      return this._calendarComponent;
   }

   @Override
   public final void setSummary(int type, String paramValue, String value) {
      this._subject.setLength(0);
      if (value != null) {
         this._subject.append(value);
      }
   }

   @Override
   public final String getSummaryValue() {
      return this._subject.toString();
   }

   @Override
   public final int getSummaryType() {
      return 0;
   }

   @Override
   public final String getSummaryParamValue() {
      return null;
   }

   @Override
   public final void setDateTimeStart(int type, int paramType, Date dateTimeStartValue) {
      this._startTime.setTime(dateTimeStartValue.getTime());
   }

   @Override
   public final int getDateTimeStartType() {
      return 81434961;
   }

   @Override
   public final int getDateTimeStartParamType() {
      return -1773854324;
   }

   @Override
   public final Date getDateTimeStartValue() {
      return this._startTime;
   }

   @Override
   public final void setDateTimeEnd(int type, int paramType, Date dateTimeEndValue) {
      this._endTime.setTime(dateTimeEndValue.getTime());
   }

   @Override
   public final int getDateTimeEndType() {
      return 81434961;
   }

   @Override
   public final int getDateTimeEndParamType() {
      return -1773854324;
   }

   @Override
   public final Date getDateTimeEndValue() {
      return this._endTime;
   }

   @Override
   public final void setAction(int actionValue) {
      if (this.isValidActionValue(actionValue)) {
         this._alarmAction = actionValue;
      }
   }

   @Override
   public final boolean isValidActionValue(int actionValue) {
      return actionValue == 62628790 || actionValue == -1905220446 || actionValue == 66081660 || actionValue == 1691390643;
   }

   @Override
   public final void setAlarmDescription(int type, String paramValue, String displayDescription) {
   }

   @Override
   public final int getAlarmDescriptionType() {
      return 0;
   }

   @Override
   public final String getAlarmDescriptionParamValue() {
      return null;
   }

   @Override
   public final String getAlarmDescriptionValue() {
      return null;
   }

   @Override
   public final int getAction() {
      return this._alarmAction;
   }

   @Override
   public final void setTrigger(int type, int paramType, Date date) {
      this._triggerTime.setTime(date.getTime());
      this._triggerSet = 0;
   }

   @Override
   public final Date getTrigger() {
      if (this._triggerSet == 0) {
         return this._triggerTime;
      } else {
         return (Date)(this._triggerSet == 1 ? new Object(this._startTime.getTime() - this._triggerTimeRelative) : null);
      }
   }

   @Override
   public final void setRelativeTrigger(int type, int paramType, int offset) {
      this._triggerTimeRelative = offset * 1000;
      this._triggerSet = 1;
   }

   @Override
   public final int getRelativeTrigger() {
      return this._triggerTimeRelative;
   }

   @Override
   public final int getTriggerType() {
      return StringUtilities.strEqual(this._version, "2.0") ? 81434961 : 0;
   }

   @Override
   public final int getTriggerParamType() {
      return 0;
   }

   @Override
   public final void setExceptionDateTime(int type, int paramType, Date[] date, int dateLength) {
      this._recurExceptions = new Object[date.length];

      for (int i = 0; i < date.length; i++) {
         this._recurExceptions[i] = (Date)(new Object(date[i].getTime()));
      }
   }

   @Override
   public final void setExceptionDateTime(int type, int paramType, Date date) {
      Array.resize(this._recurExceptions, this._recurExceptions.length + 1);
      this._recurExceptions[this._recurExceptions.length - 1] = (Date)(new Object(date.getTime()));
   }

   @Override
   public final int getExceptionDateTimeType() {
      return 0;
   }

   @Override
   public final int getExceptionDateTimeParamType() {
      return -1773854324;
   }

   @Override
   public final Date[] getExceptionDateTimeValue() {
      return null;
   }

   @Override
   public final int getExceptionDateLength() {
      return 0;
   }

   @Override
   public final void setFreq(int freqType) {
      this._recurType = 0;
      switch (freqType) {
         case -1738378111:
            this._recurType = 2;
            return;
         case -1681232246:
            this._recurType = 4;
         default:
            return;
         case 64808441:
            this._recurType = 1;
            return;
         case 1954618349:
            this._recurType = 3;
      }
   }

   @Override
   public final int getFreq() {
      int freq = -746168210;
      switch (this._recurType) {
         case 1:
         default:
            return 64808441;
         case 2:
            return -1738378111;
         case 3:
            return 1954618349;
         case 4:
            freq = -1681232246;
         case 0:
            return freq;
      }
   }

   @Override
   public final boolean isFreqSpecified() {
      return this._recurType != 0;
   }

   @Override
   public final void setUntil(int untilEndDateType, Date untilEndDate) {
      if (untilEndDate != null) {
         this._recurEndTime.setTime(untilEndDate.getTime());
      }
   }

   @Override
   public final Date getUntil() {
      return this._recurEndTime;
   }

   @Override
   public final int getUntilEndDateType() {
      return -1773854324;
   }

   @Override
   public final boolean isUntilSpecified() {
      return this._recurEndTime.getTime() != 0;
   }

   @Override
   public final void setCount(int count) {
      this._recurCount = count;
   }

   @Override
   public final int getCount() {
      return this._recurCount;
   }

   @Override
   public final boolean isCountSpecified() {
      return this._recurCount >= 0;
   }

   @Override
   public final void setInterval(int interval) {
      this._recurPeriod = interval;
   }

   @Override
   public final int getInterval() {
      return this._recurPeriod;
   }

   @Override
   public final boolean isIntervalSpecified() {
      return this._recurPeriod != 0;
   }

   @Override
   public final void setByDay(int[] orderWeek, int[] weekDay, int dayLength) {
      if (dayLength > 0) {
         for (int i = 0; i < dayLength; i++) {
            this.setByDay(orderWeek[i], weekDay[i]);
         }
      } else {
         this._recurDaysInWeek = new int[0];
         this._recurWeeksInMonth = new int[0];
      }
   }

   @Override
   public final void setByDay(int orderWeek, int weekDay) {
      int maxOrderWeek = 53;
      if (weekDay >= 1 && weekDay <= 7 && orderWeek >= -maxOrderWeek && orderWeek <= maxOrderWeek) {
         Arrays.add(this._recurDaysInWeek, weekDay);
         Arrays.add(this._recurWeeksInMonth, orderWeek);
      } else {
         throw new Object("invalid value for BYDAY");
      }
   }

   @Override
   public final int[] getByDayOrderWeek() {
      return this._recurWeeksInMonth;
   }

   @Override
   public final int[] getByDayWeekDay() {
      return this._recurDaysInWeek;
   }

   @Override
   public final int getByDayLength() {
      return this._recurDaysInWeek.length;
   }

   @Override
   public final void setCalendarComponent(int component) {
      this._calendarComponent = component;
   }

   @Override
   public final void setByMonthDay(int[] monthDay, int monthDayLength) {
      if (monthDayLength > 0) {
         for (int i = 0; i < monthDayLength; i++) {
            this.setByMonthDay(monthDay[i]);
         }
      } else {
         this._recurDaysInMonth = new int[0];
      }
   }

   @Override
   public final void setByMonthDay(int monthDay) {
      if (monthDay != 0 && monthDay >= -31 && monthDay <= 31) {
         if (monthDay < 0) {
            monthDay += 32;
         }

         Arrays.add(this._recurDaysInMonth, monthDay);
      } else {
         throw new Object("invalid value for BYMONTHDAY");
      }
   }

   @Override
   public final int[] getByMonthDay() {
      return this._recurDaysInMonth;
   }

   @Override
   public final int getByMonthDayLength() {
      return this._recurDaysInMonth.length;
   }

   @Override
   public final boolean isMonthDaysSpecified() {
      return this._recurDaysInMonth.length > 0;
   }

   @Override
   public final void setByYearDay(int[] yearDay, int yearDayLength) {
      if (yearDayLength > 0) {
         this.setByYearDay(yearDay[0]);
      }
   }

   @Override
   public final void setByYearDay(int yearDay) {
      if (yearDay <= 0 && (yearDay < -366 || yearDay > -1)) {
         throw new Object();
      }
   }

   @Override
   public final int[] getByYearDay() {
      return null;
   }

   @Override
   public final int getByYearDayLength() {
      return 0;
   }

   @Override
   public final boolean isYearDaysSpecified() {
      return false;
   }

   @Override
   public final void setByMonth(int[] month, int monthLength) {
      if (monthLength > 0) {
         for (int i = 0; i < monthLength; i++) {
            this.setByMonth(month[i]);
         }
      } else {
         this._recurMonthsInYear = new int[0];
      }
   }

   @Override
   public final void setByMonth(int month) {
      if (month > 0 && month < 13) {
         Arrays.add(this._recurMonthsInYear, month);
      } else {
         throw new Object("invalid value for BYMONTH");
      }
   }

   @Override
   public final int[] getByMonth() {
      return this._recurMonthsInYear;
   }

   @Override
   public final int getByMonthLength() {
      return this._recurMonthsInYear.length;
   }

   @Override
   public final boolean isMonthsSpecified() {
      return this._recurMonthsInYear.length > 0;
   }

   @Override
   public final void setBySetPos(int[] setPos, int setPosLength) {
      if (setPosLength > 0) {
         for (int i = 0; i < setPosLength; i++) {
            this.setBySetPos(setPos[i]);
         }
      } else {
         this._recurBySetPos = new int[0];
      }
   }

   @Override
   public final void setBySetPos(int setPos) {
      if (setPos != 0 && setPos >= -366 && setPos <= 366) {
         Arrays.add(this._recurBySetPos, setPos);
      } else {
         throw new Object("invalid value for BYSETPOS");
      }
   }

   @Override
   public final int[] getBySetPos() {
      return this._recurBySetPos;
   }

   @Override
   public final int getBySetPosLength() {
      return this._recurBySetPos.length;
   }

   @Override
   public final boolean isSetPosSpecified() {
      return this._recurBySetPos.length > 0;
   }

   @Override
   public final void setBySecond(int[] seconds, int secondLength) {
   }

   @Override
   public final void setBySecond(int second) {
   }

   @Override
   public final int[] getBySecond() {
      return null;
   }

   @Override
   public final int getBySecondLength() {
      return 0;
   }

   @Override
   public final boolean isSecondsSpecified() {
      return false;
   }

   @Override
   public final void setByMinute(int[] minute, int minuteLength) {
   }

   @Override
   public final void setByMinute(int minute) {
   }

   @Override
   public final int[] getByMinute() {
      return null;
   }

   @Override
   public final int getByMinuteLength() {
      return 0;
   }

   @Override
   public final boolean isMinutesSpecified() {
      return false;
   }

   @Override
   public final void setByHour(int[] hours, int hourLength) {
   }

   @Override
   public final void setByHour(int hour) {
   }

   @Override
   public final int[] getByHour() {
      return null;
   }

   @Override
   public final int getByHourLength() {
      return 0;
   }

   @Override
   public final boolean isHoursSpecified() {
      return false;
   }

   @Override
   public final void setByWeekNo(int[] weekNo, int weekNoLength) {
   }

   @Override
   public final void setByWeekNo(int weekNo) {
   }

   @Override
   public final int[] getByWeekNo() {
      return null;
   }

   @Override
   public final int getByWeekNoLength() {
      return 0;
   }

   @Override
   public final boolean isValidWeekNo(int weekNo) {
      return false;
   }

   @Override
   public final boolean isWeekNoSpecified() {
      return false;
   }
}
