package net.rim.device.cldc.util;

import java.util.TimeZone;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

public final class TimeService {
   private String _defaultTimeZoneID;
   private TimeZone _tzObject;
   private IntHashtable _timeZoneTable;
   private TimeZoneDataLocalizer _localizer;
   private PersistentObject _persistableTimeZoneData;
   private TimeZoneDataObject[] _sortedTimeZoneDataObjectList;
   private int[] _indexMap;
   private boolean _automaticallyAdjustClockForDSTTime = true;
   private PersistentObject _persistableBitfieldSettings;
   private int _timeServiceSettings;
   private int TIME_SERVICE_SETTINGS_AUTO_ADJUST_FOR_DST = 1;
   private static final long DEFAULT_TIMEZONE = 2989539104253779367L;
   private static final long AUTO_ADJUST_CLOCK_FOR_DST_SETTING = 987576202591045751L;
   private static final long TIMEZONEDATA = 6330365711713234846L;
   private static final int TIME_ZONE_SHORT_NAME = 0;
   private static final int TIME_ZONE_LONG_NAME = 1;
   private static final int TIME_ZONE_DEFAULT_NAME = 2;
   private static TimeService _instance;
   private static final long REGISTRY_KEY = 2569363511892233325L;

   private TimeService() {
      this.intializeTimeZones();
      this._defaultTimeZoneID = this.internalGetDefaultTimeZoneID();
      this._persistableBitfieldSettings = RIMPersistentStore.getPersistentObject(987576202591045751L);
      Integer i = (Integer)this._persistableBitfieldSettings.getContents();
      if (i == null) {
         this._timeServiceSettings = this.TIME_SERVICE_SETTINGS_AUTO_ADJUST_FOR_DST;
         i = new Integer(this._timeServiceSettings);
         this._persistableBitfieldSettings.setContents(i, 51, false);
         this._persistableBitfieldSettings.commit();
      } else {
         this._timeServiceSettings = i;
         if ((this._timeServiceSettings & this.TIME_SERVICE_SETTINGS_AUTO_ADJUST_FOR_DST) == 0) {
            this._automaticallyAdjustClockForDSTTime = false;
         }
      }

      this.updateTimeZoneOffset();
   }

   public final synchronized void setAutomaticClockAdjustmentForDST(boolean automaticallyAdjustClockForDSTTime) {
      this._automaticallyAdjustClockForDSTTime = automaticallyAdjustClockForDSTTime;
      this._timeServiceSettings = this._timeServiceSettings | this.TIME_SERVICE_SETTINGS_AUTO_ADJUST_FOR_DST;
      this._persistableBitfieldSettings.setContents(new Integer(this._timeServiceSettings), 51, false);
      this._persistableBitfieldSettings.commit();
   }

   public final synchronized boolean automaticClockAdjustmentForDST() {
      return this._automaticallyAdjustClockForDSTTime;
   }

   public final synchronized void setLocalizer(TimeZoneDataLocalizer localizer) {
      this._localizer = localizer;
   }

