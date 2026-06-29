package net.rim.device.apps.api.utility.framework;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.DateFormatSymbols;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.framework.model.Recur$RecurCapabilities;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;

public final class RecurUtil {
   public static final long RECUR_CAPABILITIES_KEY;
   public static final int SYNC_RECUR_NOREPEAT;
   public static final int SYNC_RECUR_DAILY;
   public static final int SYNC_RECUR_WEEKLY;
   public static final int SYNC_RECUR_MONTHLY;
   public static final int SYNC_RECUR_MONTHLY_BY_POS;
   public static final int SYNC_RECUR_YEARLY;
   public static final int SYNC_RECUR_YEARLY_BY_POS;
   public static final int SYNC_RECUR_MIXED;
   public static final int SYNC_RECUR_MIXED_BY_POS;
   public static final int SYNC_RECUR_EXCLUSION;
   public static final int SYNC_RECUR_MONTHLY_BY_POS_AND_CLASS;
   public static final int SYNC_RECUR_YEARLY_BY_POS_AND_CLASS;
   public static final int SYNC_RECUR_WEEKLY_DAYS;
   public static final int SYNC_RECUR_QUARTERLY;
   public static final int SYNC_RECUR_QUARTERLY_BY_POS;
   public static final int SYNC_RECUR_MIXED_BY_POS_NO_MONTH;
   public static final int RECURRENCE_FIELD_ID;
   public static final int EXCLUSIONS_FIELD_ID;
   public static final int RECUR_FDOW_FIELD_ID;
   public static final int INCLUSIONS_FIELD_ID;
   private static DateFormat _df = DateFormat.getInstance(48);
   private static Recur$Modifier _modifier = new Recur$Modifier();
   private static Calendar _calendar = Calendar.getInstance();
   private static Calendar _gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
   private static TimeZone _gmtTimeZone = TimeZone.getTimeZone(DateTimeUtilities.GMT);
   private static int[] _dateTimeFields1 = new int[7];
   private static final long DAY_MAX;
   private static final long WEEK_MAX;
   private static final long MONTH_MAX;
   private static final long YEAR_MAX;
   private static final int NUM_WEEKEND_MODIFIERS;
   private static final int NUM_WEEKDAY_MODIFIERS;
   private static final int NUM_DAY_MODIFIERS;
   private static final int ANY_DAY;
   private static final int ANY_WEEKDAY;
   private static final int ANY_WEEKEND_DAY;
   public static final int LAST_WEEK;
   public static final int FIFTH_WEEK;
   private static String EMPTY_STRING = "";
   private static MessageFormat _mf;
   private static Object[] _mfParms;
   private static MessageFormat _occursEveryClause;
   private static MessageFormat _occursEveryPluralClause;
   private static Object[] _occursEveryParams;
   private static MessageFormat _untilClause;
   private static MessageFormat _dowClause;
   private static MessageFormat _dowClausePlural;
   private static MessageFormat _monthClause;
   private static MessageFormat _monthClausePlural;
   private static long[] _exclusions = new long[0];
   private static long[] _inclusions = new long[0];
   private static final int OCCURS_EVERY_INDEX;
   private static final int UNTIL_INDEX;
   private static final int ON_DOW_INDEX;
   private static final int IN_MONTH_INDEX;

   public static final void addDaysModifier(Recur recur, int days) {
      addDaysModifier(recur, days, 0);
   }

   public static final void addDaysModifier(Recur recur, int days, int weekOfMonth) {
      if (days != 0) {
         for (int i = 0; i < 7; i++) {
            if ((days & 1 << i) != 0) {
               addDayOfWeekModifier(recur, i, weekOfMonth);
            }
         }
      }
   }

   public static final void addDayOfWeekModifier(Recur recur, int dayOfWeek, int weekOfMonth) {
      if (dayOfWeek >= 0 && dayOfWeek <= 6 && weekOfMonth >= -1 && weekOfMonth <= 5) {
         Recur$Modifier modifier = new Recur$Modifier();
         modifier.parm1 = dayOfWeek;
         modifier.parm2 = weekOfMonth;
         recur.addModificationValue(1, modifier);
      }
   }

