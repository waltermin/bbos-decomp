package net.rim.device.apps.internal.api.serialformats;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.i18n.SimpleDateFormat;

public final class ICalendarWriter extends TokenWriter implements ICalendarDefine {
   private ICalendarProvider _iCalendar;
   private String _version;
   StringBuffer _rrulePropertyTag = null;
   private static final char VCAL_COUNT_START = '#';

   private static final String getCalendarComponent(int type) {
      switch (type) {
         case -1766506524:
            return "VEVENT";
         case 82003356:
            return "VTODO";
         default:
            throw new InvalidFormatException();
      }
   }

   private static final String getParam(int param) {
      switch (param) {
         case -2024440166:
            return "MEMBER=";
         case -1801118381:
            return "ENCODING=";
         case -1596611924:
            return "SENT-BY";
         case -830962856:
            return "LANGUAGE=";
         case -420507304:
            return "DELEGATED-FROM=";
         case -14640857:
            return "FMTTYPE=";
         case 2155:
            return "CN=";
         case 67693:
            return "DIR=";
         case 2521206:
            return "ROLE=";
         case 2525371:
            return "RSVP=";
         case 2591265:
            return "TZID=";
         case 77742365:
            return "RANGE=";
         case 81434961:
            return "VALUE=";
         case 1808476171:
            return "RELATED=";
         case 1809047347:
            return "RELTYPE=";
         case 1933665876:
            return "ALTREP=";
         case 1971792071:
            return "PARTSTAT=";
         case 1999242924:
            return "CUTYPE=";
         case 2050957225:
            return "DELEGATED-TO=";
         case 2067583478:
            return "FBTYPE=";
         default:
            throw new InvalidFormatException();
      }
   }

   private static final String getValue(int value) {
      switch (value) {
         case -1938396735:
            return "PERIOD";
         case -1773854324:
            return "DATE-TIME:";
         case 2090926:
            return "DATE:";
         default:
            throw new InvalidFormatException();
      }
   }

   private static final String getFreq(int freq) {
      switch (freq) {
         case -1738378111:
            return "WEEKLY";
         case -1681232246:
            return "YEARLY";
         case -565154143:
            return "MINUTELY";
         case 64808441:
            return "DAILY";
         case 1726084353:
            return "SECONDLY";
         case 1954618349:
            return "MONTHLY";
         case 2136870513:
            return "HOURLY";
         default:
            throw new InvalidFormatException();
      }
   }

   private static final String getWeekDay(int day) {
      switch (day) {
         case 0:
            throw new InvalidFormatException();
         case 1:
         default:
            return "SU";
         case 2:
            return "MO";
         case 3:
            return "TU";
         case 4:
            return "WE";
         case 5:
            return "TH";
         case 6:
            return "FR";
         case 7:
            return "SA";
      }
   }

   private static final String getRecurType(int type) {
      switch (type) {
         case 59:
            throw new InvalidFormatException();
         case 60:
         default:
            return "D";
         case 61:
            return "W";
         case 62:
            return "MP";
         case 63:
            return "MD";
         case 64:
            return "YM";
         case 65:
            return "YD";
      }
   }

   private static final String getAction(int action) {
      switch (action) {
         case -1905220446:
            return "DISPLAY";
         case 62628790:
            return "AUDIO";
         case 66081660:
            return "EMAIL";
         case 1691390643:
            return "PROCEDURE";
         default:
            throw new InvalidFormatException();
      }
   }

   public ICalendarWriter(ICalendarProvider iCalendar, OutputStream os, String encoding) {
      super(os, encoding);
      this._iCalendar = iCalendar;
   }