   private final void intializeTimeZones() {
      this._timeZoneTable = new IntHashtable(88);
      this._persistableTimeZoneData = RIMPersistentStore.getPersistentObject(6330365711713234846L);
      this._sortedTimeZoneDataObjectList = (TimeZoneDataObject[])this._persistableTimeZoneData.getContents();
      if (this._sortedTimeZoneDataObjectList != null) {
         for (int i = 0; i < this._sortedTimeZoneDataObjectList.length; i++) {
            TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[i];
            this._timeZoneTable.put(tzdo.getTimeZoneID(), tzdo);
         }
      } else {
         this._sortedTimeZoneDataObjectList = new TimeZoneDataObject[88];

         for (int i = 0; i < 88; i++) {
            int dataOffset = i * 9;
            int idOffset = TimeZoneData.ZONE_ID_OFFSETS[i];
            int idLength = TimeZoneData.ZONE_ID_OFFSETS[i + 1] - idOffset;
            String tzName = new String(TimeZoneData.ZONE_ID_DATA, idOffset, idLength);
            short[] tzdata = TimeZoneData.ZONE_DATA;
            TimeZoneDataObject tzdo = new TimeZoneDataObject(
               tzdata[dataOffset + 0],
               tzName,
               tzdata[dataOffset + 1],
               tzdata[dataOffset + 2],
               (tzdata[dataOffset + 3] & 3840) >> 8,
               (tzdata[dataOffset + 3] & 240) >> 4,
               tzdata[dataOffset + 3] & 15,
               tzdata[dataOffset + 4],
               tzdata[dataOffset + 5],
               (tzdata[dataOffset + 6] & 3840) >> 8,
               (tzdata[dataOffset + 6] & 240) >> 4,
               tzdata[dataOffset + 6] & 15,
               tzdata[dataOffset + 7],
               tzdata[dataOffset + 8],
               null,
               null,
               -1,
               false
            );
            tzdo.setBuiltInIndex(i);
            this._timeZoneTable.put(tzdata[dataOffset + 0], tzdo);
            this._sortedTimeZoneDataObjectList[i] = tzdo;
         }

         Arrays.sort(this._sortedTimeZoneDataObjectList, TimeZoneDataObject.getComparator());
         this._persistableTimeZoneData.setContents(this._sortedTimeZoneDataObjectList, 51, false);
         this._persistableTimeZoneData.commit();
      }

      this._indexMap = new int[this._sortedTimeZoneDataObjectList.length];
      int i = 0;

      while (i < this._sortedTimeZoneDataObjectList.length) {
         this._indexMap[i] = i++;
      }
   }

   private final int getTimeZoneBuiltInIndex(int tzid) {
      int result = -1;

      for (int i = 0; i < 88; i++) {
         int dataOffset = i * 9;
         if (tzid == TimeZoneData.ZONE_DATA[dataOffset]) {
            return i;
         }
      }

      return result;
   }

   private final synchronized String[] getTimeZoneNames(int type) {
      int size = this._indexMap.length;
      String[] result = new String[size];

      for (int i = 0; i < size; i++) {
         result[i] = this.getTimeZoneDescriptionByIndex(i, type);
      }

      return result;
   }

   public final synchronized String[] getTimeZoneNamesLong() {
      return this.getTimeZoneNames(1);
   }

   public final synchronized String[] getTimeZoneNamesShort() {
      return this.getTimeZoneNames(0);
   }

   public final synchronized String[] getTimeZoneIDs() {
      int size = this._indexMap.length;
      String[] result = new String[size];

      for (int i = 0; i < size; i++) {
         result[i] = this._sortedTimeZoneDataObjectList[this._indexMap[i]].getTimeZoneStringID();
      }

      return result;
   }

   public final synchronized int[] getTimeZoneUIDs() {
      int size = this._sortedTimeZoneDataObjectList.length;
      int[] result = new int[size];

      for (int i = 0; i < size; i++) {
         result[i] = this._sortedTimeZoneDataObjectList[i].getTimeZoneID();
      }

      return result;
   }

   public final synchronized TimeZone[] getTimeZones() {
      int size = this._sortedTimeZoneDataObjectList.length;
      TimeZone[] result = new TimeZone[size];

      for (int i = 0; i < size; i++) {
         result[i] = this.getZone(this.getTimeZoneID(this._sortedTimeZoneDataObjectList[i]));
      }

      return result;
   }

   public final synchronized TimeZoneDataObject[] getTimeZoneDataObjects() {
      return this._sortedTimeZoneDataObjectList;
   }

   public final synchronized String getTimeZoneIDFromIndex(int index) {
      String result = null;
      if (index >= 0 && index < this._indexMap.length) {
         result = this._sortedTimeZoneDataObjectList[this._indexMap[index]].getTimeZoneStringID();
      }

      return result;
   }

