package net.rim.device.apps.internal.calendar.eventdb;

import java.util.TimeZone;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.controller.DurationParts;
import net.rim.device.apps.api.framework.model.RIMModel;

final class RecurCache$BuildCacheHelper {
   private static final long LOOK_BACK_ADJUSTMENT = 1209600000L;
   private static final long LOOK_FORWARD_ADJUSTMENT = 6048000000L;

   public RecurCache$BuildCacheHelper() {
   }

   public final void buildCache(RecurCache$CacheEntry ce, long start, long duration, TimeZone tz, DurationParts root) {
      RIMModel[] occurrences = ce._occurrences;
      long[] starts = ce._startTimes;
      long[] ends = ce._endTimes;
      long earliest = start - 1209600000;
      long latest = start + duration + 6048000000L;
      long middleTime = start + duration / 2;
      int middleOffset = 7;
      ce._tzid = tz.getID();
      RIMModel po = (RIMModel)root.getPrevFromTime(middleTime, tz);
      int numOccurrences = 0;

      for (int i = middleOffset; po != null && i >= 0; i--) {
         Duration poDuration = (Duration)po;
         numOccurrences++;
         occurrences[i] = po;
         starts[i] = poDuration.getStart(tz);
         ends[i] = starts[i] + poDuration.getDuration(tz);
         po = (RIMModel)root.getPrev(po);
         if (starts[i] <= earliest) {
            break;
         }
      }

      int copyStart = middleOffset + 1 - numOccurrences;
      System.arraycopy(occurrences, copyStart, occurrences, 0, numOccurrences);
      System.arraycopy(starts, copyStart, starts, 0, numOccurrences);
      System.arraycopy(ends, copyStart, ends, 0, numOccurrences);
      ce._moreAvailableEarlier = po != null;
      if (numOccurrences == 0) {
         po = (RIMModel)root.getNextFromTime(middleTime, tz);
      } else {
         po = (RIMModel)root.getNext(occurrences[numOccurrences - 1]);
      }

      int max = 14;

      for (int i = numOccurrences; po != null && i < max; i++) {
         Duration poDuration = (Duration)po;
         numOccurrences++;
         occurrences[i] = po;
         starts[i] = poDuration.getStart(tz);
         ends[i] = starts[i] + poDuration.getDuration(tz);
         po = (RIMModel)root.getNext(po);
         if (starts[i] >= latest) {
            break;
         }
      }

      ce._moreAvailableLater = po != null;
      ce._numOccurrences = numOccurrences;
   }
}
