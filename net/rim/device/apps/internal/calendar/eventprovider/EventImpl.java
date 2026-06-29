package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewerProvider;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.controller.DurationParts;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfoProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MultiServiceEvent;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderModel;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.apps.api.sync.Checksumable;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.sync.Reconcilable;
import net.rim.device.apps.api.utility.framework.RecurCalc;
import net.rim.device.apps.api.utility.framework.RecurUtil;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.commonmodels.pim.RecurImpl;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public final class EventImpl
   extends EventBase
   implements Event,
   RIMModel,
   SyncObject,
   Copyable,
   Checksumable,
   Reconcilable,
   Duration,
   DurationParts,
   UniqueIDProvider,
   KeyProvider,
   HotKeyProvider,
   CalendarEventViewerProvider,
   VerbProvider,
   EncryptableProvider,
   ReadableList,
   MatchProvider,
   ActionProvider,
   MultiServiceEvent,
   AccessibleContext,
   MeetingInfoProvider {
   private int _localID;
   private long _startDateOffset;
   private int _duration;
   private RecurImpl _recur;
   private Object _subjectEncoding;
   private Object _locationEncoding;
   private Object _notesEncoding;
   private long _relatedLUID;
   private long _relatedTime;
   private int _timeZoneID;
   private Object[] _externalObjects;
   private long[] _externalKeys;
   private int _packedInfo;
   private int _syncID;
   private byte _sensitivity;
   private CalendarKey _calendarKey;
   private String _iCalID;
   private int _accessibleStateSet = 1;
   private int _numOfAccessibleItems = 4;
   private static final long EVENT_SEQUENCE_ID = 5328208744741124224L;
   private static int _persistentId = PersistentInteger.getId(5328208744741124224L, -1);
   static final long CONTROLLER_ID = 7604121982064887442L;
   static RecurCalc _recurCalc = (RecurCalc)(new Object());
   private static Recur$Modifier _modifier = (Recur$Modifier)(new Object());
   private static long[] _exclusions = new long[0];
   private static long[] _inclusions = new long[0];
   private static Object _calLock;
   private static Calendar _gmtCal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
   private static Calendar _currCal = Calendar.getInstance();
   private static ContextObjectWR _reconcileContextWR = (ContextObjectWR)(new Object());
   private static final int PACKEDINFO_FREE_BUSY = 255;
   private static final int PACKEDINFO_ALL_DAY = 256;
   private static int[] _hints = new int[0];
   private static final long SORT_KEY_START = -7347526267900023482L;
   private static final long SORT_KEY_END = -104331952420113366L;
   private static final long SORT_KEY_DUR = -1863931878973078146L;
   private static Recur$Handle _sharedHandle = (Recur$Handle)(new Object());

   protected final void accessibleEventOccurred(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventDispatcher.dispatchAccessibleEvent(event, oldValue, newValue, context);
   }

   protected final void removeAccessibleState(int state) {
      this._accessibleStateSet &= ~state;
   }

   protected final void addAccessibleState(int state) {
      if (this.isAccessibleStateSet(1)) {
         this.removeAccessibleState(1);
      }

      this._accessibleStateSet |= state;
   }

   public final void setReminderDelta(long delta) {
      ReminderModel rm = this.getReminderData();
      if (rm != null) {
         rm.setTime(delta);
      }
   }

   protected final String getBriefSummary() {
      String result = "";
      String subject = this.getSubject();
      String location = this.getLocation();
      int locationLength = location == null ? 0 : location.length();
      int subjectLength = subject == null ? 0 : subject.length();
      if (locationLength + subjectLength > 0) {
         StringBuffer b = (StringBuffer)(new Object());
         b.setLength(0);
         if (subjectLength > 0) {
            b.append(subject);
            b.append(' ');
         }

         if (locationLength > 0) {
            b.append('(');
            if (location.length() < 100) {
               b.append(location);
            } else {
               b.append(location.substring(0, 100));
               b.append('…');
            }

            b.append(')');
         }

         result = b.toString();
      }

      return result;
   }

   public final byte getPropertiesForEvent() {
      byte propsToReturn = 0;
      if (this.getAllDayFlag()) {
         propsToReturn = (byte)(propsToReturn | 1);
      }

      return propsToReturn;
   }

   protected final void appendVerboseWhenStringForNonRecurrence(StringBuffer b) {
      ResourceBundle rb = EventBase._rb;
      Calendar cal = Calendar.getInstance();
      TimeZone tz = cal.getTimeZone();
      DateFormat timeFormat = DateFormat.getInstance(6);
      long start = this.getStart(tz);
      ((CalendarExtensions)cal).setTimeLong(start);
      EventBase._mediumDateFormat.format(cal, b, null);
      b.append(' ');
      timeFormat.format(cal, b, null);
      int year = cal.get(1);
      int month = cal.get(2);
      int day = cal.get(5);
      long end = start + this.getDuration(tz);
      ((CalendarExtensions)cal).setTimeLong(end);
      if (year == cal.get(1) && month == cal.get(2) && day == cal.get(5)) {
         b.append('-');
      } else {
         b.append(rb.getString(605));
         EventBase._mediumDateFormat.format(cal, b, null);
         b.append(' ');
      }

      timeFormat.format(cal, b, null);
      EventBase.appendTimeZoneDescription(tz, b);
   }

   protected final String getVerboseSummary() {
      String str = null;
      ResourceBundle rb = EventBase._rb;
      StringBuffer b = (StringBuffer)(new Object());
      b.setLength(0);
      b.append(rb.getString(600));
      if (this.isRecurring()) {
         this.appendVerboseWhenStringForRecurrence(b);
      } else {
         this.appendVerboseWhenStringForNonRecurrence(b);
      }

      str = this.getLocation();
      if (str != null && str.length() > 0) {
         b.append(rb.getString(601));
         b.append(str);
      }

      str = this.getNotes();
      if (str != null && str.length() > 0) {
         b.append(rb.getString(602));
         b.append(str);
      }

      return b.toString();
   }

   protected final void appendVerboseWhenStringForRecurrence(StringBuffer b) {
      ResourceBundle rb = EventBase._rb;
      Calendar cal = Calendar.getInstance();
      TimeZone tz = TimeZone.getTimeZone(this.getTimeZoneID());
      DateFormat timeFormat = DateFormat.getInstance(6);
      long start = this.getStart(tz);
      ((CalendarExtensions)cal).setTimeLong(start);
      StringBuffer recurDesc = (StringBuffer)(new Object());
      Recur recur = this.getReadOnlyRecurrence();
      boolean allDay = this.getAllDayFlag();
      RecurUtil.getDesc(recur, allDay, recurDesc, tz);
      b.append(recurDesc.toString());
      b.append(rb.getString(608));
      b.append(EventBase._mediumDateFormat.format(cal));
      if (!allDay) {
         b.append(rb.getString(606));
         timeFormat.format(cal, b, null);
         long duration = this.getDurationFromHandle(start, tz);
         ((CalendarExtensions)cal).setTimeLong(start + duration);
         if (duration < 86400000) {
            b.append('-');
            timeFormat.format(cal, b, null);
         } else {
            b.append(rb.getString(607));
            long hours = duration / 3600000;
            duration -= hours * 3600000;
            long minutes = duration / 60000;
            b.append(hours);
            b.append(rb.getString(609));
            if (minutes > 0) {
               b.append(minutes);
               b.append(rb.getString(minutes == 1 ? 611 : 610));
            }
         }
      }

      EventBase.appendTimeZoneDescription(tz, b);
   }

   protected final String getTimeSummary(long instanceStart) {
      String str = null;
      Calendar cal = Calendar.getInstance();
      TimeZone tz = cal.getTimeZone();
      DateFormat timeFormat = DateFormat.getInstance(7);
      if (this.getAllDayFlag()) {
         return null;
      }

      StringBuffer b = (StringBuffer)(new Object());
      b.setLength(0);
      long start = this.getStart(tz);
      long end;
      if (this.isRecurring()) {
         start = instanceStart;
         end = start + this.getDurationFromHandle(start, tz);
      } else {
         end = start + this.getDuration(tz);
      }

      ((CalendarExtensions)cal).setTimeLong(start);
      timeFormat.format(cal, b, null);
      if (start != end) {
         b.append('-');
         ((CalendarExtensions)cal).setTimeLong(end);
         timeFormat.format(cal, b, null);
      }

      return b.toString();
   }

   final Object getCalLock() {
      CalDB calDB = CalendarServiceManager.getInstance().findCalendarDatabase(this);
      if (_calLock == null) {
         _calLock = calDB.getLockObject();
      }

      return _calLock;
   }

   @Override
   public final boolean contains(Object obj) {
      synchronized (this.getCalLock()) {
         if (this._externalObjects != null && obj != null) {
            for (Object objInList : this._externalObjects) {
               if (objInList != null && objInList.equals(obj)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   @Override
   public final void setUID(int newLocalID) {
      this._localID = newLocalID;
   }

   @Override
   public final int getUID() {
      return this._localID;
   }

   @Override
   public final void setStoreID(int homeInformationStore) {
   }

   @Override
   public final int getStoreID() {
      return this._localID > 0 ? 267390960 : -1;
   }

   @Override
   public final long getLUID() {
      return EventUtilities.makeLUID(this._localID);
   }

   @Override
   public final void setAllDayFlag(boolean allDayFlag) {
      if (allDayFlag) {
         this._packedInfo |= 256;
      } else {
         this._packedInfo &= -257;
      }
   }

   @Override
   public final boolean getAllDayFlag() {
      return (this._packedInfo & 256) != 0;
   }

   @Override
   public final long getStartDate(TimeZone tz) {
      return this.getStart(tz);
   }

   @Override
   public final void setStartDate(long dateOfStart, TimeZone tz) {
      if (this.getAllDayFlag() && tz != null) {
         synchronized (_currCal) {
            synchronized (_gmtCal) {
               Calendar currCal = _currCal;
               Calendar gmtCal = _gmtCal;
               currCal.setTimeZone(tz);
               ((CalendarExtensions)currCal).setTimeLong(dateOfStart);
               gmtCal.set(0, 1);
               currCal.set(0, 1);
               gmtCal.set(1, currCal.get(1));
               gmtCal.set(2, currCal.get(2));
               gmtCal.set(5, currCal.get(5));
               gmtCal.set(11, currCal.get(11));
               gmtCal.set(12, currCal.get(12));
               gmtCal.set(13, currCal.get(13));
               gmtCal.set(14, currCal.get(14));
               this._startDateOffset = ((CalendarExtensions)gmtCal).getTimeLong();
            }
         }
      } else {
         synchronized (_currCal) {
            Calendar currCal = _currCal;
            ((CalendarExtensions)currCal).setTimeLong(dateOfStart);
            currCal.set(0, 1);
            this._startDateOffset = ((CalendarExtensions)currCal).getTimeLong();
         }
      }
   }

   @Override
   public final void setInstanceDuration(long duration) {
      duration /= 60000;
      if (duration <= Integer.MAX_VALUE && duration >= Integer.MIN_VALUE) {
         this._duration = (int)duration;
      } else {
         throw new Object();
      }
   }

   @Override
   public final long getInstanceDuration() {
      return (long)this._duration * 60000;
   }

   @Override
   public final boolean getHandleBeforeTime(Recur$Handle handle, long targetDateTime, TimeZone tz) {
      return this.getHandleTimeWorker(handle, targetDateTime, tz, false);
   }

   @Override
   public final boolean getHandleAfterTime(Recur$Handle handle, long targetDateTime, TimeZone tz) {
      return this.getHandleTimeWorker(handle, targetDateTime, tz, true);
   }

   @Override
   public final boolean getHandleBefore(Recur$Handle handle, long lastHandle) {
      if (this._recur == null) {
         return false;
      }

      TimeZone tzRecur;
      if (this.getAllDayFlag()) {
         tzRecur = _gmtCal.getTimeZone();
      } else {
         tzRecur = TimeZone.getTimeZone(this.getTimeZoneID());
         if (tzRecur == null) {
            tzRecur = TimeZone.getDefault();
         }
      }

      return _recurCalc.getFromKnownInstance(lastHandle, false, handle, this._startDateOffset, this.getInstanceDuration(), this._recur, tzRecur);
   }

   @Override
   public final boolean getHandleAfter(Recur$Handle handle, long lastHandle) {
      if (this._recur == null) {
         return false;
      }

      TimeZone tzRecur;
      if (this.getAllDayFlag()) {
         tzRecur = _gmtCal.getTimeZone();
      } else {
         tzRecur = TimeZone.getTimeZone(this.getTimeZoneID());
         if (tzRecur == null) {
            tzRecur = TimeZone.getDefault();
         }
      }

      return _recurCalc.getFromKnownInstance(lastHandle, true, handle, this._startDateOffset, this.getInstanceDuration(), this._recur, tzRecur);
   }

   @Override
   public final long getStartFromHandle(long handle, TimeZone tz) {
      long timeToReturn;
      if (!this.getAllDayFlag()) {
         timeToReturn = handle;
      } else {
         synchronized (_currCal) {
            synchronized (_gmtCal) {
               Calendar currCal = _currCal;
               Calendar gmtCal = _gmtCal;
               currCal.setTimeZone(tz);
               ((CalendarExtensions)gmtCal).setTimeLong(handle);
               currCal.set(1, gmtCal.get(1));
               currCal.set(2, gmtCal.get(2));
               currCal.set(5, gmtCal.get(5));
               currCal.set(11, gmtCal.get(11));
               currCal.set(12, gmtCal.get(12));
               currCal.set(13, gmtCal.get(13));
               currCal.set(14, gmtCal.get(14));
               timeToReturn = ((CalendarExtensions)currCal).getTimeLong();
            }
         }
      }

      return timeToReturn;
   }

   @Override
   public final long getDurationFromHandle(long handle, TimeZone tz) {
      long timeToReturn;
      if (!this.getAllDayFlag()) {
         timeToReturn = this.getInstanceDuration();
      } else {
         synchronized (_currCal) {
            synchronized (_gmtCal) {
               Calendar currCal = _currCal;
               Calendar gmtCal = _gmtCal;
               currCal.setTimeZone(tz);
               ((CalendarExtensions)gmtCal).setTimeLong(handle + this.getInstanceDuration());
               currCal.set(1, gmtCal.get(1));
               currCal.set(2, gmtCal.get(2));
               currCal.set(5, gmtCal.get(5));
               currCal.set(11, gmtCal.get(11));
               currCal.set(12, gmtCal.get(12));
               currCal.set(13, gmtCal.get(13));
               currCal.set(14, gmtCal.get(14));
               timeToReturn = ((CalendarExtensions)currCal).getTimeLong() - this.getStartFromHandle(handle, tz);
            }
         }
      }

      return timeToReturn;
   }

   @Override
   public final boolean conflictsWithNextInSeries(boolean forward, boolean adjustStartTime, long exclusionTime, long timeToStartSearching, TimeZone tz) {
      boolean result = false;
      int offset = 0;
      int dayinmillis = 0;
      if (this.getAllDayFlag()) {
         synchronized (_currCal) {
            _currCal.setTimeZone(tz);
            ((CalendarExtensions)_currCal).setTimeLong(exclusionTime);
            dayinmillis = 1000 * (60 * (_currCal.get(11) * 60 + _currCal.get(12)) + _currCal.get(13)) + _currCal.get(14);
            offset = tz.getOffset(1, _currCal.get(1), _currCal.get(2), _currCal.get(5), _currCal.get(7), dayinmillis);
         }
      }

      Recur$Handle foundOccurrence = (Recur$Handle)(new Object());
      if (adjustStartTime) {
         timeToStartSearching -= offset;
      }

      if (forward) {
         if (this.getHandleAfterTime(foundOccurrence, timeToStartSearching, tz)) {
            result = exclusionTime > foundOccurrence._handle - offset;
         }
      } else if (this.getHandleBeforeTime(foundOccurrence, timeToStartSearching, tz)) {
         result = exclusionTime < foundOccurrence._handle - offset + this.getInstanceDuration();
      }

      return result;
   }

   @Override
   public final void setSubject(String subject) {
      if (subject == null) {
         subject = "";
      }

      this._subjectEncoding = subject;
   }

   @Override
   public final String getSubject() {
      try {
         return PersistentContent.decodeString(this._subjectEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final void setLocation(String location) {
      if (location == null) {
         location = "";
      }

      this._locationEncoding = location;
   }

   @Override
   public final String getLocation() {
      try {
         return PersistentContent.decodeString(this._locationEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final boolean isRecurring() {
      return this._recur != null && this._recur.getRecurType() != 0;
   }

   @Override
   public final Recur getRecurrenceCopy() {
      if (!this.isRecurring()) {
         RecurImpl recurToReturn = (RecurImpl)(new Object());
         return recurToReturn;
      } else {
         RecurImpl recurToReturn = (RecurImpl)this._recur.clone(null);
         return recurToReturn;
      }
   }

   @Override
   public final Recur getReadOnlyRecurrence() {
      return this._recur;
   }

   @Override
   public final void setRecurrence(Recur recur) {
      this._recur = (RecurImpl)recur;
   }

   @Override
   public final void setNotes(String notes) {
      if (notes == null) {
         notes = "";
      }

      this._notesEncoding = notes;
   }

   @Override
   public final String getNotes() {
      if (this._notesEncoding == null) {
         return "";
      }

      try {
         return (String)PersistentContent.decode(this._notesEncoding);
      } finally {
         ;
      }
   }

   @Override
   public final void setICalID(String id) {
      this._iCalID = id;
   }

   @Override
   public final String getICalID() {
      return this._iCalID;
   }

   @Override
   public final boolean isMeeting() {
      MeetingInfo meetingInfo = (MeetingInfo)this.get(7228817433593908387L);
      return meetingInfo != null && meetingInfo.getAttendeeCount() > 0;
   }

   @Override
   public final MeetingInfo getMeetingInfo() {
      return (MeetingInfo)this.get(7228817433593908387L);
   }

   @Override
   public final void setFreeBusy(byte freeBusy) {
      switch (freeBusy) {
         case -1:
         default:
            freeBusy = 2;
         case 0:
         case 1:
         case 2:
         case 3:
            this._packedInfo = this._packedInfo & -256 | freeBusy & 255;
      }
   }

   @Override
   public final byte getFreeBusy() {
      return (byte)(this._packedInfo & 0xFF);
   }

   @Override
   public final void setSensitivity(byte sensitivity) {
      switch (sensitivity) {
         case -1:
         default:
            sensitivity = 0;
         case 0:
         case 1:
         case 2:
         case 3:
            this._sensitivity = sensitivity;
      }
   }

   @Override
   public final byte getSensitivity() {
      return this._sensitivity;
   }

   @Override
   public final void setRelatedLUID(long relatedLUID) {
      this._relatedLUID = -1 & relatedLUID;
   }

   @Override
   public final long getRelatedLUID() {
      if (this._relatedLUID == 0) {
         return 0;
      }

      long LUIDToReturn = 0;
      int UID = (int)(-1 & this._relatedLUID);
      return UID > 0 ? UIDGenerator.makeLUID(267390960, UID) : UIDGenerator.makeLUID(-1, UID);
   }

   @Override
   public final void setRelatedTime(long relatedTime) {
      this._relatedTime = relatedTime;
   }

   @Override
   public final long getRelatedTime() {
      return this._relatedTime;
   }

   @Override
   public final void put(long key, Object o) {
      synchronized (this.getCalLock()) {
         this.remove(key);
         int max;
         if (this._externalObjects == null) {
            this._externalObjects = new Object[1];
            this._externalKeys = new long[1];
            max = 0;
         } else {
            max = this._externalObjects.length;
            Array.resize(this._externalObjects, max + 1);
            Array.resize(this._externalKeys, max + 1);
         }

         this._externalKeys[max] = key;
         this._externalObjects[max] = o;
      }
   }

   @Override
   public final void remove(long key) {
      synchronized (this.getCalLock()) {
         if (this._externalObjects != null) {
            Object[] externalObjects = this._externalObjects;
            long[] externalKeys = this._externalKeys;
            int max = externalObjects.length;

            for (int i = 0; i < max; i++) {
               if (externalKeys[i] == key) {
                  System.arraycopy(externalKeys, i + 1, externalKeys, i, max - i - 1);
                  System.arraycopy(externalObjects, i + 1, externalObjects, i, max - i - 1);
                  Array.resize(externalKeys, max - 1);
                  Array.resize(externalObjects, max - 1);
                  break;
               }
            }

            if (externalObjects.length <= 0) {
               this._externalObjects = null;
               this._externalKeys = null;
            }
         }
      }
   }

   @Override
   public final void removeAll() {
      synchronized (this.getCalLock()) {
         this._externalObjects = null;
         this._externalKeys = null;
      }
   }

   @Override
   public final int size() {
      synchronized (this.getCalLock()) {
         return this._externalObjects == null ? 0 : this._externalObjects.length;
      }
   }

   @Override
   public final Object get(long uid) {
      if (this._externalObjects == null) {
         return null;
      }

      Object[] externalObjects = this._externalObjects;
      long[] externalKeys = this._externalKeys;
      int max = externalObjects.length;

      for (int i = 0; i < max; i++) {
         if (externalKeys[i] == uid) {
            return externalObjects[i];
         }
      }

      return null;
   }

   @Override
   public final long getKey(Object obj) {
      if (this._externalObjects != null && obj != null) {
         Object[] externalObjects = this._externalObjects;
         long[] externalKeys = this._externalKeys;
         int max = externalObjects.length;

         for (int i = 0; i < max; i++) {
            Object objInList = externalObjects[i];
            if (objInList != null && objInList.equals(obj)) {
               return externalKeys[i];
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   @Override
   public final boolean contains(long uid) {
      Object o = this.get(uid);
      return o != null;
   }

   @Override
   public final Enumeration getElements() {
      synchronized (this.getCalLock()) {
         if (this._externalObjects == null) {
            return (Enumeration)(new Object());
         }

         Object[] arrayToReturn = new Object[this._externalObjects.length];
         System.arraycopy(this._externalObjects, 0, arrayToReturn, 0, arrayToReturn.length);
         return (Enumeration)(new Object(arrayToReturn));
      }
   }

   @Override
   public final int getElements(Object[] elements) {
      synchronized (this.getCalLock()) {
         if (this._externalObjects == null) {
            return 0;
         }

         int max = this._externalObjects.length;
         Array.resize(elements, max);
         System.arraycopy(this._externalObjects, 0, elements, 0, max);
         return max;
      }
   }

   @Override
   public final Object copy() {
      synchronized (this.getCalLock()) {
         EventImpl that = new EventImpl(this._calendarKey);
         that.setUID(UIDGenerator.getUID(0));
         that._packedInfo = this._packedInfo;
         that._startDateOffset = this._startDateOffset;
         that._duration = this._duration;
         that._timeZoneID = this._timeZoneID;
         that._recur = null;
         that._sensitivity = this._sensitivity;
         that._subjectEncoding = this._subjectEncoding;
         that._locationEncoding = this._locationEncoding;
         that._notesEncoding = this._notesEncoding;
         that._iCalID = this._iCalID;
         Object[] externalObjects = this._externalObjects;
         long[] externalKeys = this._externalKeys;
         int max = externalObjects.length;

         for (int i = 0; i < max; i++) {
            Object o = externalObjects[i];
            if (o instanceof Object) {
               Copyable copyable = (Copyable)o;
               that.put(externalKeys[i], copyable.copy());
            }
         }

         return that;
      }
   }

   @Override
   public final long getChecksum(Object context) {
      long result = 0;
      ContextObject co = null;
      if (context instanceof Object) {
         co = (ContextObject)context;
      }

      OTACalendarSyncDataManager otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
      OTASyncData syncData = otaSyncDataManager.get(this);
      if (syncData != null) {
         byte[] hashData = syncData.getChecksum();
         if (hashData == null) {
            return -1;
         }

         int start = 0;
         int end = hashData.length;
         if (co != null && co.getFlag(60)) {
            end = 4;
         } else if (co != null && co.getPrivateFlag(-2183283945546558784L, 1)) {
            end = 6;
         } else if (co != null && co.getPrivateFlag(-2183283945546558784L, 2)) {
            start = 6;
         } else {
            result = CRC32.update(0, hashData, 0, 6);
            result |= CRC32.update((int)result, hashData, 6, 4) << 32;
         }

         for (int i = start; i < end; i++) {
            long temp = (char)hashData[i] & 0xFF;
            result |= temp << (i - start) * 8;
         }
      }

      return result;
   }

   @Override
   public final void reconcile(Object objectToReconcile, Object context) {
      Event eventToReconcile = null;
      if (objectToReconcile instanceof Object) {
         Event var13 = objectToReconcile;
         ContextObject reconcileContext = _reconcileContextWR.getContextObject();
         ContextObject.put(reconcileContext, -442904859142728844L, this);
         ContextObject.put(reconcileContext, 4930575420328309875L, var13);
         synchronized (this.getCalLock()) {
            if (this.getICalID() == null) {
               this.setICalID(((Event)var13).getICalID());
            }

            Object[] externalObjects = this._externalObjects;
            long[] externalKeys = this._externalKeys;
            int max = externalObjects.length;

            for (int i = 0; i < max; i++) {
               Object o = externalObjects[i];
               if (o instanceof Object) {
                  ((Reconcilable)o).reconcile(((ReadableLongMap)var13).get(externalKeys[i]), reconcileContext);
               }
            }
         }
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      long eventPartInstance = 0;
      boolean reminder = false;
      if (context != null) {
         Long reminderTime = (Long)((LongHashtable)context).get(2006691216517637157L);
         if (reminderTime != null) {
            ReminderModel rm = this.getReminderData();
            long offset = 0;
            if (rm != null) {
               offset = rm.getTime();
            }

            eventPartInstance = reminderTime + offset;
         }

         if (((ContextObject)context).getFlag(36)) {
            reminder = true;
         }
      }

      EditEventVerb editVerb;
      if (reminder) {
         editVerb = new EditEventVerb(this, null, 357, 598288);
      } else {
         editVerb = new EditEventVerb(this);
      }

      if (eventPartInstance != 0) {
         editVerb.setPartInstance(eventPartInstance);
      }

      return this.getVerbs(context, verbs, new DeleteEventVerb(this), editVerb);
   }

   @Override
   public final Object invokeHotkey(Object context, int hotkeyID) {
      if (hotkeyID != 127 && Keypad.getAltedChar((char)hotkeyID) != 127) {
         return null;
      }

      Verb[] defaultVerbs = new Object[2];
      DeleteEventVerb deleteVerb = new DeleteEventVerb(this);
      defaultVerbs[1] = deleteVerb;
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DeleteEventVerb$DeleteEventVerbListener listener = (DeleteEventVerb$DeleteEventVerbListener)ar.waitFor(8503322669810003080L);
      deleteVerb.setDeleteEventVerbListener(listener);
      this.checkForVerbOverrides(context, defaultVerbs);
      return defaultVerbs[1].invoke(null);
   }

   @Override
   public final long getLUID(Object context) {
      return this.getLUID();
   }

   @Override
   public final boolean hasParts() {
      return this.isRecurring();
   }

   @Override
   public final Object getNextFromTime(long targetDateTime, TimeZone tz) {
      EventPart ep = null;
      Recur$Handle sharedHandle = _sharedHandle;
      synchronized (sharedHandle) {
         boolean result = this.getHandleAfterTime(sharedHandle, targetDateTime, tz);
         if (!result) {
            return ep;
         }

         ep = new EventPart();
         ep._event = this;
         ep._handle = sharedHandle._handle;
         return ep;
      }
   }

   @Override
   public final Object getPrevFromTime(long targetDateTime, TimeZone tz) {
      EventPart ep = null;
      Recur$Handle sharedHandle = _sharedHandle;
      synchronized (sharedHandle) {
         boolean result = this.getHandleBeforeTime(sharedHandle, targetDateTime, tz);
         if (!result) {
            return ep;
         }

         ep = new EventPart();
         ep._event = this;
         ep._handle = sharedHandle._handle;
         return ep;
      }
   }

   @Override
   public final Object getNext(Object last) {
      EventPart ep = null;
      Recur$Handle sharedHandle = _sharedHandle;
      synchronized (sharedHandle) {
         boolean result = this.getHandleAfter(sharedHandle, ((EventPart)last)._handle);
         if (!result) {
            return ep;
         }

         ep = new EventPart();
         ep._event = this;
         ep._handle = sharedHandle._handle;
         return ep;
      }
   }

   @Override
   public final Object getPrev(Object last) {
      EventPart ep = null;
      Recur$Handle sharedHandle = _sharedHandle;
      synchronized (sharedHandle) {
         boolean result = this.getHandleBefore(sharedHandle, ((EventPart)last)._handle);
         if (!result) {
            return ep;
         }

         ep = new EventPart();
         ep._event = this;
         ep._handle = sharedHandle._handle;
         return ep;
      }
   }

   @Override
   public final int compare(Object part1, Object part2) {
      long handle1 = ((EventPart)part1)._handle;
      long handle2 = ((EventPart)part2)._handle;
      if (handle1 < handle2) {
         return -1;
      } else {
         return handle1 > handle2 ? 1 : 0;
      }
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyNumber) {
      return 0;
   }

   @Override
   public final long getTrueStartDate() {
      long start = this.getStart(null);
      if (this._recur != null) {
         synchronized (_inclusions) {
            this._recur.getInclusions(_inclusions);
            if (_inclusions != null && _inclusions.length > 0 && _inclusions[0] < start) {
               start = _inclusions[0];
            }

            Array.resize(_inclusions, 0);
            return start;
         }
      } else {
         return start;
      }
   }

   @Override
   public final long getTrueEndDate() {
      long duration = this.getDuration(null, true);
      long endDate = duration;
      if (this.isRecurring()) {
         endDate -= 172800000;
      }

      if (endDate < 0) {
         endDate = Long.MAX_VALUE;
      } else {
         endDate += this.getTrueStartDate();
         if (endDate < 0) {
            endDate = Long.MAX_VALUE;
         }
      }

      if (this._recur != null) {
         duration = this.getInstanceDuration();
         synchronized (_inclusions) {
            this._recur.getInclusions(_inclusions);
            if (_inclusions != null && _inclusions.length > 0) {
               long temp = duration + _inclusions[_inclusions.length - 1];
               if (temp > endDate) {
                  endDate = temp;
               }
            }

            Array.resize(_inclusions, 0);
            return endDate;
         }
      } else {
         return endDate;
      }
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyNumber) {
      int numReturned = 0;
      long temp = 0;
      if (keyNumber == -7347526267900023482L) {
         numReturned = 1;
         keyArray[index] = this.getTrueStartDate();
      } else if (keyNumber == -104331952420113366L) {
         numReturned = 1;
         keyArray[index] = this.getTrueEndDate();
      } else if (keyNumber == -1863931878973078146L) {
         numReturned = 1;
         temp = this.getDuration(null, true);
         if (temp < 0) {
            temp = Long.MAX_VALUE;
         }

         keyArray[index] = temp;
      }

      return numReturned;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyNumber) {
      return 0;
   }

   @Override
   public final CalendarEventViewer getCalendarEventViewer(Object context) {
      return new EventViewer(this, context);
   }

   @Override
   public final int getSyncID() {
      return this._syncID;
   }

   @Override
   public final Event getEventInstance() {
      return this;
   }

   @Override
   public final ServiceIdentifier getServiceIdentifier() {
      return CalendarServiceManager.getInstance().findCalendarService(this._calendarKey.getCalendarServiceID());
   }

   @Override
   public final boolean isDisplayReminder() {
      return CalendarOptions.getOptions().isDisplayReminders(this._calendarKey);
   }

   @Override
   public final boolean isVisible() {
      return CalendarOptions.getOptions().isShowAppointments(this._calendarKey);
   }

   @Override
   public final int getColour() {
      if (this.getSensitivity() != 0) {
         int privateColour = CalendarOptions.getOptions().getPrivateAppointmentsColour();
         if (privateColour >= 0) {
            return privateColour;
         }
      }

      return CalendarOptions.getOptions().getCalendarColour(this._calendarKey);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      if (PersistentContent.checkEncoding(this._subjectEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._locationEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._notesEncoding, compress, encrypt)) {
         int numExternalObjects = this._externalObjects.length;

         for (int i = 0; i < numExternalObjects; i++) {
            Object object = this._externalObjects[i];
            if (object instanceof Object) {
               EncryptableProvider encryptable = (EncryptableProvider)object;
               if (!encryptable.checkCrypt(compress, encrypt)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._subjectEncoding = PersistentContent.reEncode(this._subjectEncoding, compress, encrypt);
      this._locationEncoding = PersistentContent.reEncode(this._locationEncoding, compress, encrypt);
      this._notesEncoding = PersistentContent.reEncode(this._notesEncoding, compress, encrypt);
      int numExternalObjects = this._externalObjects.length;

      for (int i = 0; i < numExternalObjects; i++) {
         Object object = this._externalObjects[i];
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               this._externalObjects[i] = newObject;
            }
         }
      }

      return this;
   }

   @Override
   public final long getReminderTime(TimeZone tz) {
      return this.getReminderTime(tz, null);
   }

   @Override
   public final long getReminderEndTime(TimeZone tz) {
      ReminderModel rm = this.getReminderData();
      long rmTime = rm == null ? 0 : rm.getTime();
      return !this.isRecurring()
         ? this.getReminderTime(tz, rm) + this.getDuration(tz) + rmTime
         : this.getReminderTime(tz, rm) + this.getInstanceDuration() + rmTime;
   }

   @Override
   public final ReminderModel getReminderData() {
      return (ReminderModel)this.get(813899564474876953L);
   }

   @Override
   public final boolean updateReminderData(ReminderModel rm) {
      this.put(813899564474876953L, rm);
      return true;
   }

   @Override
   public final int getReminderState() {
      ReminderModel rm = this.getReminderData();
      return rm != null ? rm.getState() : Integer.MIN_VALUE;
   }

   @Override
   public final long getReminderID() {
      return this.getLUID();
   }

   @Override
   public final long getCalendarServiceID() {
      return this._calendarKey.getCalendarServiceID();
   }

   @Override
   public final String getReminderDescription() {
      TimeZone tz = TimeZone.getDefault();
      ReminderModel rm = this.getReminderData();
      long start = this.isRecurring() && rm != null ? this.getReminderTime(tz, rm) + rm.getTime() : this.getStart(tz);
      long duration = this.isRecurring() ? this.getInstanceDuration() : this.getDuration(tz);
      String subject = this.getStringForField(5649235763655597796L);
      String location = this.getStringForField(9164664086580876244L);
      StringBuffer b = (StringBuffer)(new Object());
      b.setLength(0);
      int locationLength = location == null ? 0 : location.length();
      int subjectLength = subject == null ? 0 : subject.length();
      int summarySize = Display.getHeight();
      short var17;
      if (Display.getHeight() > 100) {
         var17 = 150;
      } else {
         var17 = 75;
      }

      if (locationLength + subjectLength > 0) {
         if (subjectLength > 0) {
            b.append(subject);
            if (subjectLength > var17 >> 1) {
               b.setLength(var17 >> 1);
               b.append('…');
            }

            b.append(' ');
         }

         if (locationLength > 0) {
            b.append('(');
            b.append(location);
            if (b.length() > var17) {
               b.setLength(var17);
               b.append('…');
            }

            b.append(')');
         }
      }

      if (b.length() > 0) {
         b.append('\n');
      }

      boolean allDay = this.getAllDayFlag();
      DateFormat df = DateFormat.getInstance(allDay ? 48 : 54);
      Calendar cal = Calendar.getInstance(allDay ? TimeZone.getTimeZone(DateTimeUtilities.GMT) : TimeZone.getDefault());
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(start);
      df.format(cal, b, null);
      if (allDay) {
         duration -= 86400000;
      }

      if (duration != 0) {
         b.append('\n');
         calEx.setTimeLong(duration + start);
         df.format(cal, b, null);
      }

      return b.toString();
   }

   @Override
   public final void setTimeZoneID(String tzID) {
      this._timeZoneID = TimeService.getTimeService().getSerialSyncID(tzID);
   }

   @Override
   public final String getTimeZoneID() {
      return TimeService.getTimeService().getTimeZoneIDFromSerialSyncID(this._timeZoneID);
   }

   @Override
   public final Object getAt(int index) {
      synchronized (this.getCalLock()) {
         return this._externalObjects != null && index < this._externalObjects.length ? this._externalObjects[index] : null;
      }
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      synchronized (this.getCalLock()) {
         return ReadableListUtil.getAt(index, count, elements, destIndex, this);
      }
   }

   @Override
   public final String getAccessibleName() {
      return this.getStringForField(-4581712257088750184L);
   }

   @Override
   public final String getAccessibleDescription() {
      return this.getStringForField(-8797898085576394050L);
   }

   @Override
   public final String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public final AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public final AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public final AccessibleContext getAccessibleParent() {
      return null;
   }

   @Override
   public final int getAccessibleRole() {
      return 28;
   }

   @Override
   public final int getAccessibleSelectionCount() {
      return 0;
   }

   @Override
   public final AccessibleContext getAccessibleSelectionAt(int index) {
      return null;
   }

   @Override
   public final boolean isAccessibleChildSelected(int index) {
      return false;
   }

   @Override
   public final int getAccessibleStateSet() {
      return this._accessibleStateSet;
   }

   @Override
   public final long getCalendarFolderID() {
      return this._calendarKey.getCalendarFolderID();
   }

   @Override
   public final void setCalendarKey(CalendarKey key) {
      this._calendarKey = key;
   }

   @Override
   public final boolean isAccessibleStateSet(int state) {
      return (this._accessibleStateSet & state) != 0;
   }

   @Override
   public final CalendarKey getCalendarKey() {
      return this._calendarKey;
   }

   @Override
   public final int getAccessibleChildCount() {
      return this._numOfAccessibleItems;
   }

   @Override
   public final AccessibleContext getAccessibleChildAt(int index) {
      switch (index) {
         case -1:
            return (AccessibleContext)(new Object("", 28, 4, this));
         case 0:
         default:
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            long start = this.getStartDate(tz);
            DateField dateField = (DateField)(new Object("Date: ", start, 16));
            return (AccessibleContext)(new Object(dateField.toString(), 28, 4, this));
         case 1:
            return (AccessibleContext)(new Object(this.getSubject(), 28, 4, this));
         case 2:
            return (AccessibleContext)(new Object(this.getLocation(), 28, 4, this));
         case 3:
            return (AccessibleContext)(this.isAllDay()
               ? new Object("All Day", 28, 4, this)
               : new Object(this.getStringForField(-8797898085576394050L), 28, 4, this));
      }
   }

   @Override
   public final int getIndex(Object element) {
      synchronized (this.getCalLock()) {
         return ReadableListUtil.getIndex(element, this);
      }
   }

   @Override
   public final int match(Object criteria) {
      if (!(criteria instanceof Object)) {
         try {
            SearchCriterion[] crit = (Object[])criteria;
            return Match.match(this, this, crit, _hints);
         } finally {
            return -1;
         }
      } else {
         SearchCriterion crit = (SearchCriterion)criteria;
         int criteriaType = crit.getType();
         if (criteriaType == 24) {
            return crit.getValue() == this.getUID() ? 1 : 0;
         } else if (criteriaType != 21) {
            return -1;
         } else {
            StringMatch stringMatch = (StringMatch)crit.getValue();
            if (Match.stringMatchMatch(stringMatch, this.getSubject()) == 1) {
               return 1;
            } else if (Match.stringMatchMatch(stringMatch, this.getLocation()) == 1) {
               return 1;
            } else {
               return Match.stringMatchMatch(stringMatch, this.getNotes()) == 1 ? 1 : -1;
            }
         }
      }
   }

   @Override
   public final boolean isAnException(long time) {
      if (!this.isRecurring()) {
         return false;
      }

      synchronized (_exclusions) {
         synchronized (_inclusions) {
            this._recur.getInclusions(_inclusions);
            if (_exclusions.length > 0) {
               synchronized (_currCal) {
                  _currCal.setTimeZone(TimeZone.getTimeZone(this.getTimeZoneID()));
                  ((CalendarExtensions)_currCal).setTimeLong(time);
                  return _recurCalc.isAnException(_currCal, _exclusions, _inclusions);
               }
            }

            Array.resize(_exclusions, 0);
            Array.resize(_inclusions, 0);
         }

         return false;
      }
   }

   @Override
   public final boolean isAnInclusion(long time) {
      if (this.isRecurring()) {
         synchronized (_inclusions) {
            this._recur.getInclusions(_inclusions);
            if (_inclusions != null && _inclusions.length > 0) {
               boolean var10000;
               synchronized (_currCal) {
                  _currCal.setTimeZone(TimeZone.getTimeZone(this.getTimeZoneID()));
                  ((CalendarExtensions)_currCal).setTimeLong(time);
                  var10000 = _recurCalc.isAnInclusion(_currCal, _inclusions);
               }

               return var10000;
            } else {
               Array.resize(_inclusions, 0);
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      boolean returnValue = false;
      if (actionId == 6099736323056465049L) {
         EditEventVerb editVerb = new EditEventVerb(this);
         editVerb.invoke(context);
         returnValue = true;
      }

      return returnValue;
   }

   @Override
   public final int getIconsForField(long field, IconCollection[] icons, int[][] indices) {
      if (field == 5975440733927886771L) {
         return 0;
      }

      int numIconsAdded = 0;
      synchronized (this.getCalLock()) {
         int max = this._externalObjects.length;

         for (int i = 0; i < max; i++) {
            Object exData = this._externalObjects[i];
            if (exData instanceof Object) {
               RIMModel rm = (RIMModel)exData;
               if (rm instanceof Object) {
                  DescriptionProvider dp = (DescriptionProvider)rm;
                  numIconsAdded += dp.getIconsForField(field, icons, indices);
               }
            }
         }

         if (field == 7380487202915104824L) {
            int[] ourIndices = null;
            if (this.isRecurring()) {
               ourIndices = addIndex(ourIndices, 0);
            }

            if (this.getRelatedLUID() != 0) {
               ourIndices = addIndex(ourIndices, 1);
            }

            if (this._notesEncoding != null) {
               boolean showNotesIcon = true;
               if (this._notesEncoding instanceof Object) {
                  String str = (String)this._notesEncoding;
                  if (str.trim().length() == 0) {
                     showNotesIcon = false;
                  }
               }

               if (showNotesIcon) {
                  ourIndices = addIndex(ourIndices, 2);
               }
            }

            if (ourIndices != null) {
               int numIcons = icons.length;
               Array.resize(icons, numIcons + 1);
               Array.resize(indices, numIcons + 1);
               icons[numIcons] = CalendarIcons.CAL_STATUS_ICONS;
               indices[numIcons] = ourIndices;
               numIconsAdded += ourIndices.length;
            }
         }

         return numIconsAdded;
      }
   }

   private static final int[] addIndex(int[] indices, int index) {
      if (indices == null) {
         indices = new int[0];
      }

      int numIndices = indices.length;
      Array.resize(indices, numIndices + 1);
      indices[numIndices] = index;
      return indices;
   }

   @Override
   public final byte getProperties() {
      return this.getPropertiesForEvent();
   }

   private final boolean getHandleTimeWorker(Recur$Handle handle, long targetDateTime, TimeZone tz, boolean forward) {
      if (this._recur == null) {
         return false;
      }

      TimeZone tzRecur;
      if (!this.getAllDayFlag()) {
         tzRecur = TimeZone.getTimeZone(this.getTimeZoneID());
         if (tzRecur == null) {
            tzRecur = TimeZone.getDefault();
         }
      } else {
         synchronized (_currCal) {
            synchronized (_gmtCal) {
               Calendar currCal = _currCal;
               Calendar gmtCal = _gmtCal;
               currCal.setTimeZone(tz);
               long startOfRecurrence = this.getStart(tz);
               gmtCal.set(0, 1);
               currCal.set(0, 1);
               if (targetDateTime < startOfRecurrence) {
                  ((CalendarExtensions)currCal).setTimeLong(startOfRecurrence);
                  currCal.set(1, currCal.get(1) - 1);
                  targetDateTime = ((CalendarExtensions)currCal).getTimeLong();
               }

               ((CalendarExtensions)currCal).setTimeLong(targetDateTime);
               gmtCal.set(1, currCal.get(1));
               gmtCal.set(2, currCal.get(2));
               gmtCal.set(5, currCal.get(5));
               gmtCal.set(11, currCal.get(11));
               gmtCal.set(12, currCal.get(12));
               gmtCal.set(13, currCal.get(13));
               gmtCal.set(14, currCal.get(14));
               targetDateTime = ((CalendarExtensions)gmtCal).getTimeLong();
               tzRecur = gmtCal.getTimeZone();
            }
         }
      }

      return _recurCalc.getAnInstance(targetDateTime, forward, handle, this._startDateOffset, this.getInstanceDuration(), this._recur, tzRecur);
   }

   @Override
   public final boolean isAllDay() {
      return this.getAllDayFlag();
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof EventImpl)) {
         return false;
      }

      EventImpl that = (EventImpl)obj;
      return that.getUID() == this.getUID();
   }

   @Override
   public final long getDuration(TimeZone tz) {
      return this.getDuration(tz, false);
   }

   @Override
   public final int hashCode() {
      return this._localID;
   }

   EventImpl(CalendarKey calendarKey) {
      this._calendarKey = calendarKey;
      this._syncID = this.getNextSequenceNumber();
      this.setFreeBusy((byte)2);
      this.setSensitivity((byte)0);
      this.setInstanceDuration(3600000);
      this._iCalID = EventUtilities.generateStringUID();
   }

   private final long getDuration(TimeZone tz, boolean accountForInclusions) {
      if (this.isRecurring()) {
         if (!this._recur.isFinite()) {
            return -1;
         }

         long endDate = this._recur.getEndDate();
         if (accountForInclusions) {
            synchronized (_inclusions) {
               this._recur.getInclusions(_inclusions);
               if (_inclusions != null && _inclusions.length > 0) {
                  long temp = _inclusions[_inclusions.length - 1] + this.getInstanceDuration();
                  if (temp > endDate) {
                     endDate = temp;
                  }
               }

               Array.resize(_inclusions, 0);
            }
         }

         return accountForInclusions ? endDate - this.getTrueStartDate() + 172800000 : endDate - this._startDateOffset + 172800000;
      } else if (this.getAllDayFlag() && tz != null) {
         synchronized (_currCal) {
            long timeToReturn;
            synchronized (_gmtCal) {
               Calendar currCal = _currCal;
               Calendar gmtCal = _gmtCal;
               currCal.setTimeZone(tz);
               ((CalendarExtensions)gmtCal).setTimeLong(this._startDateOffset + this.getInstanceDuration());
               currCal.set(1, gmtCal.get(1));
               currCal.set(2, gmtCal.get(2));
               currCal.set(5, gmtCal.get(5));
               currCal.set(11, gmtCal.get(11));
               currCal.set(12, gmtCal.get(12));
               currCal.set(13, gmtCal.get(13));
               currCal.set(14, gmtCal.get(14));
               timeToReturn = ((CalendarExtensions)currCal).getTimeLong() - this.getStart(tz);
            }

            return timeToReturn;
         }
      } else {
         return this.getInstanceDuration();
      }
   }

   @Override
   protected final void checkForVerbOverrides(Object context, Verb[] defaultVerbs) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      ContextObject.put(contextObject, 248, defaultVerbs);
      ContextObject.setFlag(contextObject, 87);
      ContextObject.put(contextObject, 424670468422402792L, this);
      int count = this.size();
      Object[] models = new Object[count];
      this.getElements(models);

      for (int i = 0; i < count; i++) {
         Object model = models[i];
         if (model instanceof Object) {
            VerbProvider verbProvider = (VerbProvider)model;
            verbProvider.getVerbs(contextObject, new Object[0]);
         }
      }

      ContextObject.remove(contextObject, 248);
      ContextObject.clearFlag(contextObject, 87);
      ContextObject.remove(contextObject, 424670468422402792L);
   }

   private final int getNextSequenceNumber() {
      synchronized (this.getCalLock()) {
         int eventSequence = PersistentInteger.get(_persistentId);
         PersistentInteger.set(_persistentId, eventSequence + 1);
         return eventSequence;
      }
   }

   @Override
   public final long getStart(TimeZone tz) {
      if (this.getAllDayFlag() && tz != null) {
         synchronized (_currCal) {
            long timeToReturn;
            synchronized (_gmtCal) {
               Calendar currCal = _currCal;
               Calendar gmtCal = _gmtCal;
               currCal.setTimeZone(tz);
               ((CalendarExtensions)gmtCal).setTimeLong(this._startDateOffset);
               gmtCal.set(0, 1);
               currCal.set(0, 1);
               currCal.set(1, gmtCal.get(1));
               currCal.set(2, gmtCal.get(2));
               currCal.set(5, gmtCal.get(5));
               currCal.set(11, gmtCal.get(11));
               currCal.set(12, gmtCal.get(12));
               currCal.set(13, gmtCal.get(13));
               currCal.set(14, gmtCal.get(14));
               timeToReturn = ((CalendarExtensions)currCal).getTimeLong();
            }

            return timeToReturn;
         }
      } else {
         return this._startDateOffset;
      }
   }

   @Override
   public final String getStringForField(long field) {
      return this.getStringForField(field, this.getStart(Calendar.getInstance().getTimeZone()));
   }

   private final long getReminderTime(TimeZone tz, ReminderModel rm) {
      if (rm == null) {
         rm = this.getReminderData();
      }

      if (rm != null) {
         if (!this.isRecurring()) {
            return this.getStart(tz) - rm.getTime();
         }

         int reminderState = rm.getState();
         if (reminderState == 5 || reminderState == 2 || reminderState == 7 || reminderState == 6) {
            return rm.getReminderFiredFor() - rm.getTime();
         }

         Recur$Handle handle = (Recur$Handle)(new Object());
         boolean validInstance = this.getHandleAfterTime(
            handle, rm.getReminderFiredFor() + (this.getInstanceDuration() == 0 ? 1 : this.getInstanceDuration()), tz
         );
         if (validInstance) {
            return handle._handle - rm.getTime();
         }
      }

      return Long.MAX_VALUE;
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object(""))).append(this.getUID()).append("  -  ").append(this.getSubject()).toString();
   }

   @Override
   public final String getStringForField(long field, long data) {
      String str = null;
      if (field == 5649235763655597796L) {
         str = this.getSubject();
      } else if (field == 9164664086580876244L) {
         str = this.getLocation();
      } else if (field == -8797898085576394050L) {
         str = this.getTimeSummary(data);
      } else if (field == -4581712257088750184L) {
         str = this.getBriefSummary();
      } else if (field == 1589658722817992360L) {
         str = this.getVerboseSummary();
      } else {
         Object[] sharedArray = EventBase._sharedObjectArray;
         synchronized (sharedArray) {
            this.getElements(sharedArray);

            for (Object exData : sharedArray) {
               if (exData instanceof Object) {
                  RIMModel rm = (RIMModel)exData;
                  if (rm instanceof Object) {
                     DescriptionProvider dp = (DescriptionProvider)rm;
                     str = dp.getStringForField(field);
                     if (str != null) {
                        break;
                     }
                  }
               }
            }
         }
      }

      return str;
   }
}
