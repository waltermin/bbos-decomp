package net.rim.blackberry.api.pim;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.AttendeeFactory;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.api.utility.props.LongProp;
import net.rim.vm.Array;

public final class EventImpl extends PIMItemImpl implements BlackBerryEvent {
   private Date _dtend;
   private Calendar _searchCalendar1;
   private Calendar _searchCalendar2;
   private int _mode;
   private boolean _modified;
   private net.rim.device.apps.api.calendar.modelcontrollerinterface.Event _event;
   private net.rim.device.apps.api.calendar.modelcontrollerinterface.Event _committedEvent;
   private EventListImpl _eventList;
   private RepeatRule _repeatRule;
   private static final long REMINDER_FIELD;
   private static final long REMINDER_NONE;
   private static final int NUM_YEARS_CALC;
   private static final int MODIFY_NONE;
   private static final int MODIFY_ADD;
   private static final int MODIFY_REMOVE;
   private static int _allWeekInMonth = 31;
   private static int _allDaysInWeek = 130048;
   private static EventContainer _eventContainer = EventContainer.getInstance();

   final void prepareToDie() {
      this.removeFieldsModel();
      _eventContainer.commit();
   }

   final net.rim.device.apps.api.calendar.modelcontrollerinterface.Event getRimEvent() {
      return this._event;
   }

   protected final Date getEndDate(TimeZone timeZone) {
      return this._dtend;
   }

   @Override
   public final void setRepeat(RepeatRule value) {
      if (value != null) {
         long startDate = this.getStartDate();
         this._repeatRule = value.copy();
         EventFieldsModel fieldsModel = this._repeatRule.getFieldsModel();
         fieldsModel.setStartDate(startDate);
         fieldsModel.setUID(this._event.getUID());
         this._event.setRecurrence(this._repeatRule._recur);
         Recur recur = this._event.getRecurrenceCopy();
         Date d = this.computeDates(fieldsModel, recur, 1);
         this._event.setRecurrence(recur);
         if (d != null) {
            this._event.setStartDate(d.getTime(), null);
         }

         this.fixRecurFromStart();
      } else {
         long time = 0;
         if (this.countValues(106) > 0) {
            time = this.getDate(106, 0);
         }

         this._event.setStartDate(time, null);
         this._repeatRule = null;
         this._event.setRecurrence(null);
         this.removeFieldsModel();
      }

      this._modified = true;
   }

   @Override
   public final RepeatRule getRepeat() {
      if (this._event.isRecurring()) {
         if (this._repeatRule == null) {
            this._repeatRule = new RepeatRule(this._event, null, null);
         }

         return this._repeatRule.copy();
      } else {
         return null;
      }
   }

   @Override
   public final int countValues(int field) {
      switch (field) {
         case 100:
            if (this._event.get(813899564474876953L) == null) {
               return 0;
            }

            return 1;
         case 101:
         case 105:
            throw new UnsupportedFieldException(field);
         case 102:
            if (this._dtend == null) {
               return 0;
            }

            return 1;
         case 103:
            if (this._event.getLocation() == null) {
               return 0;
            }

            return 1;
         case 104:
            if (this._event.getNotes() != null && this._event.getNotes().length() != 0) {
               return 1;
            }

            return 0;
         case 106:
            if (this.getStartDate() <= 0) {
               return 0;
            }

            return 1;
         case 107:
            if (this._event.getSubject() == null) {
               return 0;
            }

            return 1;
         case 108:
            if (EventStatics._cal.contains(this._event) && String.valueOf(this._event.getUID()) != null) {
               return 1;
            }

            return 0;
         case 20000927:
            MeetingInfo meetingInfo = this._event.getMeetingInfo();
            return meetingInfo.getAttendeeCount();
         case 20000928:
         case 20000929:
            return 1;
         default:
            throw new Object();
      }
   }

   @Override
   public final void removeValue(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 100:
            LongProp reminderObject = (LongProp)this._event.get(813899564474876953L);
            if (reminderObject != null) {
               reminderObject.setLong(-1);
            }
            break;
         case 102:
            this._event.setInstanceDuration(0);
            this._event.setAllDayFlag(false);
            this._dtend = null;
            break;
         case 103:
            this._event.setLocation(null);
            break;
         case 104:
            this._event.setNotes(null);
            break;
         case 106:
            this._event.setStartDate(0, null);
            this._event.setAllDayFlag(false);
            break;
         case 107:
            this._event.setSubject(null);
            break;
         case 108:
            throw new Object("UID is a read-only field.");
         case 20000927:
            MeetingInfo meetingInfo = this._event.getMeetingInfo();
            meetingInfo.removeAttendee(this.getAttendee(index));
            break;
         case 20000928:
         case 20000929:
            return;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final PIMList getPIMList() {
      return this._eventList;
   }

   @Override
   public final void commit() {
      if (this._eventList._closed) {
         throw new PIMException("Event List is closed.", 2);
      }

      if (this._mode == 1) {
         throw new Object();
      }

      if (this.isEmpty()) {
         throw new PIMException("Cannot commit an empty Event", 6);
      }

      if (this.countValues(106) == 0) {
         throw new PIMException("Cannot commit Event without start date", 6);
      }

      if (this.countValues(102) == 0) {
         throw new PIMException("Cannot commit Event without end date", 6);
      }

      if (this.getDate(106, 0) > this.getDate(102, 0)) {
         throw new PIMException("Cannot commit an Event with start date after end date", 6);
      }

      Object[] calEvents = new Object[EventStatics._cal.size()];
      EventStatics._cal.getElements(calEvents);
      if (this._committedEvent != null) {
         RecurUtilities.scanAndDeleteRelatedEvents(EventStatics._cal, this._committedEvent);
         EventStatics._cal.remove(this._committedEvent);
      }

      if (this._repeatRule != null) {
         this.addFieldsModel(this._repeatRule.getFieldsModel().copy());
      }

      this._committedEvent = this._event;
      ObjectGroup.createGroupIgnoreTooBig(this._committedEvent);
      EventStatics._cal.add(this._committedEvent);
      this._event = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)ObjectGroup.expandGroup(this._committedEvent);
      this._modified = false;
   }

