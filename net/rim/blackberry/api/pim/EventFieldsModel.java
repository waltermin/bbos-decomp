package net.rim.blackberry.api.pim;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.utility.framework.RecurUtil;

final class EventFieldsModel implements PersistableRIMModel, SyncObject {
   private int _uid;
   private long _startDate;
   private int _frequency = 16;
   private int _interval = 1;
   private int _repeatCount;
   private long _endDate;
   private int _dayInWeek;
   private int _month;
   private int _weekInMonth;
   private int _dayInMonth;
   private int _dayInYear;
   private boolean _isDirty;
   private static final byte FIELD_END_OF_DATA = 0;
   private static final byte FIELD_UID = 1;
   private static final byte FIELD_START_DATE = 2;
   private static final byte FIELD_FREQ = 3;
   private static final byte FIELD_INTERVAL = 4;
   private static final byte FIELD_DAY_IN_MONTH = 5;
   private static final byte FIELD_DAY_IN_WEEK = 6;
   private static final byte FIELD_DAY_IN_YEAR = 7;
   private static final byte FIELD_MONTH = 8;
   private static final byte FIELD_WEEK_IN_MONTH = 9;
   private static final byte FIELD_COUNT = 10;
   private static final byte FIELD_END_DATE = 11;
   private static final int INT_LENGTH = 4;
   private static final int LONG_LENGTH = 8;

   public final byte[] getData() {
      DataBuffer buffer = (DataBuffer)(new Object(true));
      buffer.writeInt(-12975410);
      buffer.writeByte(1);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._uid);
      buffer.writeByte(2);
      buffer.writeCompressedInt(8);
      buffer.writeLong(this._startDate);
      buffer.writeByte(3);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._frequency);
      buffer.writeByte(4);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._interval);
      buffer.writeByte(5);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._dayInMonth);
      buffer.writeByte(6);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._dayInWeek);
      buffer.writeByte(7);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._dayInYear);
      buffer.writeByte(8);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._month);
      buffer.writeByte(9);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._weekInMonth);
      buffer.writeByte(10);
      buffer.writeCompressedInt(4);
      buffer.writeInt(this._repeatCount);
      buffer.writeByte(11);
      buffer.writeCompressedInt(8);
      buffer.writeLong(this._endDate);
      buffer.writeByte(0);
      return buffer.toArray();
   }

   public final void setStartDate(long start) {
      this._startDate = start;
   }

   public final void setUID(int uid) {
      this._uid = uid;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final long getStartDate() {
      return this._startDate;
   }

   public final void setFrequency(int freq) {
      this._frequency = freq;
   }

   public final int getFrequency() {
      return this._frequency;
   }

   public final void setInterval(int interval) {
      this._interval = interval;
   }

   public final int getInterval() {
      return this._interval;
   }

   public final void setRepeatCount(int count) {
      this._repeatCount = count;
   }

   public final int getRepeatCount() {
      return this._repeatCount;
   }

   public final void setEndDate(long endDate) {
      this._endDate = endDate;
   }

   public final long getEndDate() {
      return this._endDate;
   }

   public final void setWeekInMonth(int wk) {
      this._weekInMonth = wk;
   }

   public final int getWeekInMonth() {
      return this._weekInMonth;
   }

   public final void setDayNumber(int day) {
      this._dayInMonth = day;
   }

   public final int getDayNumber() {
      return this._dayInMonth;
   }

   public final void setDayInYear(int day) {
      this._dayInYear = day;
   }

   public final int getDayInYear() {
      return this._dayInYear;
   }

   public final void setDayInWeek(int days) {
      this._dayInWeek = days;
   }

   public final int getDayInWeek() {
      return this._dayInWeek;
   }

   public final void setMonth(int month) {
      this._month = month;
   }

   public final int getMonth() {
      return this._month;
   }

   public final boolean isDirty() {
      return this._isDirty;
   }

   public final void setDirty(boolean isDirty) {
      this._isDirty = isDirty;
   }

   public final EventFieldsModel copy() {
      EventFieldsModel newModel = new EventFieldsModel();
      newModel._frequency = this._frequency;
      newModel._interval = this._interval;
      newModel._dayInYear = this._dayInYear;
      newModel._dayInMonth = this._dayInMonth;
      newModel._dayInWeek = this._dayInWeek;
      newModel._endDate = this._endDate;
      newModel._month = this._month;
      newModel._repeatCount = this._repeatCount;
      newModel._uid = this._uid;
      newModel._startDate = this._startDate;
      newModel._weekInMonth = this._weekInMonth;
      return newModel;
   }

   public EventFieldsModel(byte[] data) {
      DataBuffer buffer = (DataBuffer)(new Object(data, 0, data.length, true));

      try {
         int signature = buffer.readInt();
         if (signature != -12975410) {
            return;
         }

         for (byte fieldId = buffer.readByte(); fieldId != 0; fieldId = buffer.readByte()) {
            int length = buffer.readCompressedInt();
            switch (fieldId) {
               case 0:
                  buffer.skipBytes(length);
                  break;
               case 1:
               default:
                  this._uid = buffer.readInt();
                  break;
               case 2:
                  this._startDate = buffer.readLong();
                  break;
               case 3:
                  this._frequency = buffer.readInt();
                  break;
               case 4:
                  this._interval = buffer.readInt();
                  break;
               case 5:
                  this._dayInMonth = buffer.readInt();
                  break;
               case 6:
                  this._dayInWeek = buffer.readInt();
                  break;
               case 7:
                  this._dayInYear = buffer.readInt();
                  break;
               case 8:
                  this._month = buffer.readInt();
                  break;
               case 9:
                  this._weekInMonth = buffer.readInt();
                  break;
               case 10:
                  this._repeatCount = buffer.readInt();
                  break;
               case 11:
                  this._endDate = buffer.readLong();
            }
         }
      } finally {
         return;
      }
   }

   public EventFieldsModel(net.rim.device.apps.api.calendar.modelcontrollerinterface.Event e) {
      this._uid = e.getUID();
      this._startDate = e.getStartDate(null);
      if (e.isRecurring()) {
         Recur recur = e.getRecurrenceCopy();
         this._frequency = RepeatRuleUtil.freqRecurToRepeat(recur.getRecurType());
         this._interval = recur.getRecurPeriod();
         if (recur.isFinite()) {
            this._endDate = recur.getEndDate();
         }

         this._dayInWeek = RepeatRuleUtil.dayInWeekRecurToRepeat(RecurUtil.getBitmapDaysOfWeek(recur));
         this._month = RepeatRuleUtil.getRecurMonth(recur);
         this._weekInMonth = RepeatRuleUtil.getRecurWeekInMonth(recur);
      }
   }

   public EventFieldsModel() {
   }
}