   public final void encodeICalendar() {
      int calendarComponent = this._iCalendar.getCalendarComponent();
      if (calendarComponent != -1766506524 && calendarComponent != 82003356) {
         throw new InvalidFormatException();
      }

      this.resetLength();
      this.addPropertyTag("BEGIN:VCALENDAR");
      this.addLineBreak();
      this.buildCALSCALE();
      this.buildPRODID();
      this.buildVERSION();
      this.buildMETHOD();
      if (calendarComponent == -1766506524 || calendarComponent == 82003356) {
         this.buildBeginEndCalendarComponent(true);
         this.buildUID();
         this.buildDTSTAMP();
         this.buildDESCRIPTION();
         this.buildLOCATION();
         this.buildSUMMARY();
         this.buildPRIORITY();
         this.buildDTSTART();
      }

      if (calendarComponent == -1766506524) {
         this.buildDTEND();
         this.buildEXDATE();
         this.buildRRULE();
      }

      if (calendarComponent == 82003356) {
         this.buildCOMPLETED();
         this.buildDUE();
      }

      this.checkVersion();
      if ("VCALENDAR/1.0".equals(this._version)) {
         this.buildDALARM();
      } else if ("VCALENDAR/2.0".equals(this._version) && this._iCalendar.getAlarmNestedBeginTag() == -1770502245) {
         this.buildBeginEndAlarmComponent(true);
         this.buildACTION();
         this.buildTRIGGER();
         this.buildAlarmDescription();
         this.buildBeginEndAlarmComponent(false);
      }

      if (calendarComponent == -1766506524 || calendarComponent == 82003356) {
         this.buildBeginEndCalendarComponent(false);
      }

      this.addPropertyTag("END:VCALENDAR");
      this.addLineBreak();
   }