   public final synchronized int getTimeZoneUIDFromIndex(int index) {
      int result = -1;
      if (index >= 0 && index < this._indexMap.length) {
         result = this._sortedTimeZoneDataObjectList[this._indexMap[index]].getTimeZoneID();
      }

      return result;
   }

   public final synchronized TimeZone getTimeZone(String ID) {
      if (this._tzObject != null && ID.equals(this._tzObject.getID())) {
         return this._tzObject;
      }

      TimeZone tzObject = this.getZone(this.getTimeZoneID(ID));
      if (tzObject != null) {
         this._tzObject = tzObject;
         return tzObject;
      }

      tzObject = this.getCustomZone(ID);
      if (tzObject != null) {
         this._tzObject = tzObject;
      }

      return tzObject;
   }

   public final int getTimeZoneID(TimeZoneDataObject tzdo) {
      int result = tzdo.getMappedTZID();
      return result == -1 ? tzdo.getTimeZoneID() : result;
   }

   public final int getTimeZoneID(String timeZoneName) {
      int result = -1;

      for (int i = 0; timeZoneName != null && i < this._sortedTimeZoneDataObjectList.length; i++) {
         TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[i];
         if (timeZoneName.equalsIgnoreCase(tzdo.getTimeZoneStringID())) {
            result = tzdo.getMappedTZID();
            if (result == -1) {
               return tzdo.getTimeZoneID();
            }
            break;
         }
      }

      return result;
   }

   public final boolean isTimeZoneConfigured() {
      return this._defaultTimeZoneID != null;
   }

   private final void updateTimeZoneOffset() {
      TimeZone tz = this.getTimeZone(this.getDefaultTimeZoneID());
      if (tz != null) {
         InternalServices.setTimeZoneOffset(tz.getRawOffset());
      }
   }

   private final String internalGetDefaultTimeZoneID() {
      PersistentObject po = RIMPersistentStore.getPersistentObject(2989539104253779367L);
      return (String)po.getContents();
   }

   private final void internalSetDefaultTimeZoneID(String timeZoneID) {
      PersistentObject po = RIMPersistentStore.getPersistentObject(2989539104253779367L);
      this._defaultTimeZoneID = timeZoneID;
      po.setContents(timeZoneID, 51, false);
      this.updateTimeZoneOffset();
      po.commit();
   }

   public final String getDefaultTimeZoneID() {
      String timeZoneID = this._defaultTimeZoneID;
      if (timeZoneID == null) {
         timeZoneID = DateTimeUtilities.GMT;
      }

      return timeZoneID;
   }

   public final synchronized void setDefaultTimeZone(String defaultTimeZoneID) {
      if (defaultTimeZoneID != null && this.getTimeZoneID(defaultTimeZoneID) != -1 && !defaultTimeZoneID.equals(this._defaultTimeZoneID)) {
         this._defaultTimeZoneID = this.getTimeZoneInfo(this.getTimeZoneID(defaultTimeZoneID)).getTimeZoneStringID();
         this.internalSetDefaultTimeZoneID(this._defaultTimeZoneID);
         TimeZone tz = this.getTimeZone(this._defaultTimeZoneID);
         if (tz != null) {
            InternalServices.setTimeZoneOffset(tz.getRawOffset());
         }

         RIMGlobalMessagePoster.postGlobalEvent(3596208183088439728L);
      }
   }

   public final int getTimeZoneIndex(String timeZoneName) {
      int result = -1;

      for (int i = 0; timeZoneName != null && i < this._indexMap.length; i++) {
         TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[this._indexMap[i]];
         if (timeZoneName.equalsIgnoreCase(tzdo.getTimeZoneStringID())) {
            result = i;
            break;
         }
      }

      if (result == -1 && (timeZoneName == null || !timeZoneName.equalsIgnoreCase(DateTimeUtilities.GMT))) {
         result = this.getTimeZoneIndex(DateTimeUtilities.GMT);
      }

      return result;
   }