   public static final void addDayOfClassModifier(Recur recur, int dayOfClass, int weekOfMonth) {
      if (dayOfClass >= 0 && dayOfClass <= 9 && weekOfMonth >= -1 && weekOfMonth <= 5) {
         switch (dayOfClass) {
            case 6:
               addDayOfWeekModifier(recur, dayOfClass, weekOfMonth);
               break;
            case 7:
            default:
               for (int i = 0; i < 7; i++) {
                  addDayOfWeekModifier(recur, i, weekOfMonth);
               }
               break;
            case 8:
               for (int i = 1; i <= 5; i++) {
                  addDayOfWeekModifier(recur, i, weekOfMonth);
               }
               break;
            case 9:
               addDayOfWeekModifier(recur, 0, weekOfMonth);
               addDayOfWeekModifier(recur, 6, weekOfMonth);
               return;
         }
      }
   }

   public static final void addMonthModifier(Recur recur, int month) {
      if (month >= 1 && month <= 12) {
         Recur$Modifier modifier = new Recur$Modifier();
         modifier.parm1 = month - 1;
         recur.addModificationValue(2, modifier);
      }
   }

   public static final int getBitmapDaysOfWeek(Recur recur) {
      int count = recur.numModifierValues(1);
      int days = 0;
      if (count != 0) {
         for (int i = 0; i < count; i++) {
            synchronized (_modifier) {
               recur.getModifierAt(1, i, _modifier);
               days |= (byte)(1 << _modifier.parm1);
            }
         }
      }

      return days;
   }

   public static final int getDayOfWeek(Recur recur) {
      int count = recur.numModifierValues(1);
      int days = 0;
      if (count > 0) {
         synchronized (_modifier) {
            recur.getModifierAt(1, 0, _modifier);
            days = _modifier.parm1;
         }

         if (count == 2) {
            return 9;
         }

         if (count == 5) {
            return 8;
         }

         if (count == 7) {
            days = 7;
         }
      }

      return days;
   }

   public static final void setDayOfMonth(Recur recur, int dayOfMonth) {
      synchronized (_modifier) {
         Recur$Modifier modifier = new Recur$Modifier();
         modifier.parm1 = dayOfMonth;
         modifier.parm2 = 0;
         recur.addModificationValue(3, modifier);
      }
   }

   public static final int getDayOfMonth(Recur recur) {
      int count = recur.numModifierValues(3);
      int dayOfMonth = -1;
      if (count > 0) {
         synchronized (_modifier) {
            recur.getModifierAt(3, 0, _modifier);
            return _modifier.parm1;
         }
      } else {
         return dayOfMonth;
      }
   }

   public static final int getWeekOfMonth(Recur recur) {
      int count = recur.numModifierValues(1);
      int weeks = 0;
      if (count > 0) {
         synchronized (_modifier) {
            recur.getModifierAt(1, 0, _modifier);
            return _modifier.parm2;
         }
      } else {
         return weeks;
      }
   }

   public static final int getMonthOfYear(Recur recur) {
      int count = recur.numModifierValues(1);
      int months = 0;
      if (count == 1) {
         synchronized (_modifier) {
            recur.getModifierAt(2, 0, _modifier);
            return _modifier.parm1 + 1;
         }
      } else {
         return months;
      }
   }

   public static final String[] makeChoiceArray() {
      ResourceBundle rb = ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common");
      return rb.getStringArray(9076);
   }

   public static final byte getRecurValue(int offset) {
      switch (offset) {
         case 0:
            return 0;
         case 1:
         default:
            return 1;
         case 2:
            return 2;
         case 3:
            return 3;
         case 4:
            return 4;
      }
   }

   public static final long getRecurDurMax(byte recurType) {
      switch (recurType) {
         case 0:
            return -1;
         case 1:
            return 86400000;
         case 2:
            return 604800000;
         case 3:
            return 2678400000L;
         case 4:
         default:
            return 31536000000L;
      }
   }

   public static final int getRecurOffset(byte recurType) {
      switch (recurType) {
         case 0:
            return 0;
         case 1:
            return 1;
         case 2:
            return 2;
         case 3:
            return 3;
         case 4:
         default:
            return 4;
      }
   }

   public static final String[] makeEndChoiceArray(boolean isNeverOptionAllowed) {
      ResourceBundle rb = ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common");
      String[] choiceArray = rb.getStringArray(9078);
      if (!isNeverOptionAllowed) {
         String[] tempArray = new Object[choiceArray.length - 1];
         System.arraycopy(choiceArray, 1, tempArray, 0, choiceArray.length - 1);
         choiceArray = tempArray;
      }

      return choiceArray;
   }

