package net.rim.device.apps.api.messaging.messagelist;

import java.util.Calendar;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListenerWithHint;
import net.rim.device.api.collection.FilterProgress;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.NotificationSuspension;
import net.rim.device.api.collection.OrderedList;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.ui.DialogWithBackgroundThread;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;
import net.rim.vm.Monitor;

public final class DateSortedSeparatedMessageArray
   implements ChainableCollection,
   ReadableList,
   LoadableCollection,
   NotificationSuspension,
   IntRangedActionTarget,
   FilterProgress,
   CollectionListenerWithHint {
   private CollectionListenerManager _collectionListenerManager;
   private ReadableList _dateSortedItems;
   private int _dateSortedItemsSize;
   protected Calendar _calendar;
   protected int[] _rangeStartIndex;
   protected int[] _rangeEndIndex;
   protected long[] _rangeDate;
   protected short[] _rangeDoy;
   protected int _rangeLen;
   protected int _rangeDirection;
   private NoMessagesBar _noMessagesBar;
   private LongKeyProviderAdaptor _longKeyProviderAdaptor;
   DialogWithBackgroundThread _dialogWithBackgroundThread = (DialogWithBackgroundThread)(new Object());
   DateSortedSeparatedMessageArray$ApplyRunnable _applyRunnable = new DateSortedSeparatedMessageArray$ApplyRunnable(this);
   private static final boolean CHECK_ASSERT = false;
   private static final int ASCENDING_DIRECTION = 1;
   private static final int DESCENDING_DIRECTION = -1;
   private static final int INITIAL_RANGE_SIZE = 64;
   private static final int RANGE_SIZE_GROWTH = 64;
   private static final long EVENT_LOGGER_ID = 4720449521217587723L;
   public static final long FLAG_DISPLAY_POPUP_SCREEN_DURING_BULK_MARK_OLD = 2800326993345467839L;
   private static final long TWO_DAYS_IN_MS = 172800000L;

   @Override
   public final void loadFrom(Object collection) {
      synchronized (FolderHierarchies.getLockObject()) {
         this.setItems(collection);
      }
   }

   @Override
   public final void apply(int lowValue, int highValue, long action, Object context) {
      this._applyRunnable.setValues(lowValue, highValue, action, context);
      String dialogLabel = null;
      if (action == -6225946334564270161L) {
         dialogLabel = MessageResources.getString(64);
      } else if (action == -3967872215949752466L) {
         dialogLabel = MessageResources.getString(66);
      } else {
         dialogLabel = MessageResources.getString(68);
      }

      this._dialogWithBackgroundThread.initialize(dialogLabel, this._applyRunnable, this.displayPopupScreen(context));
      this._dialogWithBackgroundThread.run();
   }

   @Override
   public final int getProgress() {
      ReadableList dateSortedItems = this._dateSortedItems;
      if (!(dateSortedItems instanceof Object)) {
         return 100;
      }

      FilterProgress fp = (FilterProgress)dateSortedItems;
      return fp.getProgress();
   }

   public final int getPreviousDateIndex(int index) {
      synchronized (FolderHierarchies.getLockObject()) {
         int size = this.size();
         if (size <= 1) {
            return 0;
         }

         if (index >= size) {
            index = size - 1;
         }

         int invertedIndex = this.invertSepIndex(index);
         int dateRangeIx = this.getDateRangeContainingSeparatedIndex(invertedIndex);
         int returnIx;
         if (dateRangeIx != this.earliestDateRange()) {
            dateRangeIx -= this._rangeDirection;
            returnIx = this.rangeIndexToSeparatedIndex(dateRangeIx);
         } else {
            returnIx = this.invertSepIndex(this.endOfSegmentPartOfRange(dateRangeIx));
         }

         return returnIx;
      }
   }

   public final int getNextDateIndex(int index) {
      synchronized (FolderHierarchies.getLockObject()) {
         int size = this.size();
         if (size <= 1) {
            return 0;
         }

         if (index >= size) {
            index = size - 1;
         }

         int invertedIndex = this.invertSepIndex(index);
         int dateRangeIx = this.getDateRangeContainingSeparatedIndex(invertedIndex);
         if (dateRangeIx != this.latestDateRange() && invertedIndex == this.dateSepPartOfRange(dateRangeIx)) {
            dateRangeIx += this._rangeDirection;
         }

         return this.rangeIndexToSeparatedIndex(dateRangeIx);
      }
   }

   final void timezoneChanged() {
      this._calendar = Calendar.getInstance();
      synchronized (FolderHierarchies.getLockObject()) {
         this.reset(null);
      }
   }

   public final int getIndex(long targetDate, boolean exactMatch) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._dateSortedItemsSize == 0) {
            return -1;
         }

         int minIndex = 0;
         int maxIndex = this.size();
         int bestIndex = 0;
         long bestDiff = Long.MAX_VALUE;

         while (maxIndex > minIndex) {
            int midIndex = (maxIndex + minIndex) / 2;
            Object midObject = this.getAt(midIndex);
            long midDate = this._longKeyProviderAdaptor.getLongKey(midObject);
            long midDiff = midDate - targetDate;
            if (midDiff > 0) {
               if (bestDiff > midDiff) {
                  bestIndex = midIndex;
                  bestDiff = midDiff;
               }

               minIndex = midIndex + 1;
            } else {
               if (midDiff >= 0) {
                  return midIndex;
               }

               if (bestDiff > -midDiff) {
                  bestIndex = midIndex;
                  bestDiff = -midDiff;
               }

               maxIndex = midIndex;
            }
         }

         if (!exactMatch) {
            if (maxIndex != bestIndex && maxIndex < this.size()) {
               long maxDiff = targetDate - this._longKeyProviderAdaptor.getLongKey(this.getAt(maxIndex));
               if (bestDiff > maxDiff) {
                  bestIndex = maxIndex;
               }
            }

            return bestIndex;
         } else {
            return -1;
         }
      }
   }

   protected final void assertHaveFolderLock() {
      if (!Monitor.monitorOwned(FolderHierarchies.getLockObject())) {
         try {
            throw new Object("DSSMA modified outside of FolderHierarchies lock.");
         } finally {
            return;
         }
      }
   }

   @Override
   public final int size() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._dateSortedItemsSize != 0 && this._dateSortedItems.size() < this._dateSortedItemsSize) {
            this.handleCriticalError("Size Failed", 0, 0, this._dateSortedItems.size(), 0, true);
         }

         return this._rangeLen == 0 ? 1 : this._dateSortedItemsSize + this._rangeLen;
      }
   }

   @Override
   public final int getAt(int offset, int length, Object[] values, int j) {
      synchronized (FolderHierarchies.getLockObject()) {
         return ReadableListUtil.getAt(offset, length, values, j, this);
      }
   }

   @Override
   public final int getIndex(Object item) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (item instanceof DateSeparator) {
            return -1;
         }

         long itemDate = this._longKeyProviderAdaptor.getLongKey(item);
         int index = this.getIndex(itemDate, true);
         if (index < 0) {
            return -1;
         }

         int unseparatedIndex = this.getUnseparatedIndexOnOrAfter(index);
         int originalIndex = index;
         int originalUnseparatedIndex = unseparatedIndex;

         while (unseparatedIndex < this._dateSortedItemsSize) {
            Object itemAtIndex = this._dateSortedItems.getAt(unseparatedIndex);
            if (itemAtIndex == item) {
               return index;
            }

            if (this._longKeyProviderAdaptor.getLongKey(itemAtIndex) != itemDate) {
               break;
            }

            index++;
            unseparatedIndex++;
         }

         index = originalIndex - 1;

         for (int var13 = originalUnseparatedIndex - 1; var13 >= 0; var13--) {
            Object itemAtIndex = this._dateSortedItems.getAt(var13);
            if (itemAtIndex == item) {
               return index;
            }

            if (this._longKeyProviderAdaptor.getLongKey(itemAtIndex) != itemDate) {
               break;
            }

            index--;
         }

         return -1;
      }
   }

   @Override
   public final void removeCollectionListener(Object callback) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.removeCollectionListener(callback);
      }
   }

   @Override
   public final Object getAt(int offset) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (offset == 0 && this._rangeLen == 0) {
            return this._noMessagesBar;
         }

         int newOffset = this.invertSepIndex(offset);
         int dateRangeIx = this.getDateRangeContainingSeparatedIndex(newOffset);
         Object ret;
         if (this.dateSepPartOfRange(dateRangeIx) == newOffset) {
            ret = new DateSeparator(this._rangeDate[dateRangeIx]);
         } else {
            ret = this._dateSortedItems.getAt(this.getUnseparatedArrayIndexForSeparatedIndex(dateRangeIx, newOffset));
         }

         return ret;
      }
   }

   @Override
   public final void reset(Collection collection) {
      this.reset(collection, null);
   }

   @Override
   public final void reset(Collection collection, Object hint) {
      if (ContextObject.getFlag(hint, 62) || this.setItems(this._dateSortedItems)) {
         this._collectionListenerManager.fireReset(this);
      }
   }

   @Override
   public final void addCollectionListener(Object callback) {
      synchronized (FolderHierarchies.getLockObject()) {
         this._collectionListenerManager.addCollectionListener(callback);
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.assertHaveFolderLock();
      this.doAddElement(element);
      this._collectionListenerManager.fireElementAdded(this, element);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.assertHaveFolderLock();
      boolean fire_update = true;
      if (oldElement != newElement) {
         if (this.doRemoveElement(oldElement)) {
            this.doAddElement(newElement);
         } else {
            fire_update = false;
         }
      }

      if (fire_update) {
         this._collectionListenerManager.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.assertHaveFolderLock();
      if (this.doRemoveElement(element)) {
         this._collectionListenerManager.fireElementRemoved(this, element);
      }
   }

   @Override
   public final void suspendNotification(Object context) {
      this.assertHaveFolderLock();
      if (this._dateSortedItems instanceof Object) {
         ((NotificationSuspension)this._dateSortedItems).suspendNotification(context);
      }
   }

   @Override
   public final void resumeNotification(Object context) {
      this.assertHaveFolderLock();
      if (this._dateSortedItems instanceof Object) {
         ((NotificationSuspension)this._dateSortedItems).resumeNotification(context);
      }

      this.reset(this, context);
   }

   public DateSortedSeparatedMessageArray(int noMessagesStringId, LongKeyProviderAdaptor longKeyProviderAdaptor) {
      this._longKeyProviderAdaptor = longKeyProviderAdaptor;
      this._calendar = Calendar.getInstance();
      this._noMessagesBar = new NoMessagesBar(noMessagesStringId);
      this._collectionListenerManager = (CollectionListenerManager)(new Object());
      Application app = Application.getApplication();
      app.addGlobalEventListener(new DSSMAGlobalEventListener(this, app));
   }

   private final void handleCriticalError(String reason, long date, int doy, int extraInt, long extraLong, boolean attemptEmergencyRecovery) {
      StringBuffer sb = (StringBuffer)(new Object(reason));
      sb.append(": date = ");
      sb.append(date);
      sb.append(", doy = ");
      sb.append(doy);
      sb.append(", extraInt = ");
      sb.append(extraInt);
      sb.append(", extraLong = ");
      sb.append(extraLong);
      sb.append(", _rangeLen = ");
      sb.append(this._rangeLen);
      sb.append(", _dateSortedItemsSize");
      sb.append(this._dateSortedItemsSize);
      if (!Monitor.monitorOwned(FolderHierarchies.getLockObject())) {
         sb.append(", NO FolderHierarchies lock");
      }

      EventLogger.logEvent(4720449521217587723L, sb.toString().getBytes(), 1);

      try {
         throw new Object("DSSMA Forced Stack Trace");
      } finally {
         if (attemptEmergencyRecovery) {
            this.reset(null);
         }

         QuincyManager.sendUncaughtException("DSSMA");
         return;
      }
   }

   private final void badState(int id, int index) {
      StringBuffer sb = (StringBuffer)(new Object("DSSMA Bad State: "));
      sb.append(id);
      sb.append(", Size=");
      sb.append(this._dateSortedItems.size());
      sb.append(", Index=");
      sb.append(index);
      if (index < this._dateSortedItems.size()) {
         RIMModel model = (RIMModel)this._dateSortedItems.getAt(index);
         long date = this._longKeyProviderAdaptor.getLongKey(model);
         sb.append(", Date=");
         sb.append(date);
         sb.append(", Model=");
         sb.append(model.getClass().getName());
      }

      EventLogger.logEvent(4720449521217587723L, sb.toString().getBytes(), 1);
      throw new Object(((StringBuffer)(new Object("DSSMA: invalid state("))).append(id).append("): ").append(index).toString());
   }

   private final int invertSepIndex(int separator_index) {
      if (this._rangeDirection > 0) {
         separator_index = this.size() - 1 - separator_index;
      }

      return separator_index;
   }

   private static final int signLong(long v) {
      if (v < 0) {
         return -1;
      } else {
         return v > 0 ? 1 : 0;
      }
   }

   private static final int cmpDateWithDoy(long d1, short doy1, long d2, short doy2) {
      long diff = d1 - d2;
      long abs_diff = diff >= 0 ? diff : -diff;
      return abs_diff < 172800000 && doy1 == doy2 ? 0 : signLong(diff);
   }

   private final int latestDateRange() {
      return this._rangeDirection > 0 ? this._rangeLen - 1 : 0;
   }

   private final int earliestDateRange() {
      return this._rangeDirection > 0 ? 0 : this._rangeLen - 1;
   }

   private final int endOfSegmentPartOfRange(int date_range_idx) {
      int[] rangeBound;
      int adjust;
      if (this._rangeDirection > 0) {
         rangeBound = this._rangeStartIndex;
         adjust = 0;
      } else {
         rangeBound = this._rangeEndIndex;
         adjust = 1;
      }

      return rangeBound[date_range_idx] + date_range_idx + adjust;
   }

   private final int dateSepPartOfRange(int date_range_idx) {
      int[] rangeBound;
      int adjust;
      if (this._rangeDirection > 0) {
         rangeBound = this._rangeEndIndex;
         adjust = 1;
      } else {
         rangeBound = this._rangeStartIndex;
         adjust = 0;
      }

      return rangeBound[date_range_idx] + date_range_idx + adjust;
   }

   private final void extendCurrentRange(int offset, int new_idx) {
      int end_idx = this._rangeEndIndex[offset];
      if (new_idx > end_idx && new_idx - 1 == end_idx && offset == this._rangeLen - 1) {
         this._rangeEndIndex[offset] = new_idx;
      } else {
         this.badState(1, new_idx);
      }
   }

   private final int getUnseparatedIndexOnOrBefore(int separatedIndex) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (separatedIndex == 0) {
            return -1;
         }

         int reversedIndex = this.invertSepIndex(separatedIndex);
         int dateRangeIndex = this.getDateRangeContainingSeparatedIndex(reversedIndex);
         return this.getUnseparatedArrayIndexForSeparatedIndex(dateRangeIndex, reversedIndex);
      }
   }

   private final int getUnseparatedIndexOnOrAfter(int separatedIndex) {
      synchronized (FolderHierarchies.getLockObject()) {
         int reversedIndex = this.invertSepIndex(separatedIndex);
         int dateRangeIndex = this.getDateRangeContainingSeparatedIndex(reversedIndex);
         int retIndex = this.getUnseparatedArrayIndexForSeparatedIndex(dateRangeIndex, reversedIndex);
         if (this.dateSepPartOfRange(dateRangeIndex) == reversedIndex) {
            retIndex -= this._rangeDirection;
         }

         return retIndex;
      }
   }

   private final int ensureRoomForOneMore() {
      int len = this._rangeLen++;
      if (len >= this._rangeDate.length) {
         this.growDateRanges();
      }

      return len;
   }

   private final void addRangeToEnd(int index, short doy, long date) {
      int i = this.ensureRoomForOneMore();
      this._rangeStartIndex[i] = index;
      this._rangeEndIndex[i] = index;
      this._rangeDate[i] = date;
      this._rangeDoy[i] = doy;
   }

   private final boolean setItems(Object o) {
      if (!(o instanceof Object)) {
         return false;
      }

      ReadableList collection = (ReadableList)o;
      this._dateSortedItems = collection;
      this._dateSortedItemsSize = collection.size();
      this.findRangeBoundaries();
      return true;
   }

   private final void copyAllArrays(int src, int dest, int num) {
      System.arraycopy(this._rangeStartIndex, src, this._rangeStartIndex, dest, num);
      System.arraycopy(this._rangeEndIndex, src, this._rangeEndIndex, dest, num);
      System.arraycopy(this._rangeDate, src, this._rangeDate, dest, num);
      System.arraycopy(this._rangeDoy, src, this._rangeDoy, dest, num);
   }

   private final void addRangeAnywhere(int date_idx, long date, short day_of_year) {
      int[] rangeStart = this._rangeStartIndex;
      int[] rangeEnd = this._rangeEndIndex;
      int date_num = this.ensureRoomForOneMore();
      if (date_idx >= date_num) {
         rangeStart[date_idx] = rangeEnd[date_idx] = this._dateSortedItemsSize;
         this._rangeDate[date_idx] = date;
         this._rangeDoy[date_idx] = day_of_year;
      } else {
         int amt_to_copy = date_num - date_idx;
         if (amt_to_copy != 0) {
            this.copyAllArrays(date_idx, date_idx + 1, amt_to_copy);
         }

         rangeEnd[date_idx] = rangeStart[date_idx];
         this._rangeDate[date_idx] = date;
         this._rangeDoy[date_idx] = day_of_year;
         date_idx++;
         rangeStart[date_idx]++;
         this.addOneToRange(date_idx);
      }
   }

   private final void addOneToRange(int date_idx) {
      int[] rangeStart = this._rangeStartIndex;
      int[] rangeEnd = this._rangeEndIndex;
      int date_num = this._rangeLen;
      rangeEnd[date_idx]++;

      while (++date_idx < date_num) {
         rangeStart[date_idx]++;
         rangeEnd[date_idx]++;
      }
   }

   private final void removeOneFromRange(int date_idx) {
      int[] rangeStart = this._rangeStartIndex;
      int[] rangeEnd = this._rangeEndIndex;
      int date_num = this._rangeLen;
      int start = rangeStart[date_idx];
      int end = rangeEnd[date_idx];
      if (start > --end) {
         int amt_to_copy = date_num - date_idx - 1;
         if (amt_to_copy != 0) {
            this.copyAllArrays(date_idx + 1, date_idx, amt_to_copy);
         }

         this._rangeLen = --date_num;
      } else {
         rangeEnd[date_idx] = end;
         date_idx++;
      }

      while (date_idx < date_num) {
         rangeStart[date_idx]--;
         rangeEnd[date_idx]--;
         date_idx++;
      }
   }

   private final void doAddElement(Object element) {
      long date = 0;
      short day_of_year = 0;

      try {
         RIMModel m = (RIMModel)element;
         date = this._longKeyProviderAdaptor.getLongKey(m);
         day_of_year = this.extractDOYFromDate(date);
         int end_index = this._dateSortedItemsSize;
         if (element != this._dateSortedItems.getAt(end_index) || !this.addToEnd(end_index, date, day_of_year)) {
            int date_idx = this.getDateRangeContainingSubElement(element, date, day_of_year);
            if (date_idx >= 0) {
               this.addOneToRange(date_idx);
            } else {
               this.addRangeAnywhere(-date_idx - 1, date, day_of_year);
            }
         }

         this._dateSortedItemsSize++;
      } finally {
         this.handleCriticalError(
            ((StringBuffer)(new Object("Add Failed ("))).append(element.getClass().getName()).append(")").toString(), date, day_of_year, 0, 0, true
         );
         return;
      }
   }

   private final boolean doRemoveElement(Object element) {
      boolean found = false;
      RIMModel m = (RIMModel)element;
      long date = this._longKeyProviderAdaptor.getLongKey(m);
      short day_of_year = this.extractDOYFromDate(date);
      int date_idx = this.getDateRangeContainingSubElement(element, date, day_of_year);
      if (date_idx >= 0) {
         this.removeOneFromRange(date_idx);
         this._dateSortedItemsSize--;
         return true;
      } else {
         this.handleCriticalError(
            ((StringBuffer)(new Object("Remove Failed ("))).append(element.getClass().getName()).append(")").toString(), date, day_of_year, date_idx, 0, true
         );
         return found;
      }
   }

   private final short extractDOYFromDate(long date) {
      ((CalendarExtensions)this._calendar).setTimeLong(date);
      return (short)this._calendar.get(6);
   }

   private final long findDayLimit(boolean want_end) {
      this._calendar.set(11, want_end ? 23 : 0);
      this._calendar.set(12, want_end ? 59 : 0);
      this._calendar.set(13, want_end ? 59 : 0);
      this._calendar.set(14, want_end ? 999 : 0);
      return ((CalendarExtensions)this._calendar).getTimeLong();
   }

   private final void findRangeBoundaries() {
      synchronized (FolderHierarchies.getLockObject()) {
         this.initializeDateRanges();
         long day_start = Long.MAX_VALUE;
         long day_end = Long.MIN_VALUE;
         short curr_doy = 32767;
         int n = this._dateSortedItemsSize;

         for (int i = 0; i < n; i++) {
            RIMModel model = (RIMModel)this._dateSortedItems.getAt(i);
            long date = this._longKeyProviderAdaptor.getLongKey(model);
            if (date < day_start) {
               short next_doy = this.extractDOYFromDate(date);
               long next_start = this.findDayLimit(false);
               if (next_doy + 1 == curr_doy && day_start - next_start < 172800000) {
                  day_end = day_start - 1;
               } else {
                  day_end = this.findDayLimit(true);
               }

               day_start = next_start;
               curr_doy = next_doy;
            } else if (date > day_end) {
               short next_doy = this.extractDOYFromDate(date);
               long next_end = this.findDayLimit(true);
               if (next_doy - 1 == curr_doy && next_end - day_end < 172800000) {
                  day_start = day_end + 1;
               } else {
                  day_start = this.findDayLimit(false);
               }

               day_end = next_end;
               curr_doy = next_doy;
            }

            if (!this.addToEnd(i, date, curr_doy)) {
               this.badState(5, i);
            }
         }
      }
   }

   private final void initializeDateRanges() {
      if (this._rangeStartIndex == null || this._rangeStartIndex.length < 64) {
         int len = 64;
         this._rangeStartIndex = new int[len];
         this._rangeEndIndex = new int[len];
         this._rangeDate = new long[len];
         this._rangeDoy = new short[len];
      }

      this._rangeLen = 0;
      this._rangeDirection = 1;
      if (this._dateSortedItems instanceof Object) {
         OrderedList ol = (OrderedList)this._dateSortedItems;
         if (!ol.isAscending()) {
            this._rangeDirection = -1;
         }
      }
   }

   private final void growDateRanges() {
      int new_len = this._rangeDate.length + 64;
      Array.resize(this._rangeStartIndex, new_len);
      Array.resize(this._rangeEndIndex, new_len);
      Array.resize(this._rangeDate, new_len);
      Array.resize(this._rangeDoy, new_len);
   }

   private final int getDateRangeContainingSeparatedIndex(int separated_index) {
      int hi = this._rangeLen - 1;
      int lo = 0;

      while (lo <= hi) {
         int mid = lo + hi >>> 1;
         int begin_range = this._rangeStartIndex[mid] + mid;
         if (separated_index < begin_range) {
            hi = mid - 1;
         } else {
            int end_range = this._rangeEndIndex[mid] + mid + 1;
            if (separated_index <= end_range) {
               return mid;
            }

            lo = mid + 1;
         }
      }

      throw new Object();
   }

   private final int getDateRangeContainingSubElement(Object sub_element, long date, short day_of_year) {
      int hi = this._rangeLen - 1;
      int lo = 0;

      while (lo <= hi) {
         int mid = lo + hi >>> 1;
         int cmp_range = cmpDateWithDoy(date, day_of_year, this._rangeDate[mid], this._rangeDoy[mid]);
         if (this._rangeDirection < 0) {
            cmp_range = -cmp_range;
         }

         if (cmp_range < 0) {
            hi = mid - 1;
         } else {
            if (cmp_range <= 0) {
               return mid;
            }

            lo = mid + 1;
         }
      }

      return -(lo + 1);
   }

   private final int getUnseparatedArrayIndexForSeparatedIndex(int dateRangeIx, int separatedIndex) {
      int ret = separatedIndex - dateRangeIx;
      if (this._rangeDirection < 0) {
         ret--;
      }

      return ret;
   }

   private final int rangeIndexToSeparatedIndex(int rangeIndex) {
      return this.invertSepIndex(this.dateSepPartOfRange(rangeIndex));
   }

   private final boolean displayPopupScreen(Object context) {
      if (!(context instanceof Object)) {
         return true;
      }

      ContextObject contextObject = (ContextObject)context;
      Object value = contextObject.get(2800326993345467839L);
      return !(value instanceof Object) ? true : value;
   }

   private final boolean addToEnd(int index, long date, short doy) {
      int end = this._rangeLen - 1;
      if (end == -1) {
         this.addRangeToEnd(index, doy, date);
         return true;
      }

      long range_date = this._rangeDate[end];
      int cmp_date = signLong(date - range_date);
      if (cmp_date != 0) {
         if (end == 0) {
            if (cmp_date != this._rangeDirection) {
               if (this._rangeDirection != 1) {
                  this.handleCriticalError("Bad Order", date, doy, 0, range_date, false);
                  return false;
               }

               this._rangeDirection = cmp_date;
            }
         } else if (cmp_date != this._rangeDirection) {
            this.handleCriticalError("Bad Order", date, doy, 0, range_date, false);
            return false;
         }
      }

      int cmp_range = cmpDateWithDoy(date, doy, range_date, this._rangeDoy[end]);
      if (cmp_range == 0) {
         this.extendCurrentRange(end, index);
         return true;
      } else {
         this.addRangeToEnd(index, doy, date);
         return true;
      }
   }

   static {
      EventLogger.register(4720449521217587723L, "DSSMA", 2);
   }
}