   public final int getTimeZoneIndexByUID(int uid) {
      int result = -1;

      for (int i = 0; i < this._indexMap.length; i++) {
         TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[this._indexMap[i]];
         if (tzdo.getTimeZoneID() == uid) {
            result = i;
            break;
         }
      }

      if (result == -1) {
         result = this.getTimeZoneIndex(DateTimeUtilities.GMT);
      }

      return result;
   }

   public final synchronized TimeZoneDataObject getTimeZoneInfo(int tzid) {
      return (TimeZoneDataObject)this._timeZoneTable.get(tzid);
   }

   public final synchronized TimeZoneDataObject getTimeZoneByUID(int uid) {
      for (int i = 0; i < this._sortedTimeZoneDataObjectList.length; i++) {
         TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[i];
         if (tzdo.getUID() == uid) {
            return tzdo;
         }
      }

      return null;
   }

   public final synchronized String getTimeZoneIDFromSerialSyncID(int serialSyncID) {
      String result = null;
      TimeZoneDataObject tzdo = (TimeZoneDataObject)this._timeZoneTable.get(serialSyncID);
      if (tzdo != null) {
         result = tzdo.getTimeZoneStringID();
      }

      return result;
   }

   public final synchronized int getSerialSyncID(String timeZoneName) {
      int result = -1;

      for (int i = 0; timeZoneName != null && i < this._sortedTimeZoneDataObjectList.length; i++) {
         TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[i];
         if (timeZoneName.equalsIgnoreCase(tzdo.getTimeZoneStringID())) {
            return tzdo.getTimeZoneID();
         }
      }

      return result;
   }

   private final synchronized String getTimeZoneDescription(TimeZoneDataObject tzdo, int descriptionType) {
      String result = null;
      if (this._localizer != null) {
         Locale locale = Locale.getDefault();
         result = this._localizer.getResourceString(tzdo.getTimeZoneID(), descriptionType, locale);
      }

      if (result == null) {
         if (descriptionType == 2) {
            return tzdo.getTimeZoneStringID();
         }

         String[] tzNames = CommonResource.getBundle().getStringArray(descriptionType == 0 ? 10117 : 10116);
         if (tzdo.isBuiltInData()) {
            return tzNames[tzdo.getBuiltInIndex()];
         }

         if (descriptionType == 0) {
            result = tzdo.getDefaultShortDescription();
         } else {
            result = tzdo.getDefaultLongDescription();
         }

         if (result == null) {
            result = tzdo.getTimeZoneStringID();
         }
      }

      return result;
   }

   private final synchronized String getTimeZoneDescriptionByIndex(int index, int descriptionType) {
      TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[this._indexMap[index]];
      return this.getTimeZoneDescription(tzdo, descriptionType);
   }

   public final synchronized String getTimeZoneLongDescription(String id) {
      return this.getTimeZoneDescriptionByIndex(this.getTimeZoneIndex(id), 1);
   }

   public final synchronized String getTimeZoneShortDescription(String id) {
      return this.getTimeZoneDescriptionByIndex(this.getTimeZoneIndex(id), 0);
   }

   private final synchronized String getTimeZoneDescriptionByID(int tzid, int descriptionType) {
      TimeZoneDataObject tzdo = (TimeZoneDataObject)this._timeZoneTable.get(tzid);
      return this.getTimeZoneDescription(tzdo, descriptionType);
   }

   public final synchronized String getTimeZoneLongDescription(int tzid) {
      return this.getTimeZoneDescriptionByID(tzid, 0);
   }

   public final synchronized String getLocalizedTimeZoneShortDescription(int tzid) {
      return this.getTimeZoneDescriptionByID(tzid, 0);
   }

   public final synchronized String getDefaultTimeZoneDescription(int tzid) {
      return this.getTimeZoneDescriptionByID(tzid, 2);
   }

