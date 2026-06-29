package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.synchronization.SyncObject;

public class CalendarReportTimeRecord implements SyncObject {
   int _startDate;
   int _endDate;

   public CalendarReportTimeRecord(int start, int end) {
      this._startDate = start;
      this._endDate = end;
   }

   @Override
   public int getUID() {
      return 0;
   }
}
