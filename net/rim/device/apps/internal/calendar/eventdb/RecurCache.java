package net.rim.device.apps.internal.calendar.eventdb;

import java.util.TimeZone;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.controller.DurationParts;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.vm.Array;

class RecurCache {
   private LongHashtable _cacheEntries = (LongHashtable)(new Object());
   private RecurCache$BuildCacheHelper _buildCacheHelper = new RecurCache$BuildCacheHelper();

   void getOccurrencesIn(long start, long duration, TimeZone tz, DurationParts root, long UID, Object[] occurrences) {
      Array.resize(occurrences, 0);
      if (root != null && root.hasParts()) {
         RecurCache$CacheEntry ce = (RecurCache$CacheEntry)this._cacheEntries.get(UID);
         if (ce == null || ce._tzid == null || !ce._tzid.equals(tz.getID())) {
            ce = this.buildCache(start, duration, tz, root, UID);
            this._cacheEntries.put(UID, ce);
         }

         if (ce._numOccurrences > 0) {
            boolean noEvents = false;
            long end = start + duration;
            if (end <= ce._startTimes[0]) {
               if (ce._moreAvailableEarlier) {
                  ce = this.buildCache(start, duration, tz, root, UID);
                  this._cacheEntries.put(UID, ce);
               } else {
                  noEvents = true;
               }
            }

            if (start >= ce._endTimes[ce._numOccurrences - 1] && start > ce._startTimes[ce._numOccurrences - 1]) {
               if (ce._moreAvailableLater) {
                  ce = this.buildCache(start, duration, tz, root, UID);
                  this._cacheEntries.put(UID, ce);
               } else {
                  noEvents = true;
               }
            }

            if (!noEvents) {
               RIMModel[] occurrencesToExamine = ce._occurrences;
               long[] starts = ce._startTimes;
               long[] ends = ce._endTimes;
               int numOccurrences = ce._numOccurrences;
               int startIndex = 0;

               while (startIndex < numOccurrences && start >= ends[startIndex] && start > starts[startIndex]) {
                  startIndex++;
               }

               boolean lookEarlier = startIndex == 0 && ce._moreAvailableEarlier;
               int endIndex = numOccurrences - 1;

               while (endIndex >= 0 && end <= starts[endIndex]) {
                  endIndex--;
               }

               boolean lookLater = endIndex == numOccurrences - 1 && ce._moreAvailableLater;
               if (startIndex <= endIndex && startIndex >= 0 && startIndex < numOccurrences && endIndex >= 0 && endIndex < numOccurrences) {
                  if (lookEarlier) {
                     RIMModel po = (RIMModel)root.getPrev(occurrencesToExamine[startIndex]);
                     if (po instanceof Object) {
                        Duration poDuration = (Duration)po;
                        long startOfEvent = poDuration.getStart(tz);
                        long endOfEvent = startOfEvent + poDuration.getDuration(tz);

                        while (endOfEvent > start || startOfEvent >= start) {
                           int max = occurrences.length;
                           Array.resize(occurrences, max + 1);
                           occurrences[max] = po;
                           po = (RIMModel)root.getPrev(po);
                           if (po == null) {
                              break;
                           }

                           poDuration = (Duration)po;
                           startOfEvent = poDuration.getStart(tz);
                           endOfEvent = startOfEvent + poDuration.getDuration(tz);
                        }
                     }
                  }

                  if (lookLater) {
                     RIMModel po = (RIMModel)root.getNext(occurrencesToExamine[endIndex]);
                     if (po instanceof Object) {
                        Duration poDuration = (Duration)po;
                        long startOfEvent = poDuration.getStart(tz);

                        while (startOfEvent < end) {
                           int max = occurrences.length;
                           Array.resize(occurrences, max + 1);
                           occurrences[max] = po;
                           po = (RIMModel)root.getNext(po);
                           if (po == null) {
                              break;
                           }

                           poDuration = (Duration)po;
                           startOfEvent = poDuration.getStart(tz);
                        }
                     }
                  }

                  int i = occurrences.length;
                  int max = endIndex - startIndex + 1;
                  Array.resize(occurrences, i + max);
                  System.arraycopy(occurrencesToExamine, startIndex, occurrences, i, max);
               }
            }
         }
      }
   }

   void clear(long UID) {
      this._cacheEntries.remove(UID);
   }

   void clearAll() {
      this._cacheEntries = (LongHashtable)(new Object());
   }

   private RecurCache$CacheEntry buildCache(long start, long duration, TimeZone tz, DurationParts root, long UID) {
      RecurCache$CacheEntry ce = new RecurCache$CacheEntry();
      this._buildCacheHelper.buildCache(ce, start, duration, tz, root);
      return ce;
   }
}