   private final TimeZoneImpl getZone(int tzid) {
      TimeZoneImpl result = null;
      TimeZoneDataObject tzdo = (TimeZoneDataObject)this._timeZoneTable.get(tzid);
      if (tzdo != null) {
         result = new TimeZoneImpl(
            tzdo.getTimeZoneStringID(),
            tzdo.getGMTOffset(),
            tzdo.getDSTAmount(),
            tzdo.getDSTStartMode(),
            tzdo.getDSTStartMonth(),
            tzdo.getDSTStartDayOfWeek(),
            tzdo.getDSTStartDay(),
            tzdo.getDSTStartTime(),
            tzdo.getDSTEndMode(),
            tzdo.getDSTEndMonth(),
            tzdo.getDSTEndDayOfWeek(),
            tzdo.getDSTEndDay(),
            tzdo.getDSTEndTime(),
            tzid,
            this._automaticallyAdjustClockForDSTTime
         );
      }

      return result;
   }

   private final CustomTimeZoneImpl getCustomZone(String ID) {
      int hours = 0;
      int minutes = 0;
      boolean isValidID = false;
      if (ID == null) {
         return null;
      }

      if (!ID.startsWith(DateTimeUtilities.GMT)) {
         return null;
      }

      ID = ID.substring(3);
      char sign;
      if (ID.startsWith("+")) {
         sign = '+';
         ID = ID.substring(1);
      } else {
         if (!ID.startsWith("-")) {
            return null;
         }

         sign = '-';
         ID = ID.substring(1);
      }

      int colonIndex = ID.indexOf(58);
      if (colonIndex != -1) {
         String minuteString = ID.substring(colonIndex + 1);
         if (minuteString.length() != 2) {
            return null;
         }

         try {
            hours = Integer.parseInt(ID.substring(0, colonIndex));
            minutes = Integer.parseInt(minuteString);
            isValidID = true;
         } catch (NumberFormatException nfe) {
            return null;
         }
      } else {
         int numDigits = ID.length();

         try {
            switch (numDigits) {
               case 0:
                  isValidID = false;
                  break;
               case 1:
               case 2:
               default:
                  hours = Integer.parseInt(ID);
                  minutes = 0;
                  isValidID = true;
                  break;
               case 3:
                  hours = Integer.parseInt(ID.substring(0, 1));
                  minutes = Integer.parseInt(ID.substring(1));
                  isValidID = true;
                  break;
               case 4:
                  hours = Integer.parseInt(ID.substring(0, 2));
                  minutes = Integer.parseInt(ID.substring(2));
                  isValidID = true;
            }
         } catch (NumberFormatException nfe) {
            return null;
         }
      }

      if (!isValidID) {
         return null;
      } else {
         return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59 ? new CustomTimeZoneImpl(sign, hours, minutes) : null;
      }
   }

   public final synchronized void deleteTimeZone(int tzid) {
      for (int i = 0; i < this._sortedTimeZoneDataObjectList.length; i++) {
         TimeZoneDataObject tzdo = this._sortedTimeZoneDataObjectList[i];
         if (tzdo.getTimeZoneID() == tzid) {
            if (tzdo.getTimeZoneStringID().equalsIgnoreCase(DateTimeUtilities.GMT)) {
               return;
            }

            this._sortedTimeZoneDataObjectList[i] = this._sortedTimeZoneDataObjectList[this._sortedTimeZoneDataObjectList.length - 1];
            Array.resize(this._sortedTimeZoneDataObjectList, this._sortedTimeZoneDataObjectList.length - 1);
            this._timeZoneTable.remove(tzid);
            Arrays.sort(this._sortedTimeZoneDataObjectList, TimeZoneDataObject.getComparator());
            this._indexMap = new int[this._sortedTimeZoneDataObjectList.length];
            int count = 0;

            for (int j = 0; j < this._sortedTimeZoneDataObjectList.length; j++) {
               if (!this._sortedTimeZoneDataObjectList[j].isHidden()) {
                  this._indexMap[count] = j;
                  count++;
               }
            }

            Array.resize(this._indexMap, count);
            if (this._defaultTimeZoneID != null && this._defaultTimeZoneID.equalsIgnoreCase(tzdo.getTimeZoneStringID())) {
               this._tzObject = null;
               this.internalSetDefaultTimeZoneID(null);
            }

            this._persistableTimeZoneData.commit();
            return;
         }
      }
   }