   @Override
   public final boolean isModified() {
      return this._modified;
   }

   @Override
   public final int[] getFields() {
      int[] fields = new int[10];
      int index = 0;
      if (this.countValues(108) > 0) {
         fields[index++] = 108;
      }

      if (this.countValues(103) > 0) {
         fields[index++] = 103;
      }

      if (this.countValues(104) > 0) {
         fields[index++] = 104;
      }

      if (this.countValues(107) > 0) {
         fields[index++] = 107;
      }

      if (this.countValues(100) > 0) {
         fields[index++] = 100;
      }

      if (this.countValues(102) > 0) {
         fields[index++] = 102;
      }

      if (this.countValues(106) > 0) {
         fields[index++] = 106;
      }

      if (this.countValues(20000927) > 0) {
         fields[index++] = 20000927;
      }

      if (this.countValues(20000928) > 0) {
         fields[index++] = 20000928;
      }

      if (this.countValues(20000929) > 0) {
         fields[index++] = 20000929;
      }

      Array.resize(fields, index);
      return fields;
   }

   @Override
   public final long getDate(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 102:
            return this.getEndDate(null).getTime();
         case 106:
            return this.getStartDate();
         default:
            throw new Object();
      }
   }

   private final void setAlarm(int value) {
      if (value < 0) {
         throw new Object();
      }

      LongProp reminderObject = (LongProp)this._event.get(813899564474876953L);
      if (reminderObject != null) {
         reminderObject.setLong(value * 1000);
      }
   }

   private final void setEndDate(long value) {
      if (value < 0) {
         throw new Object();
      }

      if (this.countValues(106) > 0) {
         long start = this.getDate(106, 0);
         long duration = value - start;
         this._event.setInstanceDuration(duration);
         this._event.setAllDayFlag(duration == 0);
      }

      this._dtend = (Date)(new Object(value));
   }

   private final void setStartDate(long value) {
      if (value < 0) {
         throw new Object();
      }

      EventFieldsModel fieldsModel = null;
      Recur recur = null;
      if (this._repeatRule != null) {
         recur = this._event.getRecurrenceCopy();
         fieldsModel = this._repeatRule.getFieldsModel();
         this.computeDates(fieldsModel, recur, 2);
      }

      if (fieldsModel != null) {
         fieldsModel.setStartDate(value);
      }

      Date d = this.computeDates(fieldsModel, recur, 1);
      this._event.setStartDate(d == null ? value : d.getTime(), null);
      this._event.setRecurrence(recur);
      this.fixRecurFromStart();
      if (this.countValues(102) > 0) {
         long end = this.getDate(102, 0);
         long duration = end - value;
         this._event.setInstanceDuration(duration);
         this._event.setAllDayFlag(duration == 0);
      }
   }

   @Override
   public final void addDate(int field, int attributes, long value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 102:
            this.setEndDate(value);
            break;
         case 106:
            this.setStartDate(value);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   private final Attendee getAttendee(int index) {
      MeetingInfo meetingInfo = this._event.getMeetingInfo();
      Enumeration attendees = meetingInfo.getAttendees();
      Attendee current = (Attendee)attendees.nextElement();

      for (int i = 0; i < index; i++) {
         try {
            current = (Attendee)attendees.nextElement();
         } finally {
            ;
         }
      }

      return current;
   }

   @Override
   public final int getInt(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 100:
            return this.getAlarm();
         case 101:
            throw new UnsupportedFieldException(field);
         case 20000929:
            return this._event.getFreeBusy();
         default:
            throw new Object();
      }
   }

   @Override
   public final void addInt(int field, int attributes, int value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 100:
            this.setAlarm(value);
            break;
         case 101:
            throw new UnsupportedFieldException(field);
         case 20000929:
            this._event.setFreeBusy((byte)value);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final String getString(int field, int index) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            return this._event.getLocation();
         case 104:
            return this._event.getNotes();
         case 107:
            return this._event.getSubject();
         case 108:
            if (EventStatics._cal.contains(this._event)) {
               return String.valueOf(this._event.getUID());
            }

            return null;
         case 20000927:
            Attendee attendee = this.getAttendee(index);
            Object address = attendee.getAddress();
            return address.toString();
         default:
            throw new Object();
      }
   }

   @Override
   public final void addString(int field, int attributes, String value) {
      this.checkFieldNotFull(field);
      switch (field) {
         case 103:
            this._event.setLocation(value);
            break;
         case 104:
            this._event.setNotes(value);
            break;
         case 107:
            this._event.setSubject(value);
            break;
         case 108:
            throw new Object("UID is a read-only field.");
         case 20000927:
            MeetingInfo meetingInfo = this._event.getMeetingInfo();
            Attendee attendee = AttendeeFactory.createAttendeeFromRFC822(1, value);
            meetingInfo.addAttendee(attendee);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final void addBoolean(int field, int attributes, boolean value) {
      switch (field) {
         case 20000927:
            throw new Object();
         case 20000928:
         default:
            this._event.setAllDayFlag(value);
            this._modified = true;
      }
   }

   @Override
   public final boolean getBoolean(int field, int index) {
      switch (field) {
         case 20000927:
            throw new Object();
         case 20000928:
         default:
            return this._event.getAllDayFlag();
      }
   }

   @Override
   public final void setBoolean(int field, int index, int attributes, boolean value) {
      switch (field) {
         case 20000927:
            throw new Object();
         case 20000928:
         default:
            this._event.setAllDayFlag(value);
            this._modified = true;
      }
   }

   @Override
   protected final void checkFieldNotFull(int field) {
      if (field != 20000927 && field != 20000929) {
         super.checkFieldNotFull(field);
      }
   }

   private final void calcDayInYear(Calendar cal, int dayInYear, int interval, int modifyType, Recur recur, long endTime, int repeatCount) {
      long[] exclusions = recur.getExclusions(null);
      long[] inclusions = recur.getInclusions(null);
      int startYear = cal.get(1);
      int startMonth = cal.get(2);
      int startDay = cal.get(5);
      if (dayInYear == 366) {
         cal.set(2, 11);
         cal.set(5, 31);
         if (modifyType != 0) {
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(cal.getTime());
            int year = startYear;

            for (int i = 0; i <= 5 / interval; i++) {
               if (!this.isLeapYear(year)) {
                  tempCal.set(1, year);
                  this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
               }

               year += interval;
            }
         }
      } else {
         boolean mostlyLeap = false;
         boolean excludeFirst = false;
         int year = startYear;
         if (this.isLeapYear(year)) {
            if (interval % 4 == 0) {
               mostlyLeap = true;
            } else {
               year -= interval;
               excludeFirst = true;
            }
         }

         int month = 0;
         int daysInMonth = this.getMaxDayInMonth(month, year);

         int day;
         for (day = dayInYear; day > daysInMonth && month <= 11; daysInMonth = this.getMaxDayInMonth(++month, year)) {
            day -= daysInMonth;
         }

         if (month < startMonth || startMonth == month && day < startDay) {
            year += interval;
            if (excludeFirst) {
               year += interval;
               excludeFirst = false;
            }
         }

         cal.set(1, year);
         cal.set(2, month);
         cal.set(5, day);
         if (excludeFirst) {
            if (month < 2) {
               year += interval;
               cal.set(1, year);
               excludeFirst = false;
            } else if (modifyType != 0) {
               this.modifyExclusion(recur, inclusions, cal.getTime().getTime(), modifyType);
            }
         }

         if (modifyType != 0 && month >= 2) {
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime(cal.getTime());
            Calendar inclusionCal = null;
            year = startYear;

            for (int count = 0; count <= 5 / interval && (repeatCount == 0 || count < repeatCount); count++) {
               if (!this.isLeapYear(year)) {
                  if (mostlyLeap) {
                     tempCal.set(1, year);
                     this.modifyInclusion(recur, exclusions, tempCal.getTime().getTime(), modifyType);
                     inclusionCal = Calendar.getInstance();
                     inclusionCal.setTime(tempCal.getTime());
                     if (day < this.getMaxDayInMonth(month, year)) {
                        inclusionCal.set(5, day + 1);
                     } else {
                        inclusionCal.set(2, month + 1);
                        inclusionCal.set(5, 1);
                     }

                     inclusionCal.set(1, year);
                     long inclusionTime = inclusionCal.getTime().getTime();
                     if (endTime != 0 && inclusionTime > endTime) {
                        break;
                     }

                     this.modifyInclusion(recur, exclusions, inclusionTime, modifyType);
                  }
               } else if (!mostlyLeap) {
                  tempCal.set(1, year);
                  this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
                  if (inclusionCal == null) {
                     inclusionCal = Calendar.getInstance();
                     inclusionCal.setTime(tempCal.getTime());
                     if (day > 1) {
                        inclusionCal.set(5, day - 1);
                     } else {
                        inclusionCal.set(2, month - 1);
                        inclusionCal.set(5, this.getMaxDayInMonth(month - 1, year));
                     }
                  } else {
                     inclusionCal.set(1, year);
                  }

                  long inclusionTime = inclusionCal.getTime().getTime();
                  if (endTime != 0 && inclusionTime > endTime) {
                     break;
                  }

                  this.modifyInclusion(recur, exclusions, inclusionTime, modifyType);
               }

               year += interval;
            }
         }
      }
   }

   private final Date computeDates(EventFieldsModel model, Recur recur, int modifyType) {
      if (recur != null) {
         int freq = model.getFrequency();
         long startDate = model.getStartDate();
         if (startDate != 0 && (freq == 18 || freq == 19)) {
            int dayInYear = model.getDayInYear();
            int repeatDay = model.getDayNumber();
            int months = model.getMonth();
            int daysInWk = model.getDayInWeek();
            int interval = model.getInterval();
            int repeatCount = model.getRepeatCount();
            long endTime = model.getEndDate();
            long[] exclusions = recur.getExclusions(null);
            long[] inclusions = recur.getInclusions(null);
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date)(new Object(startDate)));
            if (modifyType == 1) {
               recur.clearAllModifiers();
            }

            boolean notRelative = repeatDay != 0 || repeatDay == 0 && daysInWk == 0;
            if (freq == 19 && dayInYear != 0) {
               this.calcDayInYear(cal, dayInYear, interval, modifyType, recur, endTime, repeatCount);
            } else if (!notRelative) {
               if (daysInWk != 0) {
                  if (modifyType == 0 && freq == 18) {
                     return cal.getTime();
                  }

                  int wksInMth = model.getWeekInMonth();
                  if (wksInMth == 0) {
                     wksInMth = _allWeekInMonth;
                  } else if ((wksInMth & 32) != 0) {
                     wksInMth |= 16;
                  }

                  boolean isWeekFourSet = (wksInMth & 8) != 0;
                  wksInMth &= 31;
                  int wimSet = 0;
                  int diwSet = 0;
                  int[] weekArray = RepeatRuleUtil.getWeekArray(wksInMth);
                  int numWeeks = weekArray.length;
                  int[] dayArray = RepeatRuleUtil.getDayInWeekArray(daysInWk);
                  int numDays = dayArray.length;
                  int startWIM = this.getWIMOfDate(cal);
                  int startDIW = cal.get(7) - 1;
                  if (this.isInDaysInWeek(daysInWk, startDIW) && this.isInWkInMth(wksInMth, startWIM)) {
                     wimSet = startWIM;
                     diwSet = startDIW;
                  } else {
                     wimSet = numWeeks > 0 ? weekArray[0] : 1;
                     diwSet = numDays > 0 ? dayArray[0] : 0;
                  }

                  if (modifyType == 0) {
                     this.setMonth(cal, months, diwSet, wimSet, interval);
                     return cal.getTime();
                  }

                  if (modifyType == 1) {
                     RecurUtil.addDayOfWeekModifier(recur, diwSet, wimSet);
                     if (freq == 19) {
                        RecurUtil.addMonthModifier(recur, cal.get(2) + 1);
                     }
                  }

                  int startMonth = cal.get(2);
                  boolean checkMonth = months != 0 && (freq == 18 || (months & startMonth) == 0 || (months & ~startMonth) != 0);
                  if (checkMonth || numDays > 1 || numWeeks > 1) {
                     int count = 0;
                     boolean notStop = true;
                     Calendar tempCal = Calendar.getInstance();
                     int startDayOfMonth = cal.get(5);
                     if (freq != 18) {
                        this.setMonth(cal, months, diwSet, wimSet, interval);
                        startMonth = cal.get(2);
                        tempCal.setTime(cal.getTime());
                        int startYear = tempCal.get(1);
                        int endYear = startYear + 5 / interval;
                        int[] monthArray;
                        if (months == 0) {
                           monthArray = new int[]{startMonth};
                        } else {
                           monthArray = RepeatRuleUtil.getMonthArray(months);
                        }

                        int numMonths = monthArray.length;

                        for (int thisYear = startYear; thisYear <= endYear && notStop; thisYear += interval) {
                           tempCal.set(5, 1);
                           tempCal.set(1, thisYear);
                           int monthIndex = thisYear == startYear ? this.indexOf(monthArray, startMonth) : 0;
                           if (monthIndex < 0) {
                              monthIndex = 0;
                           }

                           while (monthIndex < numMonths && notStop) {
                              int thisMonth = monthArray[monthIndex];
                              tempCal.set(2, thisMonth);
                              int maxDay = this.getMaxDayInMonth(thisMonth, thisYear);
                              int[] firstDays = this.getFirstDays(dayArray, thisMonth, thisYear);

                              for (int weekIndex = 0; weekIndex < numWeeks && notStop; weekIndex++) {
                                 int week = weekArray[weekIndex];
                                 int dayIndex;
                                 if (week == 5 && !isWeekFourSet && repeatCount != 0 && count + 7 > repeatCount) {
                                    dayIndex = this.indexOfLeastLastDay(firstDays, maxDay);
                                 } else {
                                    dayIndex = firstDays[numDays];
                                 }

                                 for (int i = 0; i < numDays && notStop; i++) {
                                    int day = dayArray[dayIndex];
                                    int dayInMonth = firstDays[dayIndex] + 7 * (week - 1);
                                    if (dayInMonth > maxDay) {
                                       if (isWeekFourSet) {
                                          dayIndex = (dayIndex + 1) % numDays;
                                          continue;
                                       }

                                       dayInMonth -= 7;
                                    }

                                    if (thisYear != startYear || thisMonth != startMonth || dayInMonth >= startDayOfMonth) {
                                       tempCal.set(5, dayInMonth);
                                       long weekDayTime = tempCal.getTime().getTime();
                                       notStop = endTime == 0 || weekDayTime < endTime;
                                       if (notStop) {
                                          if (thisMonth == startMonth && week == wimSet && day == diwSet) {
                                             if (!this.arrayContainsDate(exclusions, weekDayTime)) {
                                                count++;
                                             }
                                          } else if (this.modifyInclusion(recur, exclusions, weekDayTime, modifyType)) {
                                             count++;
                                          }

                                          notStop = repeatCount == 0 || count < repeatCount;
                                       }
                                    }

                                    dayIndex = (dayIndex + 1) % numDays;
                                 }
                              }

                              monthIndex++;
                           }
                        }
                     } else {
                        net.rim.device.apps.api.calendar.modelcontrollerinterface.Event tempEvent = this.getNewEvent(startDate);
                        Recur enumRecur = tempEvent.getRecurrenceCopy();
                        enumRecur.setRecurType(RepeatRuleUtil.freqRepeatToRecur(freq));
                        enumRecur.setRecurPeriod(interval);
                        if (endTime == 0) {
                           enumRecur.setAsFinite(false);
                        } else {
                           enumRecur.setEndDate(endTime);
                        }

                        enumRecur.clearAllModifiers();
                        RecurUtil.addDayOfWeekModifier(enumRecur, diwSet, wimSet);
                        tempEvent.setRecurrence(enumRecur);
                        Enumeration enumeration = new RepeatPatternEnumeration(tempEvent, startDate, startDate, 0, repeatCount);
                        tempCal.setTime(cal.getTime());

                        for (int j = 0; j <= 60 / interval && notStop; j++) {
                           if (!this.isInMonths(months, tempCal.get(2))) {
                              if (j != 0) {
                                 this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
                              }
                           } else {
                              int thisYear = tempCal.get(1);
                              int thisMonth = tempCal.get(2);
                              int maxDay = this.getMaxDayInMonth(thisMonth, thisYear);
                              int[] firstDays = this.getFirstDays(dayArray, thisMonth, thisYear);

                              for (int weekIndex = 0; weekIndex < numWeeks && notStop; weekIndex++) {
                                 int week = weekArray[weekIndex];
                                 int dayIndex;
                                 if (week == 5 && !isWeekFourSet && repeatCount != 0 && count + 7 > repeatCount) {
                                    dayIndex = this.indexOfLeastLastDay(firstDays, maxDay);
                                 } else {
                                    dayIndex = firstDays[numDays];
                                 }

                                 for (int i = 0; i < numDays && notStop; i++) {
                                    int day = dayArray[dayIndex];
                                    int dayInMonth = firstDays[dayIndex] + 7 * (week - 1);
                                    if (dayInMonth > maxDay) {
                                       if (isWeekFourSet) {
                                          dayIndex = (dayIndex + 1) % numDays;
                                          continue;
                                       }

                                       dayInMonth -= 7;
                                    }

                                    if (j != 0 || dayInMonth >= startDayOfMonth) {
                                       tempCal.set(5, dayInMonth);
                                       long weekDayTime = tempCal.getTime().getTime();
                                       notStop = endTime == 0 || weekDayTime < endTime;
                                       if (notStop) {
                                          if (week == wimSet && day == diwSet) {
                                             if (!this.arrayContainsDate(exclusions, weekDayTime)) {
                                                count++;
                                             }
                                          } else if (this.modifyInclusion(recur, exclusions, weekDayTime, modifyType)) {
                                             count++;
                                          }

                                          notStop = repeatCount == 0 || count < repeatCount;
                                       }
                                    }

                                    dayIndex = (dayIndex + 1) % numDays;
                                 }
                              }
                           }

                           if (enumeration.hasMoreElements()) {
                              tempCal.setTime((Date)enumeration.nextElement());
                              if (j == 0 && this.isInMonths(months, startMonth) && tempCal.get(2) == startMonth && tempCal.get(1) == cal.get(1)) {
                                 if (enumeration.hasMoreElements()) {
                                    tempCal.setTime((Date)enumeration.nextElement());
                                 } else {
                                    tempCal = null;
                                    notStop = false;
                                 }
                              }
                           } else {
                              tempCal = null;
                              notStop = false;
                           }
                        }
                     }
                  }
               }
            } else {
               int startMonth = cal.get(2);
               if (!this.setMonthWithDay(cal, months, repeatDay, interval, freq)) {
                  if (modifyType == 1) {
                     recur.setEndDate(startDate - 1);
                     this.modifyExclusion(recur, inclusions, startDate, modifyType);
                     return null;
                  }

                  if (modifyType == 2) {
                     this.modifyExclusion(recur, inclusions, startDate, modifyType);
                  }

                  return null;
               }

               if (modifyType == 0) {
                  return cal.getTime();
               }

               if (repeatDay == 0) {
                  repeatDay = cal.get(5);
               }

               if (daysInWk == _allDaysInWeek) {
                  daysInWk = 0;
               }

               if (repeatDay > 28 || months != 0 || daysInWk != 0) {
                  if (freq != 18) {
                     startMonth = cal.get(2);
                     Calendar tempCal = Calendar.getInstance();
                     int count = 0;
                     boolean notStop = true;
                     tempCal.setTime(cal.getTime());
                     int startYear = tempCal.get(1);
                     int endYear = startYear + 5 / interval;
                     int[] monthArray;
                     if (months == 0) {
                        monthArray = new int[]{startMonth};
                     } else {
                        monthArray = RepeatRuleUtil.getMonthArray(months);
                     }

                     int numMonths = monthArray.length;

                     for (int thisYear = startYear; thisYear <= endYear && notStop; thisYear += interval) {
                        tempCal.set(5, 1);
                        tempCal.set(1, thisYear);
                        int monthIndex = thisYear == startYear ? this.indexOf(monthArray, startMonth) : 0;
                        if (monthIndex < 0) {
                           monthIndex = 0;
                        }

                        for (; monthIndex < numMonths && notStop; monthIndex++) {
                           int thisMonth = monthArray[monthIndex];
                           tempCal.set(2, thisMonth);
                           int maxDay = this.getMaxDayInMonth(thisMonth, thisYear);
                           if (maxDay < repeatDay) {
                              if (thisMonth == startMonth) {
                                 tempCal.set(5, maxDay);
                                 this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
                              }
                           } else {
                              tempCal.set(5, repeatDay);
                              if (this.isInDaysInWeek(daysInWk, tempCal.get(7) - 1)) {
                                 long weekDayTime = tempCal.getTime().getTime();
                                 notStop = endTime == 0 || weekDayTime < endTime;
                                 if (notStop) {
                                    if (thisMonth != startMonth) {
                                       if (this.modifyInclusion(recur, exclusions, weekDayTime, modifyType)) {
                                          count++;
                                       }
                                    } else if (!this.arrayContainsDate(exclusions, weekDayTime)) {
                                       count++;
                                    }

                                    notStop = repeatCount == 0 || count < repeatCount;
                                 }
                              } else if (thisMonth == startMonth) {
                                 this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
                              }
                           }
                        }
                     }
                  } else {
                     Calendar tempCal = Calendar.getInstance();
                     tempCal.setTime(cal.getTime());
                     boolean checkFebOnly = repeatDay == 29 && months == 0 && daysInWk == 0;

                     for (int j = 0; j <= 60 / interval && (endTime == 0 || endTime < tempCal.getTime().getTime()); j++) {
                        int thisMonth = tempCal.get(2);
                        if (checkFebOnly && thisMonth != 1) {
                           this.setIntervalAfter(tempCal, 18, interval);
                        } else {
                           int thisYear = tempCal.get(1);
                           int maxDay = this.getMaxDayInMonth(thisMonth, thisYear);
                           if (maxDay < repeatDay) {
                              tempCal.set(5, maxDay);
                              this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
                           } else {
                              tempCal.set(5, repeatDay);
                              if (!this.isInDaysInWeek(daysInWk, tempCal.get(7) - 1) || !this.isInMonths(months, thisMonth)) {
                                 this.modifyExclusion(recur, inclusions, tempCal.getTime().getTime(), modifyType);
                              }
                           }

                           tempCal.set(5, 1);
                           this.setIntervalAfter(tempCal, 18, interval);
                        }
                     }
                  }
               }
            }

            return cal.getTime();
         }
      }

      return null;
   }

   @Override
   public final void setDate(int field, int index, int attributes, long value) {
      this.checkIndex(field, index);
      switch (field) {
         case 102:
            this.setEndDate(value);
            break;
         case 106:
            this.setStartDate(value);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final void setInt(int field, int index, int attributes, int value) {
      this.checkIndex(field, index);
      switch (field) {
         case 100:
            this.setAlarm(value);
            break;
         case 101:
            throw new UnsupportedFieldException(field);
         case 20000929:
            this._event.setFreeBusy((byte)value);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   @Override
   public final void setString(int field, int index, int attributes, String value) {
      this.checkIndex(field, index);
      switch (field) {
         case 103:
            this._event.setLocation(value);
            break;
         case 104:
            this._event.setNotes(value);
            break;
         case 107:
            this._event.setSubject(value);
            break;
         case 108:
            throw new Object("UID is a read-only field.");
         case 20000927:
            Attendee temp = AttendeeFactory.createAttendeeFromRFC822(1, value);
            Object address = temp.getAddress();
            Attendee attendee = this.getAttendee(index);
            attendee.setAddress(address);
            break;
         default:
            throw new Object();
      }

      this._modified = true;
   }

   private final int getAlarm() {
      LongProp reminderObject = (LongProp)this._event.get(813899564474876953L);
      if (reminderObject != null) {
         long reminder = reminderObject.getLong();
         if (reminder != -1) {
            return (int)(reminder / 1000);
         }
      }

      return -1;
   }

   public EventImpl(int mode, net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e, EventListImpl eventList) {
      this._mode = mode;
      this._eventList = eventList;
      this._modified = false;
      this._committedEvent = e;
      if (ObjectGroup.isInGroup(e)) {
         this._event = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)ObjectGroup.expandGroup(e);
      } else {
         this._event = e;
      }

      long startDate = this._event.getStartDate(null);
      if (!this._event.isRecurring()) {
         this.removeFieldsModel();
         this._repeatRule = null;
      } else {
         EventFieldsModel fieldsModel = this.getFieldsModel();
         if (fieldsModel == null) {
            this._repeatRule = new RepeatRule(this._event, null, null);
         } else {
            Recur recur = e.getRecurrenceCopy();
            Date expectedStart = this.computeDates(fieldsModel, recur, 2);
            boolean startChanged = false;
            if (expectedStart == null) {
               boolean modelStartEqualsIgnoreSeconds = fieldsModel.getStartDate() / 60000 == startDate / 60000;
               if (!modelStartEqualsIgnoreSeconds) {
                  startChanged = true;
               }
            } else {
               boolean expectedStartEqualsIgnoreSeconds = expectedStart.getTime() / 60000 == startDate / 60000;
               if (!expectedStartEqualsIgnoreSeconds) {
                  startChanged = true;
               }
            }

            if (startChanged) {
               this._event.setRecurrence(recur);
               recur = this._event.getRecurrenceCopy();
               fieldsModel.setStartDate(startDate);
               fieldsModel.setMonth(RepeatRuleUtil.getRecurMonth(recur));
               fieldsModel.setDayInWeek(RecurUtil.getBitmapDaysOfWeek(recur));
               fieldsModel.setWeekInMonth(RepeatRuleUtil.getRecurWeekInMonth(recur));
               fieldsModel.setDayInYear(0);
               this.addDate(106, 0, startDate);
               this._repeatRule = new RepeatRule(this._event, recur, fieldsModel);
            } else {
               Recur tempRecur = this._event.getRecurrenceCopy();
               this._event.setRecurrence(recur);
               Recur recurCopy = this._event.getRecurrenceCopy();
               this._event.setRecurrence(tempRecur);
               this._repeatRule = new RepeatRule(this._event, recurCopy, fieldsModel);
               if (!fieldsModel.equals(this._repeatRule.getFieldsModel())) {
                  expectedStart = this.computeDates(this._repeatRule.getFieldsModel(), recur, 1);
                  this._event.setRecurrence(recur);
               }
            }
         }
      }

      if (e.getAllDayFlag() && e.getInstanceDuration() != 0) {
         this.addDate(102, 0, this.getDate(106, 0));
      } else {
         this._dtend = (Date)(new Object(this.getStartDate() + e.getInstanceDuration()));
      }
   }

   EventImpl(int mode, EventListImpl eventList) {
      this(mode);
      this._eventList = eventList;
   }

   private final long getStartDate() {
      return this._repeatRule != null ? this._repeatRule.getFieldsModel().getStartDate() : this._event.getStartDate(null);
   }

   private final boolean setMonthWithDay(Calendar start, int months, int repeatDay, int interval, int freq) {
      int startMonth = start.get(2);
      int year = start.get(1);
      int startDayOfMonth = start.get(5);
      int maxDay = this.getMaxDayInMonth(startMonth, year);
      if (this.isInMonths(months, startMonth)) {
         if (repeatDay == 0) {
            return true;
         }

         if (maxDay >= repeatDay && repeatDay >= startDayOfMonth) {
            start.set(5, repeatDay);
            return true;
         }
      }

      if (repeatDay == 0) {
         repeatDay = startDayOfMonth;
      }

      if (freq != 18) {
         int checkMonth = startMonth;

         for (int i = 0; i < 2; i++) {
            for (int nextMonth = this.getMonthAfter(months, checkMonth); nextMonth >= 0; nextMonth = this.getMonthAfter(months, nextMonth)) {
               if (this.getMaxDayInMonth(nextMonth, year) >= repeatDay) {
                  start.set(2, nextMonth);
                  start.set(5, repeatDay);
                  return true;
               }
            }

            start.set(5, 1);
            this.setIntervalAfter(start, freq, interval);
            year = start.get(1);
            checkMonth = 0;
         }

         int nextMonth = this.getMonthAfter(months, 0);
         if (nextMonth == 1 && repeatDay == 29) {
            for (int count = 5 / interval; count >= 0; count--) {
               this.setIntervalAfter(start, freq, interval);
               if (this.getMaxDayInMonth(nextMonth, start.get(1)) >= repeatDay) {
                  start.set(2, nextMonth);
                  start.set(5, repeatDay);
                  return true;
               }
            }
         }

         return false;
      } else {
         if (repeatDay != 0 && repeatDay < startDayOfMonth) {
            start.set(5, 1);
            this.setIntervalAfter(start, freq, interval);
            maxDay = this.getMaxDayInMonth(start.get(2), year);
         }

         if (repeatDay > maxDay) {
            int numIterations = 60 / interval;

            while (repeatDay > maxDay) {
               start.set(5, 1);
               boolean found = false;

               do {
                  this.setIntervalAfter(start, freq, interval);
               } while (!(found = this.isInMonths(months, start.get(2))) && --numIterations >= 0);

               if (!found || numIterations < 0) {
                  return false;
               }

               maxDay = this.getMaxDayInMonth(start.get(2), start.get(1));
            }
         }

         start.set(5, repeatDay);
         return true;
      }
   }

   private final void setMonth(Calendar start, int months, int dayInWk, int wkInMth, int interval) {
      int startMonth = start.get(2);
      if (!this.isInMonths(months, startMonth)) {
         start.set(5, 1);
         int year = start.get(1);
         int nextMonth = this.getMonthAfter(months, startMonth + 1);
         if (nextMonth < 0) {
            nextMonth = this.getMonthAfter(months, 0);
            year += interval;
            start.set(1, year);
         }

         Calendar first = this.getFirstDay(dayInWk, nextMonth, year);
         int day = first.get(5) + 7 * (wkInMth - 1);
         if (this.getMaxDayInMonth(nextMonth, year) < day) {
            day -= 7;
         }

         start.set(2, nextMonth);
         start.set(5, day);
      }
   }

   private final boolean isInMonths(int months, int month) {
      return months == 0 ? true : (1 << month + 17 & months) != 0;
   }

   private final boolean isInDaysInWeek(int daysInWk, int day) {
      return daysInWk == 0 ? true : (1 << 6 - day + 10 & daysInWk) != 0;
   }

   private final boolean isInWkInMth(int wkInMth, int week) {
      return wkInMth == 0 ? true : (1 << week - 1 + 0 & wkInMth) != 0;
   }

   private final int getWIMOfDate(Calendar cal) {
      int dayInMonth = cal.get(5);
      return dayInMonth / 7 + 1;
   }

   private final Calendar getFirstDay(int day, int month, int year) {
      Calendar cal = Calendar.getInstance();
      cal.set(5, 1);
      cal.set(2, month);
      cal.set(1, year);
      int calDay = cal.get(7) - 1;
      if (day < calDay) {
         cal.set(5, day - calDay + 8);
      } else {
         cal.set(5, day - calDay + 1);
      }

      return cal;
   }

   private final int indexOfLeastLastDay(int[] firstDays, int maxDay) {
      int leastDay = 32;
      int leastIndex = -1;

      for (int i = firstDays.length - 2; i >= 0; i--) {
         int dayInMonth = firstDays[i] + 28;
         if (dayInMonth > maxDay) {
            dayInMonth -= 7;
         }

         if (dayInMonth < leastDay) {
            leastDay = dayInMonth;
            leastIndex = i;
         }
      }

      return leastIndex;
   }

   private final int indexOf(int[] array, int element) {
      if (array != null) {
         for (int i = array.length - 1; i >= 0; i--) {
            if (array[i] == element) {
               return i;
            }
         }
      }

      return -1;
   }

   private final int getMonthAfter(int months, int afterMonth) {
      for (int var3 = months >> 17 + afterMonth; afterMonth <= 11 && var3 != 0; afterMonth++) {
         if ((var3 & 1) != 0) {
            return afterMonth;
         }

         var3 >>= 1;
      }

      return -1;
   }

   private final int[] getFirstDays(int[] dayArray, int month, int year) {
      int length = dayArray.length;
      int[] result = new int[length + 1];
      Calendar cal = Calendar.getInstance();
      cal.set(5, 1);
      cal.set(2, month);
      cal.set(1, year);
      int calDay = cal.get(7) - 1;
      result[length] = -1;

      for (int i = 0; i < length; i++) {
         int day = dayArray[i];
         if (day < calDay) {
            result[i] = day - calDay + 8;
            result[length] = i;
         } else {
            result[i] = day - calDay + 1;
         }
      }

      result[length] = (result[length] + 1) % length;
      return result;
   }

   private final void setIntervalAfter(Calendar cal, int frequency, int interval) {
      if (frequency == 19) {
         cal.set(1, cal.get(1) + interval);
      } else {
         if (frequency == 18) {
            int month = cal.get(2) + interval;
            int year = cal.get(1) + month / 12;
            month %= 12;
            cal.set(1, year);
            cal.set(2, month);
         }
      }
   }

   private final int getMaxDayInMonth(int month, int year) {
      switch (month) {
         case -1:
            throw new Object();
         case 0:
         case 2:
         case 4:
         case 6:
         case 7:
         case 9:
         case 11:
         default:
            return 31;
         case 1:
            if (this.isLeapYear(year)) {
               return 29;
            }

            return 28;
         case 3:
         case 5:
         case 8:
         case 10:
            return 30;
      }
   }

   private final boolean isLeapYear(int year) {
      return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
   }

   private final int getRepeatInt(RepeatRule repeat, int field) {
      try {
         return repeat.getInt(field);
      } catch (FieldEmptyException fee) {
         return 0;
      }
   }

   private final void fixRecurFromStart() {
      RepeatRule repeat = this.getRepeat();
      if (repeat != null && this.countValues(106) != 0) {
         Recur recur = this._event.getRecurrenceCopy();
         long startDate = this.getDate(106, 0);
         Calendar cal = Calendar.getInstance();
         cal.setTime((Date)(new Object(startDate)));
         if (this.getRepeatInt(repeat, 2) == 0 && (recur.numModifierValues(1) != 0 || this.getRepeatInt(repeat, 5) == 17)) {
            RepeatRuleUtil.setDayInWeekToRecur(recur, 1 << 17 - cal.get(7));
            this._event.setRecurrence(recur);
         }

         int count = this.getRepeatInt(repeat, 0);
         long newEndDate = RepeatPatternEnumeration.findLastCount(this._event, startDate, count);
         if (newEndDate != 0 || count == 0) {
            if (newEndDate == 0) {
               recur.setEndDate(0);
               recur.setAsFinite(false);
            } else {
               recur.setEndDate(newEndDate);
            }

            this._event.setRecurrence(recur);
         }
      }
   }

   private final boolean isEmpty() {
      return this.countValues(106) == 0 || this.countValues(102) == 0;
   }

   private final net.rim.device.apps.api.calendar.modelcontrollerinterface.Event getNewEvent(long startDate) {
      net.rim.device.apps.api.calendar.modelcontrollerinterface.Event newEvent = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)EventStatics._eventFactory
         .createInstance(null);
      newEvent.setStartDate(startDate, null);
      return newEvent;
   }

   private final boolean modifyExclusion(Recur recur, long[] inclusions, long date, int modifyType) {
      switch (modifyType) {
         case 0:
            return false;
         case 1:
         default:
            if (!this.arrayContainsDate(inclusions, date)) {
               recur.addExclusion(date);
               return true;
            }

            return false;
         case 2:
            if (recur.getExclusionCount() != 0) {
               recur.removeExclusion(date);
               return true;
            } else {
               return false;
            }
      }
   }

   private final boolean modifyInclusion(Recur recur, long[] exclusions, long date, int modifyType) {
      switch (modifyType) {
         case 0:
            return false;
         case 1:
         default:
            if (!this.arrayContainsDate(exclusions, date)) {
               recur.addInclusion(date);
               return true;
            }

            return false;
         case 2:
            if (recur.getInclusionCount() != 0) {
               recur.removeInclusion(date);
               return true;
            } else {
               return false;
            }
      }
   }

   private final boolean arrayContainsDate(long[] array, long date) {
      if (array != null && array.length > 0) {
         int dateIndex = Arrays.binarySearch(array, date, 0, array.length);
         if (dateIndex >= 0) {
            return true;
         }

         if (this._searchCalendar1 == null) {
            this._searchCalendar1 = Calendar.getInstance();
         }

         Calendar occurrence = this._searchCalendar1;
         occurrence.setTime((Date)(new Object(date)));
         int year = occurrence.get(1);
         int month = occurrence.get(2);
         int day = occurrence.get(5);
         if (this._searchCalendar2 == null) {
            this._searchCalendar2 = Calendar.getInstance();
         }

         Calendar cal = this._searchCalendar2;
         dateIndex = -dateIndex - 1;
         if (dateIndex < array.length) {
            cal.setTime((Date)(new Object(array[dateIndex])));
            if (cal.get(1) == year && cal.get(2) == month && cal.get(5) == day) {
               return true;
            }
         }

         if (dateIndex > 0) {
            cal.setTime((Date)(new Object(array[dateIndex - 1])));
            if (cal.get(1) == year && cal.get(2) == month && cal.get(5) == day) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private final void removeFieldsModel() {
      int uid = this._event.getUID();
      _eventContainer.removeSyncObject(uid);
      _eventContainer.commit();
   }

   private final EventFieldsModel getFieldsModel() {
      return _eventContainer.getEventFieldsModel(this._event.getUID());
   }

   private final void addFieldsModel(EventFieldsModel model) {
      int uid = this._event.getUID();
      model.setUID(uid);
      EventFieldsModel old = _eventContainer.getEventFieldsModel(uid);
      if (old != null) {
         _eventContainer.updateSyncObject(old, model);
      } else {
         _eventContainer.addSyncObject(model);
      }

      _eventContainer.commit();
   }

   EventImpl(int mode) {
      this._event = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)EventStatics._eventFactory.createInstance(null);
      this._dtend = null;
      this._modified = false;
      this._repeatRule = null;
      this._committedEvent = null;
      this._mode = mode;
   }
}