   private final void buildBeginEndCalendarComponent(boolean isBegin) {
      int type = this._iCalendar.getCalendarComponent();
      if (this._iCalendar.isValidCalendarComponent(type)) {
         StringBuffer begin = (StringBuffer)(new Object(isBegin ? "BEGIN:" : "END:"));
         begin.append(getCalendarComponent(type));
         this.addPropertyTag(begin.toString());
         this.addLineBreak();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void buildBeginEndAlarmComponent(boolean isBegin) {
      int type = this._iCalendar.getAlarmNestedBeginTag();
      if (type == -1770502245) {
         this.addPropertyTag(isBegin ? "BEGIN:VALARM" : "END:VALARM");
         this.addLineBreak();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void buildCALSCALE() {
      String calendarScale = this._iCalendar.getCalendarScale();
      if (!this._iCalendar.isEmptyString(calendarScale)) {
         this.addProperty("CALSCALE:", calendarScale);
      }
   }

   private final void buildPRODID() {
      String prodId = this._iCalendar.getProdId();
      if (!this._iCalendar.isEmptyString(prodId)) {
         this.addProperty("PRODID:", prodId);
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void buildVERSION() {
      String ver = this._iCalendar.getVersion();
      if (ver != null) {
         String serialVersion = null;
         if (ver.equals("VCALENDAR/1.0")) {
            this._version = "VCALENDAR/1.0";
            serialVersion = "1.0";
         } else {
            if (!ver.equals("VCALENDAR/2.0")) {
               throw new InvalidFormatException();
            }

            this._version = "VCALENDAR/2.0";
            serialVersion = "2.0";
         }

         StringBuffer version = (StringBuffer)(new Object("VERSION:"));
         version.append(serialVersion);
         this.addPropertyTag(version.toString());
         this.addLineBreak();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void buildMETHOD() {
      if (this._version.equals("VCALENDAR/2.0")) {
         this.addPropertyTag("METHOD:PUBLISH");
         this.addLineBreak();
      }
   }

   private final void buildDESCRIPTION() {
      int type = this._iCalendar.getDescriptionType();
      String paramValue = this._iCalendar.getDescriptionParamValue();
      String value = this._iCalendar.getDescriptionValue();
      if (!this._iCalendar.isEmptyString(value)) {
         if (type == 0) {
            this.addProperty("DESCRIPTION:", value);
            return;
         }

         StringBuffer description = (StringBuffer)(new Object("DESCRIPTION;"));
         if (!this._iCalendar.isEmptyString(paramValue)) {
            description.append(getParam(type));
            description.append(paramValue);
            this.addPropertyTag(description.toString());
            this.addColonSeparator();
            this.addString(value);
            this.addLineBreak();
         }
      }
   }

   private final void buildLOCATION() {
      int type = this._iCalendar.getLocationType();
      String paramValue = this._iCalendar.getLocationParamValue();
      String value = this._iCalendar.getLocationValue();
      if (!this._iCalendar.isEmptyString(value)) {
         if (type == 0) {
            this.addProperty("LOCATION:", value);
            return;
         }

         StringBuffer location = (StringBuffer)(new Object("LOCATION;"));
         location.append(getParam(type));
         location.append(paramValue);
         this.addPropertyTag(location.toString());
         this.addColonSeparator();
         this.addString(value);
         this.addLineBreak();
      }
   }

   private final void buildPRIORITY() {
      int value = this._iCalendar.getPriority();
      if (value > 0) {
         this.addProperty("PRIORITY:", Integer.toString(value));
      }
   }

   private final void buildSUMMARY() {
      int type = this._iCalendar.getSummaryType();
      String paramValue = this._iCalendar.getSummaryParamValue();
      String value = this._iCalendar.getSummaryValue();
      if (!this._iCalendar.isEmptyString(value)) {
         if (type == 0) {
            this.addProperty("SUMMARY:", value);
            return;
         }

         StringBuffer summary = (StringBuffer)(new Object("SUMMARY;"));
         summary.append(getParam(type));
         summary.append(paramValue);
         this.addPropertyTag(summary.toString());
         this.addColonSeparator();
         this.addString(value);
         this.addLineBreak();
      }
   }

   private final void buildCOMPLETED() {
      String dateTimeString = null;
      Date originalDate = this._iCalendar.getDateTimeCompleted();
      if (originalDate != null) {
         dateTimeString = this.convertDateToString(originalDate, true);
         if (dateTimeString != null) {
            this.addProperty("COMPLETED:", dateTimeString);
         }
      }
   }

   private final void buildDTEND() {
      String dateTimeString = null;
      int type = this._iCalendar.getDateTimeEndType();
      int paramType = this._iCalendar.getDateTimeEndParamType();
      Date originalDate = this._iCalendar.getDateTimeEndValue();
      boolean isVersion1 = "VCALENDAR/1.0".equals(this._version);
      if (isVersion1) {
         paramType = -1773854324;
      }

      if (originalDate != null) {
         dateTimeString = this.convertDateToString(originalDate, true);
         if (dateTimeString != null) {
            if (type == 0 || isVersion1) {
               this.addProperty("DTEND:", dateTimeString);
               return;
            }

            StringBuffer dtend = (StringBuffer)(new Object("DTEND;VALUE="));
            dtend.append(getValue(paramType));
            this.addPropertyTag(dtend.toString());
            this.addString(dateTimeString);
            this.addLineBreak();
         }
      }
   }

   private final void buildDUE() {
      String dateTimeString = null;
      int type = this._iCalendar.getDateTimeDueType();
      int paramType = this._iCalendar.getDateTimeDueParamType();
      Date originalDate = this._iCalendar.getDateTimeDueValue();
      boolean isVersion1 = "VCALENDAR/1.0".equals(this._version);
      if (isVersion1) {
         paramType = -1773854324;
      }

      if (originalDate != null) {
         if (this._iCalendar.getDateTimeStartValue() != null && originalDate.getTime() < this._iCalendar.getDateTimeStartValue().getTime()) {
            throw new Object();
         }

         dateTimeString = this.convertDateToString(originalDate, true);
         if (dateTimeString != null) {
            if (type == 0 || isVersion1) {
               this.addProperty("DUE:", dateTimeString);
               return;
            }

            StringBuffer due = (StringBuffer)(new Object("DUE;VALUE="));
            due.append(getValue(paramType));
            this.addPropertyTag(due.toString());
            this.addString(dateTimeString);
            this.addLineBreak();
         }
      }
   }

   private final void buildDTSTART() {
      String dateTimeString = null;
      int type = this._iCalendar.getDateTimeStartType();
      int paramType = this._iCalendar.getDateTimeStartParamType();
      Date originalDate = this._iCalendar.getDateTimeStartValue();
      boolean isVersion1 = "VCALENDAR/1.0".equals(this._version);
      if (isVersion1) {
         paramType = -1773854324;
      }

      if (originalDate != null) {
         dateTimeString = this.convertDateToString(originalDate, true);
         if (dateTimeString != null) {
            if (type == 0 || isVersion1) {
               this.addProperty("DTSTART:", dateTimeString);
               return;
            }

            StringBuffer dtstart = (StringBuffer)(new Object("DTSTART;VALUE="));
            dtstart.append(getValue(paramType));
            this.addPropertyTag(dtstart.toString());
            this.addString(dateTimeString);
            this.addLineBreak();
         }
      }
   }

   private final String convertDateToString(Date originalDate, boolean toUTCTime) {
      Calendar calendar;
      if (toUTCTime) {
         calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      } else {
         calendar = Calendar.getInstance();
      }

      calendar.setTime(originalDate);
      SimpleDateFormat sdf = (SimpleDateFormat)(new Object("yyyyMMdd'T'HHmmss'Z'"));
      StringBuffer dateSB = (StringBuffer)(new Object());
      sdf.format(calendar, dateSB, null);
      return dateSB.toString();
   }

   private final void buildUID() {
      String value = this._iCalendar.getUID();
      if (value != null) {
         this.addProperty("UID:", value);
      } else {
         if (value == null && this._version.compareTo("VCALENDAR/2.0") == 0) {
            throw new InvalidFormatException();
         }
      }
   }

   private final void buildDTSTAMP() {
      if (this._version.equals("VCALENDAR/2.0")) {
         String value = this.convertDateToString((Date)(new Object()), true);
         this.addProperty("DTSTAMP:", value);
      }
   }

   private final void buildEXDATE() {
      String dateTimeString = null;
      this.checkVersion();
      String dateDelimiter = "";
      if ("VCALENDAR/1.0".equals(this._version)) {
         dateDelimiter = ";";
      } else {
         if (!"VCALENDAR/2.0".equals(this._version)) {
            throw new InvalidFormatException();
         }

         dateDelimiter = ",";
      }

      int type = this._iCalendar.getExceptionDateTimeType();
      int paramType = this._iCalendar.getExceptionDateTimeParamType();
      Date[] originalDate = this._iCalendar.getExceptionDateTimeValue();
      boolean isVersion1 = "VCALENDAR/1.0".equals(this._version);
      if (isVersion1) {
         paramType = -1773854324;
      }

      StringBuffer exDateTimeString = (StringBuffer)(new Object());
      if (originalDate != null) {
         int len = this._iCalendar.getExceptionDateLength();

         for (int i = 0; i < len; i++) {
            dateTimeString = this.convertDateToString(originalDate[i], true);
            if (dateTimeString != null) {
               if (type != 0 && !isVersion1) {
                  if (i == 0) {
                     StringBuffer propertyTag = (StringBuffer)(new Object("EXDATE;VALUE="));
                     String paramTypeString = getValue(paramType);
                     propertyTag.append(paramTypeString);
                     this.addPropertyTag(propertyTag.toString());
                     exDateTimeString.append(dateTimeString);
                  } else {
                     exDateTimeString.append(dateDelimiter);
                     exDateTimeString.append(dateTimeString);
                  }
               } else if (i == 0) {
                  this.addPropertyTag("EXDATE:");
                  exDateTimeString.append(dateTimeString);
               } else {
                  exDateTimeString.append(dateDelimiter);
                  exDateTimeString.append(dateTimeString);
               }
            }
         }
      }

      if (exDateTimeString.length() != 0) {
         this.addPropertyTag(exDateTimeString.toString());
         this.addLineBreak();
      }
   }

   private final void buildRRULE() {
      this.checkVersion();
      if (this._version.equals("VCALENDAR/1.0")) {
         this.buildVCalRRULE();
      } else if (this._version.equals("VCALENDAR/2.0")) {
         this.buildICalRRULE();
      } else {
         throw new InvalidFormatException();
      }
   }

   private final void buildVCalRRULE() {
      int freq = this._iCalendar.getFreq();
      int type = 60;
      byte var11;
      switch (freq) {
         case -1738378111:
            var11 = 61;
            break;
         case -1681232246:
            if (this._iCalendar.getByMonthLength() > 0) {
               var11 = 64;
            } else {
               var11 = 65;
            }
            break;
         case -746168210:
            return;
         case 64808441:
            var11 = 60;
            break;
         case 1954618349:
            if (this._iCalendar.getByDayLength() > 0) {
               var11 = 62;
            } else {
               var11 = 63;
            }
            break;
         default:
            throw new InvalidFormatException();
      }

      String rruleStr = getRecurType(var11);
      int interval = this._iCalendar.getInterval();
      rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(String.valueOf(interval)).toString();
      switch (var11) {
         case 60:
            break;
         case 61:
         default:
            int[] days = this._iCalendar.getByDayWeekDay();
            int length = this._iCalendar.getByDayLength();

            for (int i = 0; i < length; i++) {
               rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(getWeekDay(days[i])).toString();
            }
            break;
         case 62:
            int[] pos = this._iCalendar.getByDayOrderWeek();
            int[] days = this._iCalendar.getByDayWeekDay();
            int lastPos = 0;
            int length = this._iCalendar.getByDayLength();

            for (int i = 0; i < length; i++) {
               int thisPos = pos[i];
               if (thisPos == 0) {
                  throw new InvalidFormatException();
               }

               if (lastPos != thisPos) {
                  rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(this.toVCalPos(thisPos)).toString();
                  lastPos = thisPos;
               }

               rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(getWeekDay(days[i])).toString();
            }
            break;
         case 63:
            int[] days = this._iCalendar.getByMonthDay();
            int length = this._iCalendar.getByMonthDayLength();
            if (length == 0) {
               throw new InvalidFormatException();
            }

            for (int i = 0; i < length; i++) {
               rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(this.toVCalDayInMonth(days[i])).toString();
            }
            break;
         case 64:
            int[] months = this._iCalendar.getByMonth();
            int length = this._iCalendar.getByMonthLength();

            for (int i = 0; i < length; i++) {
               rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(String.valueOf(months[i])).toString();
            }
            break;
         case 65:
            int[] dayInYear = this._iCalendar.getByYearDay();
            int length = this._iCalendar.getByYearDayLength();

            for (int i = 0; i < length; i++) {
               rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(String.valueOf(dayInYear[i])).toString();
            }
      }

      if (this._iCalendar.isCountSpecified()) {
         int count = this._iCalendar.getCount();
         rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" #").append(String.valueOf(count)).toString();
      }

      if (this._iCalendar.isUntilSpecified()) {
         Date end = this._iCalendar.getUntil();
         if (end != null) {
            rruleStr = ((StringBuffer)(new Object())).append(rruleStr).append(" ").append(this.convertDateToString(end, true)).toString();
         }
      }

      this.addProperty("RRULE:", rruleStr);
   }

   private final String toVCalDayInMonth(int day) {
      return day < 0 ? ((StringBuffer)(new Object())).append(String.valueOf(-day)).append("-").toString() : String.valueOf(day);
   }

   private final String toVCalPos(int pos) {
      return pos < 0
         ? ((StringBuffer)(new Object())).append(String.valueOf(-pos)).append("-").toString()
         : ((StringBuffer)(new Object())).append(String.valueOf(pos)).append("+").toString();
   }

   private final void buildICalRRULE() {
      int freq = this._iCalendar.getFreq();
      if (freq != -746168210) {
         this._rrulePropertyTag = (StringBuffer)(new Object("RRULE:FREQ="));
         this._rrulePropertyTag.append(getFreq(freq));
         this.buildUntilCountInterval();
         switch (freq) {
            case -1738378111:
               this.buildByTime(this._iCalendar.getBySecond(), this._iCalendar.getBySecondLength(), "BYSECOND");
               this.buildByTime(this._iCalendar.getByMinute(), this._iCalendar.getByMinuteLength(), "BYMINUTE");
               this.buildByTime(this._iCalendar.getByHour(), this._iCalendar.getByHourLength(), "BYHOUR");
               this.buildByDay();
               this.buildByWeekNoByMonth();
               this.buildByTime(this._iCalendar.getBySetPos(), this._iCalendar.getBySetPosLength(), "BYSETPOS");
               this.buildWKST();
               break;
            case -1681232246:
               this.buildByTime(this._iCalendar.getBySecond(), this._iCalendar.getBySecondLength(), "BYSECOND");
               this.buildByTime(this._iCalendar.getByMinute(), this._iCalendar.getByMinuteLength(), "BYMINUTE");
               this.buildByTime(this._iCalendar.getByHour(), this._iCalendar.getByHourLength(), "BYHOUR");
               this.buildByDay();
               this.buildByTime(this._iCalendar.getByMonthDay(), this._iCalendar.getByMonthDayLength(), "BYMONTHDAY");
               this.buildByTime(this._iCalendar.getByYearDay(), this._iCalendar.getByYearDayLength(), "BYYEARDAY");
               this.buildByWeekNoByMonth();
               this.buildByTime(this._iCalendar.getBySetPos(), this._iCalendar.getBySetPosLength(), "BYSETPOS");
               this.buildWKST();
               break;
            case -565154143:
               this.buildByTime(this._iCalendar.getBySecond(), this._iCalendar.getBySecondLength(), "BYSECOND");
               this.buildByYearDayByWeekNoByMonth();
               this.buildByTime(this._iCalendar.getBySetPos(), this._iCalendar.getBySetPosLength(), "BYSETPOS");
               break;
            case 64808441:
               this.buildByTime(this._iCalendar.getBySecond(), this._iCalendar.getBySecondLength(), "BYSECOND");
               this.buildByTime(this._iCalendar.getByMinute(), this._iCalendar.getByMinuteLength(), "BYMINUTE");
               this.buildByTime(this._iCalendar.getByHour(), this._iCalendar.getByHourLength(), "BYHOUR");
               this.buildByWeekNoByMonth();
               this.buildByTime(this._iCalendar.getBySetPos(), this._iCalendar.getBySetPosLength(), "BYSETPOS");
               break;
            case 1726084353:
               this.buildByYearDayByWeekNoByMonth();
               break;
            case 1954618349:
               this.buildByTime(this._iCalendar.getBySecond(), this._iCalendar.getBySecondLength(), "BYSECOND");
               this.buildByTime(this._iCalendar.getByMinute(), this._iCalendar.getByMinuteLength(), "BYMINUTE");
               this.buildByTime(this._iCalendar.getByHour(), this._iCalendar.getByHourLength(), "BYHOUR");
               this.buildByDay();
               this.buildByTime(this._iCalendar.getByMonthDay(), this._iCalendar.getByMonthDayLength(), "BYMONTHDAY");
               this.buildByTime(this._iCalendar.getBySetPos(), this._iCalendar.getBySetPosLength(), "BYSETPOS");
               this.buildWKST();
               break;
            case 2136870513:
               this.buildByTime(this._iCalendar.getBySecond(), this._iCalendar.getBySecondLength(), "BYSECOND");
               this.buildByTime(this._iCalendar.getByMinute(), this._iCalendar.getByMinuteLength(), "BYMINUTE");
               this.buildByYearDayByWeekNoByMonth();
               this.buildByTime(this._iCalendar.getBySetPos(), this._iCalendar.getBySetPosLength(), "BYSETPOS");
         }

         this.addPropertyTag(this._rrulePropertyTag.toString());
         this.addLineBreak();
      }
   }

   private final void buildUntilCountInterval() {
      String dateTimeString = null;
      Date originalDate = this._iCalendar.getUntil();
      if (originalDate != null) {
         if (this._iCalendar.isCountSpecified()) {
            throw new InvalidFormatException();
         }

         dateTimeString = this.convertDateToString(originalDate, true);
         if (dateTimeString != null) {
            this._rrulePropertyTag.append(";UNTIL=");
            this._rrulePropertyTag.append(dateTimeString);
         }
      }

      int countDigit = this._iCalendar.getCount();
      if (countDigit != 0) {
         if (this._iCalendar.isUntilSpecified()) {
            throw new InvalidFormatException();
         }

         this._rrulePropertyTag.append(";COUNT=");
         this._rrulePropertyTag.append(countDigit);
      }

      int interval = this._iCalendar.getInterval();
      if (interval != 0) {
         this._rrulePropertyTag.append(";INTERVAL=");
         this._rrulePropertyTag.append(interval);
      }
   }

   private final void buildByTime(int[] byTime, int byTimeLength, String tag) {
      if (byTimeLength > 0) {
         this._rrulePropertyTag.append(';');
         this._rrulePropertyTag.append(tag);
         this._rrulePropertyTag.append('=');
         this._rrulePropertyTag.append(byTime[0]);

         for (int i = 1; i < byTimeLength; i++) {
            this._rrulePropertyTag.append(',');
            this._rrulePropertyTag.append(byTime[i]);
         }
      }
   }

   private final void buildByDay() {
      int[] orderWeek = this._iCalendar.getByDayOrderWeek();
      int[] weekday = this._iCalendar.getByDayWeekDay();
      int dayArrayLength = this._iCalendar.getByDayLength();
      if (dayArrayLength > 0) {
         this._rrulePropertyTag.append(";BYDAY=");
         String weekDay = getWeekDay(weekday[0]);
         if (orderWeek[0] != 0) {
            this._rrulePropertyTag.append(orderWeek[0]);
         }

         this._rrulePropertyTag.append(weekDay);

         for (int i = 1; i < dayArrayLength; i++) {
            this._rrulePropertyTag.append(',');
            weekDay = getWeekDay(weekday[i]);
            if (orderWeek[i] != 0) {
               this._rrulePropertyTag.append(orderWeek[i]);
            }

            this._rrulePropertyTag.append(weekDay);
         }
      }
   }

   private final void buildByYearDayByWeekNoByMonth() {
      int[] byTime = this._iCalendar.getByYearDay();
      if (byTime != null) {
         this.buildByTime(byTime, this._iCalendar.getByYearDayLength(), "BYYEARDAY");
      } else {
         byTime = this._iCalendar.getByWeekNo();
         if (byTime != null) {
            this.buildByTime(byTime, this._iCalendar.getByWeekNoLength(), "BYWEEKNO");
         } else {
            byTime = this._iCalendar.getByMonth();
            if (byTime != null) {
               this.buildByTime(byTime, this._iCalendar.getByMonthLength(), "BYMONTH");
            }
         }
      }
   }

   private final void buildByWeekNoByMonth() {
      int[] byTime = this._iCalendar.getByWeekNo();
      if (byTime != null) {
         this.buildByTime(byTime, this._iCalendar.getByWeekNoLength(), "BYWEEKNO");
      } else {
         byTime = this._iCalendar.getByMonth();
         if (byTime != null) {
            this.buildByTime(byTime, this._iCalendar.getByMonthLength(), "BYMONTH");
         }
      }
   }

   private final void buildWKST() {
      int weekDay = this._iCalendar.getWeekStart();
      if (this._iCalendar.isWeekStartSpecified(weekDay)) {
         String weekDayString = getWeekDay(weekDay);
         this._rrulePropertyTag.append(";WKST=");
         this._rrulePropertyTag.append(weekDayString);
      }
   }

   private final void buildACTION() {
      int actionValue = this._iCalendar.getAction();
      if (actionValue != 0) {
         this.addProperty("ACTION:", getAction(actionValue));
      } else {
         throw new Object();
      }
   }

   private final void buildTRIGGER() {
      int offset = this._iCalendar.getRelativeTrigger() / 60;
      this.addPropertyTag("TRIGGER:");
      if (offset > 0) {
         this.addString("-");
      } else {
         offset = -1 * offset;
      }

      this.addString(((StringBuffer)(new Object("PT"))).append(offset).append("M").toString());
      this.addLineBreak();
   }

   private final void buildAlarmDescription() {
      int type = this._iCalendar.getAlarmDescriptionType();
      String paramValue = this._iCalendar.getAlarmDescriptionParamValue();
      String value = this._iCalendar.getAlarmDescriptionValue();
      if (!this._iCalendar.isEmptyString(value)) {
         if (type == 0) {
            this.addProperty("DESCRIPTION:", value);
            return;
         }

         StringBuffer description = (StringBuffer)(new Object("DESCRIPTION;"));
         if (!this._iCalendar.isEmptyString(paramValue)) {
            description.append(getParam(type));
            description.append(paramValue);
            this.addPropertyTag(description.toString());
            this.addColonSeparator();
            this.addString(value);
            this.addLineBreak();
         }
      }
   }

   private final void buildDALARM() {
      Date triggerDate = this._iCalendar.getTrigger();
      if (triggerDate != null) {
         String description = this._iCalendar.getAlarmDescriptionValue();
         if (description == null) {
            description = "";
         }

         StringBuffer dalarm = (StringBuffer)(new Object(this.convertDateToString(triggerDate, true)));
         dalarm.append(";PT5M;1;");
         dalarm.append(description);
         this.addPropertyTag("DALARM:");
         this.addPropertyTag(dalarm.toString());
         this.addLineBreak();
      }
   }

   private final void checkVersion() {
      if (this._version == null) {
         this._version = this._iCalendar.getVersion();
         if (this._version == null) {
            throw new InvalidFormatException();
         }
      }
   }

   private final void addColonSeparator() {
      this.addOneByte(58);
   }
}
