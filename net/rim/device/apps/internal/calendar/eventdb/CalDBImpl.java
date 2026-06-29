package net.rim.device.apps.internal.calendar.eventdb;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.LongHashtableCollection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.controller.DurationParts;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.sync.OTASyncIDProvider;
import net.rim.device.apps.internal.calendar.eventprovider.EventComparator;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;
import net.rim.vm.Memory;

class CalDBImpl implements CalDB {
   private PersistentObject _calData;
   private CalDBImpl$CalendarData _cal;
   private OTACalendarSyncDataManager _otaSyncDataManager;
   private CollectionListenerManager _notifierExternal = (CollectionListenerManager)(new Object());
   private CollectionListenerManager _notifierSort = (CollectionListenerManager)(new Object());
   private CollectionListenerManager _notifierLowPriorityListeners = (CollectionListenerManager)(new Object());
   private boolean _notificationsSuspended;
   private CalDBImpl$CalDBLongSortedReadableList _nonRecurringStartTimeSort = new CalDBImpl$CalDBLongSortedReadableList(
      (LongKeyProviderAdaptor)(new Object(-7347526267900023482L)), true, false
   );
   private CalDBImpl$CalDBLongSortedReadableList _recurringStartTimeSort = new CalDBImpl$CalDBLongSortedReadableList(
      (LongKeyProviderAdaptor)(new Object(-7347526267900023482L)), false, true
   );
   private CalDBImpl$CalDBLongSortedReadableList _endTimeSort = new CalDBImpl$CalDBLongSortedReadableList(
      (LongKeyProviderAdaptor)(new Object(-104331952420113366L)), true, true
   );
   private CalDBImpl$CalDBLongSortedReadableList _durationSort = new CalDBImpl$CalDBLongSortedReadableList(
      (LongKeyProviderAdaptor)(new Object(-1863931878973078146L)), true, true
   );
   private LongThresholdList _sharedThresh = new LongThresholdList();
   private LongHashtable _sharedLongHash = (LongHashtable)(new Object());
   private RecurCache _recurCache;
   private CalDBImpl$SetOfEvents _setOfEvents = new CalDBImpl$SetOfEvents();
   private Calendar _cachedCalendar = Calendar.getInstance();
   private long _calendarServiceID;
   private CalendarServiceManager _calendarServiceManager = CalendarServiceManager.getInstance();
   private CalDBImpl$CalendarBigSortedReadableList _eventsSortedByStartDate;
   static final long SYNC_STATS_GUID;
   private static final int MAX_DST_CHANGE_HANDLED;
   private static final int DOUBLE_MAX_DST_CHANGE_HANDLED;
   private static final int PERSISTENT_GC_THRESHOLD;
   private static final long INVALID_TIME;
   private static EventComparator _eventComparator = new EventComparator();
   private static final int MIN_IDLE_TIME_SEC;
   private static final int MIN_IDLE_TIME_MSEC;

   boolean init(long calendarServiceID) {
      this._recurCache = new RecurCache();
      this._calendarServiceID = calendarServiceID;
      this._calData = RIMPersistentStore.getPersistentObject(calendarServiceID);
      synchronized (this._calData) {
         if (this._calData.getContents() == null) {
            this._cal = new CalDBImpl$CalendarData();
            this._cal._calElements = (LongHashtableCollection)(new Object());
            this._calData.setContents(this._cal, 51, false);
            this._calData.commit();
         }
      }

      this._cal = (CalDBImpl$CalendarData)this._calData.getContents();
      synchronized (this.getLockObject()) {
         this._eventsSortedByStartDate = new CalDBImpl$CalendarBigSortedReadableList(
            this, (Comparator)(new Object((LongKeyProviderAdaptor)(new Object(-7347526267900023482L))))
         );
      }

      this._nonRecurringStartTimeSort.loadFrom(this._cal._calElements);
      this._recurringStartTimeSort.loadFrom(this._cal._calElements);
      this._endTimeSort.loadFrom(this._cal._calElements);
      this._durationSort.loadFrom(this._cal._calElements);
      this._notifierSort.addCollectionListener(this._nonRecurringStartTimeSort);
      this._notifierSort.addCollectionListener(this._recurringStartTimeSort);
      this._notifierSort.addCollectionListener(this._endTimeSort);
      this._notifierSort.addCollectionListener(this._durationSort);
      PersistentContent.addListener(this);
      this._otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
      return true;
   }