   public static final int getEndChoice(boolean finite) {
      return finite ? 1 : 0;
   }

   public static final boolean getFiniteFromEndChoice(int choice) {
      return choice == 1;
   }

   public static final boolean isRelative(Recur recur) {
      return recur.numModifierValues(1) > 0;
   }

   public static final void makeRelativeMonth(Recur recur, long datetime, TimeZone tz) {
      ((CalendarExtensions)_calendar).setTimeLong(datetime);
      _calendar.setTimeZone(tz);
      int dayOfMonthOffset = _calendar.get(5) - 1;
      int dayOfWeekOffset = _calendar.get(7) - 1;
      _modifier.parm1 = dayOfWeekOffset;
      _modifier.parm2 = dayOfMonthOffset / 7 + 1;
      recur.clearAllModifiers();
      recur.addModificationValue(1, _modifier);
   }

   public static final void makeRelativeYear(Recur recur, long datetime, TimeZone tz) {
      ((CalendarExtensions)_calendar).setTimeLong(datetime);
      _calendar.setTimeZone(tz);
      int dayOfMonthOffset = _calendar.get(5) - 1;
      int dayOfWeekOffset = _calendar.get(7) - 1;
      int monthOffset = _calendar.get(2) - 0;
      _modifier.parm1 = dayOfWeekOffset;
      _modifier.parm2 = dayOfMonthOffset / 7 + 1;
      recur.clearAllModifiers();
      recur.addModificationValue(1, _modifier);
      _modifier.parm1 = monthOffset;
      _modifier.parm2 = 0;
      recur.addModificationValue(2, _modifier);
   }

   public static final long[] getRemovedOccurrences(Recur oldRecur, Recur newRecur) {
      long[] removedOccurrences = new long[0];
      long[] oldArray = oldRecur.getInclusions(null);
      long[] newArray = newRecur.getInclusions(null);
      return subtract(oldArray, newArray);
   }

   public static final long[] getAddedExclusions(Recur oldRecur, Recur newRecur) {
      long[] addedExclusions = new long[0];
      long[] oldArray = oldRecur.getExclusions(null);
      long[] newArray = newRecur.getExclusions(null);
      return subtract(newArray, oldArray);
   }

   private static final long[] subtract(long[] a1, long[] a2) {
      long[] difference = new long[0];
      int length1 = a1 != null ? a1.length : 0;
      int length2 = a2 != null ? a2.length : 0;

      for (int i = 0; i < length1; i++) {
         if (Arrays.binarySearch(a2, a1[i], 0, length2) < 0) {
            Array.resize(difference, difference.length + 1);
            difference[difference.length - 1] = a1[i];
         }
      }

      return difference;
   }

