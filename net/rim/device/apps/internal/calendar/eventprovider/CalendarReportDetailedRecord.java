package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.synchronization.SyncObject;

public class CalendarReportDetailedRecord implements SyncObject {
   Object _o;
   private CalendarReportDetailedRecord _parent;

   public Object getParentObject() {
      return this._parent != null ? this._parent : null;
   }

   @Override
   public int getUID() {
      if (!(this._o instanceof Object)) {
         if (!(this._o instanceof EventPart)) {
            return 0;
         }

         EventPart ep = (EventPart)this._o;
         EventImpl event = ep._event;
         return event.getUID();
      } else {
         SyncObject so = (SyncObject)this._o;
         return so.getUID();
      }
   }

   public CalendarReportDetailedRecord(Object o) {
      this._o = o;
      if (this._o instanceof EventPart) {
         EventPart ep = (EventPart)this._o;
         this._parent = new CalendarReportDetailedRecord(ep._event);
      }
   }
}