   @Override
   public CICALConfiguration getCICALConfiguration() {
      CalendarService calendarService = this._calendarServiceManager.findCalendarService(this._calendarServiceID);
      if (calendarService == null) {
         calendarService = this._calendarServiceManager.getDefaultCalendarService();
      }

      return calendarService.getCICALConfiguration();
   }

   @Override
   public long getCalendarServiceID() {
      return this._calendarServiceID;
   }

   @Override
   public void updateIncomingStatistics(int value) {
      this.updateStatistics(true, value);
   }

   @Override
   public void updateOutgoingStatistics(int value) {
      this.updateStatistics(false, value);
   }

   private void updateStatistics(boolean incoming, int value) {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(-2552927992871374063L);
      synchronized (persistentObject) {
         Object o = persistentObject.getContents();
         CalDBImpl$CalendarSyncStatistics stats = null;
         if (!(o instanceof CalDBImpl$CalendarSyncStatistics)) {
            stats = new CalDBImpl$CalendarSyncStatistics();
         } else {
            stats = (CalDBImpl$CalendarSyncStatistics)o;
         }

         if (incoming) {
            stats._incomingEvents += value;
         } else {
            stats._outgoingEvents += value;
         }

         persistentObject.setContents(stats, 51);
         persistentObject.commit();
      }
   }

   @Override
   public void resetStatistics() {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(-2552927992871374063L);
      synchronized (persistentObject) {
         Object o = persistentObject.getContents();
         CalDBImpl$CalendarSyncStatistics stats = null;
         if (!(o instanceof CalDBImpl$CalendarSyncStatistics)) {
            stats = new CalDBImpl$CalendarSyncStatistics();
         } else {
            stats = (CalDBImpl$CalendarSyncStatistics)o;
         }

         stats._incomingEvents = 0;
         stats._outgoingEvents = 0;
         persistentObject.setContents(stats, 51);
         persistentObject.commit();
      }
   }

   private void addFromSorted(
      CalDBImpl$CalDBLongSortedReadableList sorted,
      long start,
      boolean include_start,
      long end,
      boolean include_end,
      boolean excludeRecurrences,
      LongHashtable new_list
   ) {
      this._sharedThresh.loadFrom(sorted);
      this._sharedThresh.setThresholds(start, include_start, end, include_end);
      int numObjects = this._sharedThresh.size();

      for (int counter = 0; counter < numObjects; counter++) {
         Object calObject = this._sharedThresh.getAt(counter);
         Event event = (Event)calObject;
         if (event.isVisible() && (!excludeRecurrences || !event.isRecurring())) {
            new_list.put(((UniqueIDProvider)event).getLUID(null), calObject);
         }
      }
   }

   @Override
   public Object[] getAllEventsInRange(long startRange, long endRange) {
      Object[] result = null;
      if (endRange == -1 || startRange > endRange) {
         endRange = Long.MAX_VALUE;
      }

      int size = this._endTimeSort.size();
      if (size > 0) {
         int insertPoint = this._endTimeSort.findInsertPoint(startRange);
         if (insertPoint < size) {
            result = new Object[size - insertPoint];
            int count = 0;

            while (insertPoint < size) {
               Event e = (Event)this._endTimeSort.getAt(insertPoint);
               boolean valid = true;
               if (e.getTrueEndDate() > endRange && e.getTrueStartDate() > endRange) {
                  valid = false;
               }

               if (valid) {
                  result[count] = e;
                  count++;
               }

               insertPoint++;
            }

            Array.resize(result, count);
         }
      }

      return result;
   }

   private void getPossiblyVisibleEvents(long start, long duration, LongHashtable possiblyVisibleEvents) {
      long end = start + duration;
      this.addFromSorted(this._nonRecurringStartTimeSort, start, true, end, false, false, possiblyVisibleEvents);
      this.addFromSorted(this._recurringStartTimeSort, start, true, end, false, false, possiblyVisibleEvents);
      this.addFromSorted(this._endTimeSort, start, false, end, true, false, possiblyVisibleEvents);
      this.addFromSorted(this._durationSort, duration, true, Long.MAX_VALUE, true, false, possiblyVisibleEvents);
   }