   public static final void getDesc(Recur recur, boolean allDay, StringBuffer recurDesc, TimeZone tz) {
      ResourceBundle rb = ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common");
      recurDesc.setLength(0);
      byte recurType = 0;
      if (recur != null) {
         recurType = recur.getRecurType();
         if (recur.getInclusionCount() > 0) {
            recurDesc.append(rb.getString(9079));
            return;
         }
      }

      if (recurType == 0) {
         recurDesc.append(rb.getString(9080));
      } else {
         if (_mf == null) {
            _mf = (MessageFormat)(new Object(rb.getString(9191)));
            _mfParms = new Object[4];
            _occursEveryClause = (MessageFormat)(new Object(rb.getString(9182)));
            _occursEveryPluralClause = (MessageFormat)(new Object(rb.getString(9183)));
            _occursEveryParams = new Object[2];
            _untilClause = (MessageFormat)(new Object(rb.getString(9184)));
            _dowClause = (MessageFormat)(new Object(rb.getString(9185)));
            _dowClausePlural = (MessageFormat)(new Object(rb.getString(9186)));
            _monthClause = (MessageFormat)(new Object(rb.getString(9189)));
            _monthClausePlural = (MessageFormat)(new Object(rb.getString(9190)));
         }

         Object[] mfParms = _mfParms;

         for (int i = 0; i < mfParms.length; i++) {
            mfParms[i] = EMPTY_STRING;
         }

         for (int i = 0; i < _occursEveryParams.length; i++) {
            _occursEveryParams[i] = EMPTY_STRING;
         }

         boolean isPlural = false;
         int recurPeriod = recur.getRecurPeriod();
         MessageFormat occursEveryClause;
         switch (recurPeriod) {
            case 1:
               occursEveryClause = _occursEveryClause;
               break;
            default:
               occursEveryClause = _occursEveryPluralClause;
               _occursEveryParams[1] = Integer.toString(recurPeriod);
               isPlural = true;
         }

         int recurrenceUnitId = 0;
         switch (recurType) {
            case 0:
               break;
            case 1:
               if (recurPeriod < 2) {
                  recurrenceUnitId = 9066;
               } else {
                  recurrenceUnitId = 9072;
               }
               break;
            case 2:
               if (recurPeriod < 2) {
                  recurrenceUnitId = 9065;
               } else {
                  recurrenceUnitId = 9071;
               }
               break;
            case 3:
               if (recurPeriod < 2) {
                  recurrenceUnitId = 9064;
               } else {
                  recurrenceUnitId = 9070;
               }
               break;
            case 4:
            default:
               if (recurPeriod < 2) {
                  recurrenceUnitId = 9063;
               } else {
                  recurrenceUnitId = 9069;
               }
         }

         if (recurrenceUnitId > 0) {
            _occursEveryParams[0] = rb.getString(recurrenceUnitId);
         }

         mfParms[0] = occursEveryClause.format(_occursEveryParams);
         if (recur.isFinite()) {
            ((CalendarExtensions)_calendar).setTimeLong(recur.getEndDate());
            if (allDay) {
               _calendar.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
            } else if (tz != null) {
               _calendar.setTimeZone(tz);
            } else {
               _calendar.setTimeZone(TimeZone.getDefault());
            }

            _untilClause.setFormat(0, _df);
            mfParms[1] = _untilClause.format(new Object[]{_calendar});
         }

         int max = recur.numModifierValues(2);
         int parm1 = 0;
         int parm2 = 0;
         if (max > 0) {
            synchronized (_modifier) {
               recur.getModifierAt(2, 0, _modifier);
               parm1 = _modifier.parm1;
               parm2 = _modifier.parm2;
            }

            String monthName = DateFormatSymbols.getInstance().getShortMonths()[parm1];
            MessageFormat monthClause = isPlural ? _monthClausePlural : _monthClause;
            mfParms[3] = monthClause.format(new Object[]{monthName});
         }

         max = recur.numModifierValues(1);
         if (max > 0) {
            MessageFormat dowClause = isPlural ? _dowClausePlural : _dowClause;
            Object[] dowParams = new Object[]{EMPTY_STRING, EMPTY_STRING};
            synchronized (_modifier) {
               recur.getModifierAt(1, 0, _modifier);
               parm1 = _modifier.parm1;
               parm2 = _modifier.parm2;
            }

            if (parm2 != 0) {
               if (parm2 == -1) {
                  dowParams[1] = rb.getStringArray(9074)[5];
               } else {
                  dowParams[1] = rb.getStringArray(9074)[parm2];
               }

               switch (max) {
                  case 2:
                     dowParams[0] = rb.getString(9068);
                     break;
                  case 5:
                     dowParams[0] = rb.getString(9067);
                     break;
                  case 7:
                     dowParams[0] = rb.getString(9066);
                     break;
                  default:
                     dowParams[0] = rb.getStringArray(9094)[parm1];
               }
            } else {
               recurDesc.append(rb.getStringArray(9094)[parm1]);

               for (int i = 1; i < max; i++) {
                  recurDesc.append(rb.getString(9085));
                  synchronized (_modifier) {
                     recur.getModifierAt(1, i, _modifier);
                     parm1 = _modifier.parm1;
                  }

                  recurDesc.append(rb.getStringArray(9094)[parm1]);
               }

               dowParams[0] = recurDesc.toString();
               recurDesc.setLength(0);
            }

            mfParms[2] = dowClause.format(dowParams);
         }

         _mf.format(mfParms, recurDesc, null);
      }
   }