   public final synchronized void addTimeZone(TimeZoneDataObject tzdo) {
      this.addTimeZone(
         tzdo.getUID(),
         tzdo.getTimeZoneID(),
         tzdo.getTimeZoneStringID(),
         tzdo.getDefaultLongDescription(),
         tzdo.getDefaultShortDescription(),
         tzdo.getMappedTZID(),
         tzdo.isHidden(),
         tzdo.getGMTOffset(),
         tzdo.getDSTAmount(),
         tzdo.getDSTStartMode(),
         tzdo.getDSTStartMonth(),
         tzdo.getDSTStartDayOfWeek(),
         tzdo.getDSTStartDay(),
         tzdo.getDSTStartTime(),
         tzdo.getDSTEndMode(),
         tzdo.getDSTEndMonth(),
         tzdo.getDSTEndDayOfWeek(),
         tzdo.getDSTEndDay(),
         tzdo.getDSTEndTime()
      );
   }

   public final synchronized void addTimeZone(
      int uid,
      int tzid,
      String stringID,
      String longName,
      String shortName,
      int mappedTZID,
      boolean hidden,
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
      int dstEndTime
   ) {
      int builtIndex = this.getTimeZoneBuiltInIndex(tzid);
      boolean add = true;
      TimeZoneDataObject tzdo = null;
      if (this._timeZoneTable.containsKey(tzid)) {
         tzdo = (TimeZoneDataObject)this._timeZoneTable.get(tzid);
         builtIndex = tzdo.getBuiltInIndex();
         add = false;
      }

      if (stringID.equalsIgnoreCase(DateTimeUtilities.GMT)) {
         hidden = false;
      }

      tzdo = new TimeZoneDataObject(
         uid,
         tzid,
         stringID,
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
         longName,
         shortName,
         mappedTZID,
         hidden
      );
      tzdo.setBuiltInIndex(builtIndex);
      int currentTimeZoneID = this.getSerialSyncID(this.getDefaultTimeZoneID());
      boolean currentTimeZoneUpdated = tzid == currentTimeZoneID;
      this._timeZoneTable.put(tzid, tzdo);
      if (add) {
         Array.resize(this._sortedTimeZoneDataObjectList, this._sortedTimeZoneDataObjectList.length + 1);
         this._sortedTimeZoneDataObjectList[this._sortedTimeZoneDataObjectList.length - 1] = tzdo;
      } else {
         for (int i = 0; i < this._sortedTimeZoneDataObjectList.length; i++) {
            if (this._sortedTimeZoneDataObjectList[i].getTimeZoneID() == tzid) {
               this._sortedTimeZoneDataObjectList[i] = tzdo;
               break;
            }
         }
      }

      Arrays.sort(this._sortedTimeZoneDataObjectList, TimeZoneDataObject.getComparator());
      this._indexMap = new int[this._sortedTimeZoneDataObjectList.length];
      int count = 0;

      for (int i = 0; i < this._sortedTimeZoneDataObjectList.length; i++) {
         if (!this._sortedTimeZoneDataObjectList[i].isHidden()) {
            this._indexMap[count] = i;
            count++;
         }
      }

      Array.resize(this._indexMap, count);
      this._persistableTimeZoneData.commit();
      if (currentTimeZoneUpdated) {
         this._tzObject = null;
         this.setDefaultTimeZone(tzdo.getTimeZoneStringID());
      }
   }

   public final int getTimeZoneCount() {
      return this._sortedTimeZoneDataObjectList.length;
   }

   public static final synchronized TimeService getTimeService() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         if (ar == null) {
            return null;
         }

         _instance = (TimeService)ar.getOrWaitFor(2569363511892233325L);
         if (_instance == null) {
            _instance = new TimeService();
            ar.put(2569363511892233325L, _instance);
         }
      }

      return _instance;
   }
}
