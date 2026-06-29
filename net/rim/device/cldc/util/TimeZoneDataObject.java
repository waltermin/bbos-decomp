package net.rim.device.cldc.util;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.Comparator;
import net.rim.vm.Persistable;

public class TimeZoneDataObject implements Persistable, SyncObject {
   int _uid;
   int _tzid;
   String _zoneStringID;
   int _gmtOffset;
   int _dstAmount;
   int _dstStartMode;
   int _dstStartMonth;
   int _dstStartDayOfWeek;
   int _dstStartDay;
   int _dstStartTime;
   int _dstEndMode;
   int _dstEndMonth;
   int _dstEndDayOfWeek;
   int _dstEndDay;
   int _dstEndTime;
   int _builtInIndex = -1;
   String _defaultLongDescription;
   String _defaultShortDescription;
   int _mappedTZID;
   boolean _hidden;
   private static TimeZoneDataObject$TimeZoneDataObjectComparator _comparator = new TimeZoneDataObject$TimeZoneDataObjectComparator();

   void setBuiltInIndex(int builtInIndex) {
      this._builtInIndex = builtInIndex;
   }

   public String getDefaultLongDescription() {
      return this._defaultLongDescription;
   }

   public String getDefaultShortDescription() {
      return this._defaultShortDescription;
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   public int getBuiltInIndex() {
      return this._builtInIndex;
   }

   public int getTimeZoneID() {
      return this._tzid;
   }

   public boolean isBuiltInData() {
      return this._builtInIndex != -1;
   }

   public int getMappedTZID() {
      return this._mappedTZID;
   }

   public boolean isHidden() {
      return this._hidden;
   }

   public String getTimeZoneStringID() {
      return this._zoneStringID;
   }

   public int getGMTOffset() {
      return this._gmtOffset;
   }

   public int getDSTAmount() {
      return this._dstAmount;
   }

   public int getDSTStartMode() {
      return this._dstStartMode;
   }

   public int getDSTStartMonth() {
      return this._dstStartMonth;
   }

   public int getDSTStartDayOfWeek() {
      return this._dstStartDayOfWeek;
   }

   public int getDSTStartDay() {
      return this._dstStartDay;
   }

   public int getDSTStartTime() {
      return this._dstStartTime;
   }

   public int getDSTEndMode() {
      return this._dstEndMode;
   }

   public int getDSTEndMonth() {
      return this._dstEndMonth;
   }

   public int getDSTEndDayOfWeek() {
      return this._dstEndDayOfWeek;
   }

   public int getDSTEndDay() {
      return this._dstEndDay;
   }

   public int getDSTEndTime() {
      return this._dstEndTime;
   }

   public TimeZoneDataObject(
      int uid,
      int tzid,
      String zoneStringID,
      int gmtOffset,
      int dstAmount,
      int dstStartMode,
      int dstStartMonth,
      int dstStartDayOfWeek,
      int dstStartDay,
      int dstStartTime,
      int dstEndMode,
      int dstEndMonth,
      int dstEndDayOfWeek,
      int dstEndDay,
      int dstEndTime,
      String defaultLongDescription,
      String defaultShortDescription,
      int mappedTZID,
      boolean hidden
   ) {
      this._uid = uid;
      this._tzid = tzid;
      this._zoneStringID = zoneStringID;
      this._gmtOffset = gmtOffset;
      this._dstAmount = dstAmount;
      this._dstStartMode = dstStartMode;
      this._dstStartMonth = dstStartMonth;
      this._dstStartDayOfWeek = dstStartDayOfWeek;
      this._dstStartDay = dstStartDay;
      this._dstStartTime = dstStartTime;
      this._dstEndMode = dstEndMode;
      this._dstEndMonth = dstEndMonth;
      this._dstEndDayOfWeek = dstEndDayOfWeek;
      this._dstEndDay = dstEndDay;
      this._dstEndTime = dstEndTime;
      this._defaultLongDescription = defaultLongDescription;
      this._defaultShortDescription = defaultShortDescription;
      this._mappedTZID = mappedTZID;
      this._hidden = hidden;
   }

   public static Comparator getComparator() {
      return _comparator;
   }

   public TimeZoneDataObject(
      int tzid,
      String zoneStringID,
      int gmtOffset,
      int dstAmount,
      int dstStartMode,
      int dstStartMonth,
      int dstStartDayOfWeek,
      int dstStartDay,
      int dstStartTime,
      int dstEndMode,
      int dstEndMonth,
      int dstEndDayOfWeek,
      int dstEndDay,
      int dstEndTime,
      String defaultLongDescription,
      String defaultShortDescription,
      int mappedTZID,
      boolean hidden
   ) {
      this(
         UIDGenerator.getUID(),
         tzid,
         zoneStringID,
         gmtOffset,
         dstAmount,
         dstStartMode,
         dstStartMonth,
         dstStartDayOfWeek,
         dstStartDay,
         dstStartTime,
         dstEndMode,
         dstEndMonth,
         dstEndDayOfWeek,
         dstEndDay,
         dstEndTime,
         defaultLongDescription,
         defaultShortDescription,
         mappedTZID,
         hidden
      );
   }
}