   public static final void serializeRecurInfo(long startDate, TimeZone tz, Recur recurInfo, boolean allDay, DataBuffer buffer) {
      Calendar cal = Calendar.getInstance();
      if (buffer != null && recurInfo != null) {
         int recurType = recurInfo != null ? recurInfo.getRecurType() : 0;
         if (recurType != 0) {
            byte dayCount = 1;
            short frequency = (short)recurInfo.getRecurPeriod();
            int rstart = DateTimeUtilities.convertMillisecondsToEpoch(startDate);
            int rstop = -1;
            byte days = 0;
            byte weeks = 0;
            short months = 0;
            short numExDates = 0;
            cal.setTimeZone(tz);
            ((CalendarExtensions)cal).setTimeLong(startDate);
            if (recurInfo.isFinite()) {
               long endDate = allDay ? adjustAllDayDate(recurInfo.getEndDate(), tz) : recurInfo.getEndDate();
               rstop = DateTimeUtilities.convertMillisecondsToEpoch(endDate);
            }

            byte rType;
            switch (recurType) {
               case 0:
                  rType = -1;
                  break;
               case 1:
               default:
                  rType = 1;
                  break;
               case 2:
                  rType = 12;
                  days = (byte)getBitmapDaysOfWeek(recurInfo);
                  break;
               case 3:
               case 4:
                  int count = recurInfo.numModifierValues(1);
                  if (count == 0) {
                     rType = 3;
                     days = (byte)getDayOfMonth(recurInfo);
                     if (days == -1) {
                        days = (byte)cal.get(5);
                     }
                  } else {
                     if (count == 1) {
                        rType = 4;
                     } else {
                        rType = 10;
                     }

                     days = (byte)getDayOfWeek(recurInfo);
                     weeks = (byte)getWeekOfMonth(recurInfo);
                  }

                  if (recurType == 4) {
                     months = (short)getMonthOfYear(recurInfo);
                     count = recurInfo.numModifierValues(2);
                     if (count == 1) {
                        if (rType == 4) {
                           rType = 6;
                        } else {
                           rType = 11;
                           months = (short)(cal.get(2) + 1);
                        }
                     } else {
                        rType = 5;
                        months = (short)(cal.get(2) + 1);
                        days = (byte)cal.get(5);
                     }
                  }
            }

            buffer.writeShort(18);
            buffer.writeByte(12);
            buffer.writeByte(rType);
            buffer.writeByte(dayCount);
            buffer.writeShort(frequency);
            buffer.writeInt(rstart);
            buffer.writeInt(rstop);
            buffer.writeByte(days);
            buffer.writeByte(weeks);
            buffer.writeShort(months);
            buffer.writeShort(numExDates);
            synchronized (_exclusions) {
               recurInfo.getExclusions(_exclusions);
               if (_exclusions.length != 0) {
                  buffer.writeShort(_exclusions.length << 2);
                  buffer.writeByte(13);

                  for (int i = 0; i < _exclusions.length; i++) {
                     buffer.writeInt(
                        allDay
                           ? DateTimeUtilities.convertMillisecondsToEpoch(adjustAllDayDate(_exclusions[i], tz))
                           : DateTimeUtilities.convertMillisecondsToEpoch(_exclusions[i])
                     );
                  }
               }
            }

            synchronized (_inclusions) {
               recurInfo.getInclusions(_inclusions);
               if (_inclusions.length != 0) {
                  buffer.writeShort(_inclusions.length << 2);
                  buffer.writeByte(32);

                  for (int i = 0; i < _inclusions.length; i++) {
                     buffer.writeInt(
                        allDay
                           ? DateTimeUtilities.convertMillisecondsToEpoch(adjustAllDayDate(_inclusions[i], tz))
                           : DateTimeUtilities.convertMillisecondsToEpoch(_inclusions[i])
                     );
                  }
               }
            }

            buffer.writeShort(1);
            buffer.writeByte(31);
            buffer.writeByte(recurInfo.getFirstDayOfWeek() - 1);
         }
      } else {
         throw new Object();
      }
   }