   private void restrictPossiblyVisibleEvents(long start, long duration, TimeZone tz, LongHashtable possiblyVisibleEvents, Vector visibleEvents) {
      RecurCache recurCache = this._recurCache;
      Object[] recurArray = new Object[0];
      Enumeration allPossible = possiblyVisibleEvents.elements();
      visibleEvents.setSize(0);

      while (allPossible.hasMoreElements()) {
         Object calObject = allPossible.nextElement();
         if (calObject instanceof Object) {
            DurationParts partsController = (DurationParts)calObject;
            if (partsController.hasParts()) {
               UniqueIDProvider uidp = (UniqueIDProvider)calObject;
               recurCache.getOccurrencesIn(start, duration, tz, partsController, uidp.getLUID(null), recurArray);
               int max = recurArray.length;

               for (int i = 0; i < max; i++) {
                  visibleEvents.addElement(recurArray[i]);
               }
               continue;
            }
         }

         Duration durController = (Duration)calObject;
         long durObjStart = durController.getStart(tz);
         long durObjDur = durController.getDuration(tz);
         if (durObjStart < start + duration && (durObjStart + durObjDur > start || durObjStart >= start)) {
            visibleEvents.addElement(calObject);
         }
      }
   }

   @Override
   public int getStoreIdentifier() {
      return 0;
   }

   @Override
   public Object getLockObject() {
      return this;
   }

