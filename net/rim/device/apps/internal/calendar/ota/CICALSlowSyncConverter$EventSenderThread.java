package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;

class CICALSlowSyncConverter$EventSenderThread extends Thread {
   CICALSlowSyncEvent _request;
   private final CICALSlowSyncConverter this$0;

   CICALSlowSyncConverter$EventSenderThread(CICALSlowSyncConverter _1, CICALSlowSyncEvent request) {
      this.this$0 = _1;
      this._request = request;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      if (this._request != null && this._request.getType() == 31) {
         CalendarService calendarService = (CalendarService)this._request.getServiceIdentifier();
         Object ticket = PersistentContent.waitForTicket();
         ticket.hashCode();
         byte[] temp = this._request.getCommandData();
         if (temp != null) {
            DataBuffer eventBuffer = new DataBuffer(temp, 0, temp.length, true);
            CalDB cal = calendarService.getCalendarDatabase();

            while (true) {
               boolean var11 = false /* VF: Semaphore variable */;

               try {
                  var11 = true;
                  if (eventBuffer.eof()) {
                     var11 = false;
                     break;
                  }

                  byte fieldId = eventBuffer.readByte();
                  int length = eventBuffer.readCompressedInt();
                  switch (fieldId) {
                     case 2:
                        int uid = eventBuffer.readInt();
                        Object o = cal.get(EventUtilities.makeLUID(uid));
                        if (o instanceof Event) {
                           this.this$0.transmitCICALEvent(calendarService, o);
                        }

                        this.this$0.decrementOutgoingQueue(calendarService, this._request.getSessionID());
                        break;
                     default:
                        eventBuffer.skipBytes(length);
                  }
               } finally {
                  if (var11) {
                     CICALEventLogger.logEvent(1397052229, 2);
                     this.this$0.resetOutgoingQueue(calendarService, this._request.getSessionID(), true);
                     break;
                  }
               }
            }
         }

         ticket = null;
         if (this.this$0._slowSyncDoneForced) {
            CICALEventLogger.logEvent(1397704259, 0);
            this.this$0.resetOutgoingQueue(calendarService, this._request.getSessionID(), false);
            if (this.this$0.checkIfSlowSyncCompleted(calendarService)) {
               this.this$0.processNextSlowSync();
            }
         }
      }
   }
}