   public static final int parseRecurInfo(
      Recur recurInfo, boolean allDay, TimeZone tz, DataBuffer data, int recurrenceDataOffset, int exclusionRecordOffset, int inclusionRecordOffset
   ) {
      try {
         data.setPosition(recurrenceDataOffset);
         byte recurType = data.readByte();
         data.readByte();
         short frequency = data.readShort();
         int rstart = data.readInt();
         int rstop = data.readInt();
         byte days = data.readByte();
         byte weeks = data.readByte();
         short months = data.readShort();
         data.readShort();
         if (rstop != -1) {
            long endDate = DateTimeUtilities.convertEpochToMilliseconds(rstop);
            if (allDay) {
               _calendar.setTimeZone(tz);
               ((CalendarExtensions)_calendar).setTimeLong(endDate);
               endDate = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
            }

            recurInfo.setEndDate(endDate);
         }

         if (frequency > 1) {
            recurInfo.setRecurPeriod(frequency);
         }

         switch (recurType) {
            case 0:
            case 7:
            case 8:
            case 9:
               break;
            case 1:
            default:
               recurInfo.setRecurType((byte)1);
               break;
            case 2:
            case 12:
               recurInfo.setRecurType((byte)2);
               addDaysModifier(recurInfo, days);
               break;
            case 3:
               recurInfo.setRecurType((byte)3);
               setDayOfMonth(recurInfo, days);
               break;
            case 4:
               recurInfo.setRecurType((byte)3);
               addDayOfWeekModifier(recurInfo, days, weeks);
               break;
            case 5:
               recurInfo.setRecurType((byte)4);
               break;
            case 6:
               recurInfo.setRecurType((byte)4);
               addDayOfWeekModifier(recurInfo, days, weeks);
               addMonthModifier(recurInfo, months);
               break;
            case 10:
               recurInfo.setRecurType((byte)3);
               addDayOfClassModifier(recurInfo, days, weeks);
               break;
            case 11:
               recurInfo.setRecurType((byte)4);
               addDayOfClassModifier(recurInfo, days, weeks);
               addMonthModifier(recurInfo, months);
         }

         if (exclusionRecordOffset != -1) {
            data.setPosition(exclusionRecordOffset);
            int exclusionCount = data.readShort() / 4;
            data.readByte();
            _calendar.setTimeZone(tz);

            for (int i = 0; i < exclusionCount; i++) {
               int intData = data.readInt();
               if (intData != -1) {
                  long excludedTime = DateTimeUtilities.convertEpochToMilliseconds(intData);
                  if (allDay) {
                     ((CalendarExtensions)_calendar).setTimeLong(excludedTime);
                     excludedTime = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
                  }

                  recurInfo.addExclusion(excludedTime);
               }
            }
         }

         if (inclusionRecordOffset != -1) {
            data.setPosition(inclusionRecordOffset);
            int inclusionCount = data.readShort() / 4;
            data.readByte();

            for (int i = 0; i < inclusionCount; i++) {
               int intData = data.readInt();
               if (intData != -1) {
                  long includedTime = DateTimeUtilities.convertEpochToMilliseconds(intData);
                  if (allDay) {
                     ((CalendarExtensions)_calendar).setTimeLong(includedTime);
                     includedTime = DateTimeUtilities.copyCalendar(_calendar, _gmtCalendar);
                  }

                  recurInfo.addInclusion(includedTime);
               }
            }
         }

         return rstart;
      } finally {
         ;
      }
   }

   public static final long adjustAllDayDate(long dateToAdjust, TimeZone tz) {
      long result = 0;
      int[] fields = _dateTimeFields1;
      _calendar.setTimeZone(_gmtTimeZone);
      ((CalendarExtensions)_calendar).setTimeLong(dateToAdjust);
      fields = DateTimeUtilities.getCalendarFields(_calendar, fields);
      fields[5] = fields[5] = 0;
      _calendar.setTimeZone(tz);
      DateTimeUtilities.setCalendarFields(_calendar, fields);
      return ((CalendarExtensions)_calendar).getTimeLong();
   }

   public static final Recur$RecurCapabilities getRecurrenceCapabilities() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Object o = ar.getOrWaitFor(-2874103213567372899L);
      if (!(o instanceof Recur$RecurCapabilities)) {
         Recur$RecurCapabilities var3 = new Recur$RecurCapabilities();
         ar.replace(-2874103213567372899L, var3);
         return var3;
      } else {
         return (Recur$RecurCapabilities)o;
      }
   }

   public static final void setRecurrenceCapabilities(Recur$RecurCapabilities capabilities) {
      if (capabilities == null) {
         capabilities = new Recur$RecurCapabilities();
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.replace(-2874103213567372899L, capabilities);
   }
}