   private void getNonRecurringElementsVisibleDuring(long start, long end, TimeZone tz, LongHashtable tableOfEvents) {
      this.addFromSorted(this._nonRecurringStartTimeSort, start, true, end, false, true, tableOfEvents);
      this.addFromSorted(this._durationSort, end - start, true, Long.MAX_VALUE, true, true, tableOfEvents);
      this.addFromSorted(this._endTimeSort, start, false, end, true, true, tableOfEvents);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void getElementsVisibleDuring(long start, long duration, TimeZone tz, SimpleSortingVector eventVector, boolean forceSort) {
      synchronized (this.getLockObject()) {
         long boundaryStart = start - tz.getRawOffset();
         long boundaryDuration;
         if (boundaryStart > start) {
            boundaryDuration = boundaryStart + duration - start;
            boundaryStart = start;
         } else {
            boundaryDuration = start + duration - boundaryStart;
         }

         boundaryStart -= 7200000;
         boundaryDuration += 14400000;
         boolean var17 = false /* VF: Semaphore variable */;

         try {
            var17 = true;
            this.getPossiblyVisibleEvents(boundaryStart, boundaryDuration, this._sharedLongHash);
            this.restrictPossiblyVisibleEvents(start, duration, tz, this._sharedLongHash, eventVector);
            var17 = false;
         } finally {
            if (var17) {
               this._sharedLongHash.clear();
            }
         }

         this._sharedLongHash.clear();
         if (forceSort) {
            _eventComparator.setTimeZone(tz);
            eventVector.setSortComparator(_eventComparator);
            eventVector.reSort();
            _eventComparator.setTimeZone(null);
            eventVector.setSortComparator(null);
         }
      }
   }

   @Override
   public void getElementsVisibleDuring(long start, long duration, TimeZone tz, SimpleSortingVector eventVector) {
      this.getElementsVisibleDuring(start, duration, tz, eventVector, false);
   }

   private int getNearestNonRecurring(long time, TimeZone tz) {
      int numNonRecurring = this._nonRecurringStartTimeSort.size();
      long searchTime = time - tz.getRawOffset();
      int low = 0;
      int high = numNonRecurring - 1;

      while (low <= high) {
         int mid = (low + high) / 2;
         long start = this._nonRecurringStartTimeSort.getLongAt(mid);
         if (start < searchTime) {
            low = mid + 1;
         } else {
            high = mid - 1;
         }
      }

      return low;
   }

   @Override
   public long[] getElementsStartingAround(long time, int maxBefore, int maxOnOrAfter, TimeZone tz, Vector eventVector) {
      long[] result = new long[5];
      int returnVal = 0;
      synchronized (this.getLockObject()) {
         eventVector.setSize(0);
         CalDBImpl$SetOfEvents set = this._setOfEvents;
         set.setParameters(time, maxBefore, maxOnOrAfter);
         int numNonRecurring = this._nonRecurringStartTimeSort.size();
         int numRecurring = this._recurringStartTimeSort.size();
         long lastStart = -1;
         int low = this.getNearestNonRecurring(time, tz);

         for (int i = low; i < numNonRecurring; i++) {
            Object rm = this._nonRecurringStartTimeSort.getAt(i);
            Event event = (Event)rm;
            if (event.isVisible()) {
               long luid = ((UniqueIDProvider)rm).getLUID(null);
               long start = ((Duration)rm).getStart(tz);
               if (!set.insert(rm, start, luid)) {
                  if (!DateTimeUtilities.isSameDate(lastStart, start, tz, null, this._cachedCalendar)) {
                     break;
                  }

                  set.ResizeAfterLists(++maxOnOrAfter);
                  set.insert(rm, start, luid);
               } else {
                  lastStart = start;
               }
            }
         }

         lastStart = -1;

         for (int i = low - 1; i >= 0; i--) {
            Object rm = this._nonRecurringStartTimeSort.getAt(i);
            Event event = (Event)rm;
            if (event.isVisible()) {
               long luid = ((UniqueIDProvider)rm).getLUID(null);
               long start = ((Duration)rm).getStart(tz);
               if (!set.insert(rm, start, luid)) {
                  if (!DateTimeUtilities.isSameDate(lastStart, start, tz, null, this._cachedCalendar)) {
                     break;
                  }

                  set.ResizeBeforeLists(++maxBefore);
                  set.insert(rm, start, luid);
               } else {
                  lastStart = start;
               }
            }
         }

         int recurringIndex = 0;
         int recurType = 0;
         long closest = -1;
         Hashtable recurrences = (Hashtable)(new Object(numRecurring));
         IntHashtable candidates = (IntHashtable)(new Object());
         long[] timeDelta = new long[]{
            -1L,
            -1L,
            -1L,
            -1L,
            -1L,
            7950412918156361984L,
            3058789673383143065L,
            72169106143838464L,
            -1552493614806110346L,
            1246482708980695296L,
            1788931188457483L,
            892563751440898822L,
            8404463032538073709L,
            7783909439788811776L,
            174521085922993410L,
            5299177953428407653L,
            7783909526141224294L,
            576588575910946050L,
            2740440872547387392L,
            4622390865820939328L,
            9107440941402179188L,
            7928104511785535488L,
            2740440422267906420L,
            -9068552002425254801L,
            576491951099480064L,
            2884556055728097576L,
            576588994223824905L,
            1956822834305112872L,
            2884556397478151969L,
            30406372588153203L,
            576535836486675208L,
            576571993280090667L,
            576535834747561515L,
            2773762624306242369L,
            532340683575546754L,
            7166182776929151273L,
            532340683584334194L,
            4904529161510611241L,
            576610738775487333L,
            5656066385823359809L
         };

         while (recurringIndex < numRecurring) {
            Object rm = this._recurringStartTimeSort.getAt(recurringIndex);
            Event event = (Event)rm;
            if (!event.isVisible()) {
               recurringIndex++;
            } else {
               recurrences.put(rm, rm);
               int var85 = ((Event)rm).getReadOnlyRecurrence().getRecurType();
               long luid = ((UniqueIDProvider)rm).getLUID(null);
               DurationParts dpc = (DurationParts)rm;
               Object startingPart = dpc.getPrevFromTime(time, tz);
               if (startingPart != null) {
                  Duration partDuration = (Duration)startingPart;
                  long start = partDuration.getStart(tz);
                  closest = timeDelta[var85];
                  if (closest < 0 || time - start < closest) {
                     closest = time - start;
                     timeDelta[var85] = closest;
                     candidates.put(var85, rm);
                  }
               }

               startingPart = dpc.getNextFromTime(time, tz);
               if (startingPart != null) {
                  Duration partDuration = (Duration)startingPart;
                  long start = partDuration.getStart(tz);
                  closest = timeDelta[var85];
                  if (closest < 0 || start - time < closest) {
                     closest = start - time;
                     timeDelta[var85] = closest;
                     candidates.put(var85, rm);
                  }
               }

               recurringIndex++;
            }
         }

         boolean lookingForCandidate = true;
         Enumeration recurrEnum = recurrences.elements();

         while (!set.isFilled()) {
            Object rm;
            if (lookingForCandidate) {
               rm = candidates.get(1);
               int var86 = 1;
               if (rm == null) {
                  rm = candidates.get(2);
                  var86 = 2;
               }

               if (rm == null) {
                  rm = candidates.get(3);
                  var86 = 3;
               }

               if (rm == null) {
                  rm = candidates.get(4);
                  var86 = 4;
               }

               if (rm == null) {
                  lookingForCandidate = false;
                  continue;
               }

               candidates.remove(var86);
            } else {
               if (!recurrEnum.hasMoreElements()) {
                  break;
               }

               rm = recurrEnum.nextElement();
            }

            int var87 = ((Event)rm).getReadOnlyRecurrence().getRecurType();
            recurrences.remove(rm);
            long luid = ((UniqueIDProvider)rm).getLUID(null);
            DurationParts dpc = (DurationParts)rm;
            Object startingPart = dpc.getPrevFromTime(time, tz);

            for (Object currentPart = startingPart; currentPart != null; currentPart = dpc.getPrev(currentPart)) {
               Duration partDuration = (Duration)currentPart;
               long start = partDuration.getStart(tz);
               if (!set.insert(currentPart, start, luid)) {
                  break;
               }
            }

            Object var79;
            if (startingPart == null) {
               var79 = dpc.getNextFromTime(time, tz);
            } else {
               var79 = dpc.getNext(startingPart);
            }

            while (var79 != null) {
               Duration partDuration = (Duration)var79;
               long start = partDuration.getStart(tz);
               if (!set.insert(var79, start, luid)) {
                  break;
               }

               var79 = dpc.getNext(var79);
            }
         }

         RecurCache recurCache = this._recurCache;
         Object[] recurArray = new Object[0];
         recurrEnum = recurrences.elements();

         while (recurrEnum.hasMoreElements()) {
            Object rm = recurrEnum.nextElement();
            long currentStart = set.getEarliestTime(tz);
            if (currentStart == -1) {
               currentStart = time;
            }

            long currentDuration;
            if (set.getLatestTime(tz) == -1) {
               currentDuration = 0;
            } else {
               currentDuration = set.getLatestTime(tz) - currentStart;
            }

            DurationParts dpc = (DurationParts)rm;
            long luid = ((UniqueIDProvider)rm).getLUID(null);
            recurCache.getOccurrencesIn(currentStart, currentDuration, tz, dpc, luid, recurArray);

            for (Object currentPart : recurArray) {
               Duration partDuration = (Duration)currentPart;
               long start = partDuration.getStart(tz);
               set.insert(currentPart, start, luid);
            }
         }

         long startOfSet = this.getStartOfSet(set, time, tz);
         long endOfSet = this.getEndOfSet(set, time, tz);
         this._sharedLongHash.clear();
         this.getNonRecurringElementsVisibleDuring(startOfSet, endOfSet, tz, this._sharedLongHash);
         if (this._sharedLongHash.size() > 0) {
            LongEnumeration lenum = this._sharedLongHash.keys();

            while (lenum.hasMoreElements()) {
               long luid = lenum.nextElement();
               boolean found = set.containsLUID(luid);
               if (!found) {
                  Object rm = this.get(luid);
                  Event event = (Event)rm;
                  if (event.isVisible()) {
                     long start = ((Duration)rm).getStart(tz);
                     long eventEnd = start + ((Duration)rm).getDuration(tz);
                     if (eventEnd >= startOfSet && start <= startOfSet) {
                        if (start < time) {
                           set.ResizeBeforeLists(set._numBefore + 1);
                        } else {
                           set.ResizeAfterLists(set._numOnOrAfter + 1);
                        }

                        set.insert(rm, start, luid);
                     }
                  }
               }
            }
         }

         this._sharedLongHash.clear();
         returnVal = set.getEvents(eventVector);
         result[0] = set._numBefore;
         result[1] = set._numOnOrAfter;
         result[2] = returnVal;
         result[3] = this.getStartOfSet(set, time, tz);
         result[4] = this.getLatestTime(set, time, tz);
         set.clear();
         return result;
      }
   }

   private long getStartOfSet(CalDBImpl$SetOfEvents set, long time, TimeZone tz) {
      long startOfSet = set.getEarliestTime(tz);
      if (startOfSet == -1) {
         startOfSet = time;
      }

      ((CalendarExtensions)this._cachedCalendar).setTimeLong(startOfSet);
      DateTimeUtilities.zeroCalendarTime(this._cachedCalendar);
      return ((CalendarExtensions)this._cachedCalendar).getTimeLong();
   }

   private long getLatestTime(CalDBImpl$SetOfEvents set, long time, TimeZone tz) {
      long endOfSet = set.getLatestTime(tz);
      if (endOfSet == -1) {
         endOfSet = time;
      }

      return endOfSet;
   }

   private long getEndOfSet(CalDBImpl$SetOfEvents set, long time, TimeZone tz) {
      long endOfSet = this.getLatestTime(set, time, tz);
      ((CalendarExtensions)this._cachedCalendar).setTimeLong(endOfSet);
      DateTimeUtilities.zeroCalendarTime(this._cachedCalendar);
      this._cachedCalendar.set(5, this._cachedCalendar.get(5) + 1);
      endOfSet = ((CalendarExtensions)this._cachedCalendar).getTimeLong();
      return endOfSet - 1;
   }

   private boolean searchFromInsertPoint(long[] times, int startIndex, long start, long luid, long[] luids) {
      boolean found = false;

      for (int i = startIndex; i >= 0 && times[i] == start; i--) {
         if (luids[i] == luid) {
            found = true;
         }
      }

      for (int i = startIndex + 1; i < times.length && times[i] == start; i++) {
         if (luids[i] == luid) {
            found = true;
         }
      }

      return found;
   }

   @Override
   public Object getElementWithEarliestEndDate(int calendarID) {
      synchronized (this.getLockObject()) {
         return this._endTimeSort.size() > 0 ? this._endTimeSort.getAt(0) : null;
      }
   }

   @Override
   public Object getElementWithEarliestEndDate() {
      return this.getElementWithEarliestEndDate(0);
   }

   @Override
   public ReadableList getEventsSortedByStartDate(int calendarID) {
      return this._eventsSortedByStartDate;
   }

   @Override
   public ReadableList getEventsSortedByStartDate() {
      return this.getEventsSortedByStartDate(0);
   }

   @Override
   public void addCollectionListener(Object listenerToAdd) {
      if (listenerToAdd instanceof CalDBImpl$CalendarBigSortedReadableList) {
         this._notifierSort.addCollectionListener(listenerToAdd);
      } else {
         this._notifierExternal.addCollectionListener(new Object(listenerToAdd));
      }
   }

   @Override
   public void removeCollectionListener(Object listenerToRemove) {
      this._notifierExternal.removeCollectionListener(listenerToRemove);
   }

   @Override
   public Object get(long key) {
      synchronized (this.getLockObject()) {
         return this._cal._calElements.get(key);
      }
   }

   @Override
   public long getKey(Object o) {
      synchronized (this.getLockObject()) {
         UniqueIDProvider uniqueVal = (UniqueIDProvider)o;
         return uniqueVal.getLUID(null);
      }
   }

   @Override
   public boolean contains(long key) {
      synchronized (this.getLockObject()) {
         return this._cal._calElements.contains(key);
      }
   }

   @Override
   public int size() {
      synchronized (this.getLockObject()) {
         return this._cal._calElements.size();
      }
   }

   @Override
   public int size(int calendarFolderID) {
      int size = 0;
      synchronized (this.getLockObject()) {
         Enumeration en = this._cal._calElements.getElements();

         while (en.hasMoreElements()) {
            Event e = (Event)en.nextElement();
            if (e.getCalendarKey().getCalendarFolderID() == calendarFolderID) {
               size++;
            }
         }

         return size;
      }
   }

   @Override
   public boolean contains(Object element) {
      synchronized (this.getLockObject()) {
         return this._cal._calElements.contains(element);
      }
   }

   @Override
   public Enumeration getElements() {
      synchronized (this.getLockObject()) {
         return this._cal._calElements.getElements();
      }
   }

   @Override
   public int getElements(Object[] elements) {
      synchronized (this.getLockObject()) {
         return this._cal._calElements.getElements(elements);
      }
   }

   @Override
   public void addLowPriorityListener(CollectionListener listener) {
      this._notifierLowPriorityListeners.addCollectionListener(listener);
   }

   @Override
   public void add(Object elementToAdd) {
      this.addWithAction(elementToAdd, 1);
   }

   @Override
   public void addWithAction(Object elementToAdd, int addAction) {
      if (elementToAdd != null) {
         Event event = (Event)elementToAdd;
         OTASyncData syncData = this._otaSyncDataManager.get(event);
         byte[] hashData = EventUtilities.getHashData((Event)elementToAdd);
         byte[] oldHashData = null;
         if (syncData == null) {
            syncData = (OTASyncData)(new Object(0, 1));
            this._otaSyncDataManager.add(event, syncData);
         }

         oldHashData = syncData.getChecksum();
         if ((addAction & 1) > 0) {
            syncData.updateChecksum(hashData, true);
         }

         if ((addAction & 2) > 0) {
            syncData.markClean();
         }

         PersistentObject.commit(syncData);
         this._cal._dirty = this._cal._dirty || oldHashData == null || !Arrays.equals(oldHashData, hashData);
         if (ObjectGroup.isInGroup(elementToAdd)) {
            elementToAdd = ObjectGroup.expandGroup(elementToAdd);
         }

         synchronized (this.getLockObject()) {
            if (elementToAdd instanceof Object) {
               EncryptableProvider ep = (EncryptableProvider)elementToAdd;
               ep.reCrypt(true, true);
            }

            label88:
            try {
               ObjectGroup.createGroup(elementToAdd);
            } finally {
               break label88;
            }

            long UID = this.getKey(elementToAdd);
            this._recurCache.clear(UID);
            boolean alreadyPresent = this._cal._calElements.contains(UID);
            Object oldEntry = null;
            if (alreadyPresent) {
               oldEntry = this._cal._calElements.get(UID);
            }

            this._cal._calElements.put(UID, elementToAdd);
            DirtyBits.setDirty(elementToAdd);
            this._calData.commit();
            if (!this._notificationsSuspended) {
               if (!alreadyPresent) {
                  this._notifierSort.fireElementAdded(this, elementToAdd);
                  if ((addAction & 4) <= 0) {
                     this._notifierExternal.fireElementAdded(this, elementToAdd);
                     this._notifierLowPriorityListeners.fireElementAdded(this, elementToAdd);
                  }
               } else {
                  this._notifierSort.fireElementUpdated(this, oldEntry, elementToAdd);
                  if ((addAction & 4) <= 0) {
                     this._notifierExternal.fireElementUpdated(this, oldEntry, elementToAdd);
                     this._notifierLowPriorityListeners.fireElementUpdated(this, oldEntry, elementToAdd);
                  }
               }
            }
         }
      }
   }

   @Override
   public void removeAll() {
      synchronized (this.getLockObject()) {
         this._cal._calElements.removeAll();
         this._recurCache.clearAll();
         this._notifierSort.fireReset(this);
         this._notifierExternal.fireReset(this);
         this._notifierLowPriorityListeners.fireReset(this);
      }
   }

   @Override
   public void remove(Object objectToRemove) {
      if (objectToRemove != null) {
         synchronized (this.getLockObject()) {
            long UID = this.getKey(objectToRemove);
            Object objectRemoved = this.get(UID);
            this._cal._dirty = true;
            this._cal._calElements.remove(UID);
            this._recurCache.clear(UID);
            this._calData.commit();
            if (!this._notificationsSuspended && objectRemoved != null) {
               this._notifierSort.fireElementRemoved(this, objectRemoved);
               this._notifierExternal.fireElementRemoved(this, objectRemoved);
               this._notifierLowPriorityListeners.fireElementRemoved(this, objectRemoved);
            }

            if (objectToRemove != null) {
               this._otaSyncDataManager.remove((OTASyncIDProvider)objectToRemove);
            }

            if (objectRemoved != null) {
               this._otaSyncDataManager.remove((OTASyncIDProvider)objectRemoved);
            }
         }
      }
   }

   @Override
   public void removePrior(long time, TimeZone tz) {
      this.removePrior(time, tz, 0);
   }

   @Override
   public void removePrior(long time, TimeZone tz, int calendarID) {
      int low = 0;
      int high = this._endTimeSort.size() - 1;
      long endTime = 0;
      boolean notifyForGC = false;

      while (low <= high) {
         int mid = (low + high) / 2;
         Event e = (Event)this._endTimeSort.getAt(mid);
         if (e.isRecurring()) {
            Recur r = e.getRecurrenceCopy();
            if (r.isFinite()) {
               endTime = r.getEndDate();
            } else {
               endTime = Long.MAX_VALUE;
            }
         } else {
            endTime = e.getStart(tz) + e.getDuration(tz);
         }

         if (endTime <= time) {
            low = mid + 1;
         } else {
            high = mid - 1;
         }
      }

      if (low > 10) {
         notifyForGC = true;
      }

      while (low-- > 0) {
         Event e = (Event)this._endTimeSort.getAt(low);
         this.remove(e);
      }

      if (notifyForGC) {
         Memory.persistentGC();
      }
   }

   @Override
   public void resumeNotification(Object context) {
      synchronized (this.getLockObject()) {
         this._notificationsSuspended = false;
         this._notifierSort.fireReset(this);
         this._notifierExternal.fireReset(this);
         this._notifierLowPriorityListeners.fireReset(this);
      }
   }

   @Override
   public void suspendNotification(Object context) {
      synchronized (this.getLockObject()) {
         this._notificationsSuspended = true;
      }
   }

   @Override
   public boolean isDirty() {
      synchronized (this.getLockObject()) {
         return this._cal._dirty;
      }
   }

   @Override
   public void markClean() {
      synchronized (this.getLockObject()) {
         this._cal._dirty = false;
         this._calData.commit();
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      LongHashtableCollection calElements = this._cal._calElements;
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         ticket.hashCode();
      }

      SyncManager syncManager = SyncManager.getInstance();
      boolean recrypt = false;

      while (true) {
         synchronized (this.getLockObject()) {
            LongEnumeration keys = calElements.getKeys();

            while (keys.hasMoreElements()) {
               if (generation != PersistentContent.getModeGeneration()) {
                  EventLogger.logEvent(-256469206327664059L, 1128481090, 0);
                  return;
               }

               if (DeviceInfo.getIdleTime() < 5 || syncManager.isSerialSyncInProgress()) {
                  EventLogger.logEvent(-256469206327664059L, 1128877652, 5);
                  break;
               }

               long key = keys.nextElement();
               Object event = calElements.get(key);
               if (event instanceof Object) {
                  EncryptableProvider encryptable = (EncryptableProvider)event;
                  if (!encryptable.checkCrypt(true, true)) {
                     recrypt = true;
                     encryptable = (EncryptableProvider)ObjectGroup.expandGroup(encryptable);
                     encryptable.reCrypt(true, true);

                     label147:
                     try {
                        ObjectGroup.createGroup(encryptable);
                     } finally {
                        break label147;
                     }

                     calElements.put(key, encryptable);
                  }
               }
            }

            if (!keys.hasMoreElements()) {
               if (recrypt) {
                  this._recurCache.clearAll();
                  this._nonRecurringStartTimeSort.loadFrom(this._cal._calElements);
                  this._recurringStartTimeSort.loadFrom(this._cal._calElements);
                  this._endTimeSort.loadFrom(this._cal._calElements);
                  this._durationSort.loadFrom(this._cal._calElements);
                  this._eventsSortedByStartDate.loadFrom(this);
                  EventLogger.logEvent(-256469206327664059L, 1128481861, 0);
               } else {
                  EventLogger.logEvent(-256469206327664059L, 1128484421, 0);
               }

               return;
            }
         }

         try {
            Thread.sleep(1000);
         } finally {
            continue;
         }
      }
   }

   @Override
   public String getProviderName() {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      return rb.getString(632);
   }

   @Override
   public long getProviderID() {
      return this._calendarServiceID;
   }

   @Override
   public int getEventCountBeforeTime(long time, TimeZone tz) {
      int beforeCount = 0;
      int count = this.getNearestNonRecurring(time, tz);
      count += this.checkForRecurrences(time, tz, false);
      return beforeCount;
   }

   @Override
   public int getEventCountAfterTime(long time, TimeZone tz) {
      int afterCount = 0;
      int numNonRecurring = this._nonRecurringStartTimeSort.size();
      int count = numNonRecurring - this.getNearestNonRecurring(time, tz);
      count += this.checkForRecurrences(time, tz, true);
      return afterCount;
   }

   private int checkForRecurrences(long time, TimeZone tz, boolean after) {
      int numRecurring = this._recurringStartTimeSort.size();
      int recurringIndex = 0;
      int count = 0;

      while (recurringIndex < numRecurring) {
         DurationParts dpc = (DurationParts)this._recurringStartTimeSort.getAt(recurringIndex);
         if (!after) {
            if (dpc.getPrevFromTime(time, tz) != null) {
               count++;
            }
         } else if (dpc.getNextFromTime(time, tz) != null) {
            count++;
         }

         recurringIndex++;
      }

      return count;
   }
}
