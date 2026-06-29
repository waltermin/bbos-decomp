package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.Recur;

public class EventComparator implements Comparator {
   private TimeZone _tz;

   public void setTimeZone(TimeZone tz) {
      this._tz = tz;
   }

   @Override
   public int compare(Object o1, Object o2) {
      long start1 = this.getStartDate(o1);
      long start2 = this.getStartDate(o2);
      if (start1 < start2) {
         return -1;
      } else {
         return start1 > start2 ? 1 : 0;
      }
   }

   private long getStartDate(Object o) {
      if (this._tz == null) {
         this._tz = TimeZone.getDefault();
      }

      if (!(o instanceof EventPart)) {
         EventImpl e = (EventImpl)o;
         long start = e.getStart(this._tz);
         if (e.isRecurring()) {
            Recur r = e.getReadOnlyRecurrence();
            if (r.getInclusionCount() > 0) {
               long[] inclusionsList = r.getInclusions(null);
               if (start > inclusionsList[0]) {
                  start = inclusionsList[0];
               }
            }
         }

         return start;
      } else {
         EventPart ep = (EventPart)o;
         return ep.getStart(this._tz);
      }
   }
}
