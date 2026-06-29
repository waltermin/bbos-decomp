package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.vm.Array;

public class OTAMeetingListener$PendingMeetingResponseManager {
   private Object[][] _meetingResponses;
   private final OTAMeetingListener this$0;
   private static final long PENDING_MEETING_RESPONSE_MANAGER_GUID = -1037775804712219897L;

   protected OTAMeetingListener$PendingMeetingResponseManager(OTAMeetingListener _1) {
      this.this$0 = _1;
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1037775804712219897L);
      synchronized (persistentObject) {
         label32:
         try {
            this._meetingResponses = (Object[][])persistentObject.getContents();
         } finally {
            break label32;
         }

         if (this._meetingResponses == null) {
            this._meetingResponses = new Object[0][0];
            persistentObject.setContents(this._meetingResponses, 51);
            persistentObject.commit();
         }
      }
   }

   public void processAllPendingResponses() {
      synchronized (this._meetingResponses) {
         Object ticket = PersistentContent.getTicket();
         if (ticket != null) {
            CICALEventLogger.logEvent(1347439954, 4);
            boolean result = true;

            for (int index = 0; index < this._meetingResponses.length; index++) {
               Object[] events = this._meetingResponses[index];
               if (events != null) {
                  for (int i = 0; i < events.length; i++) {
                     result &= this.this$0.processMeetingResponse((Event)events[i], null, -1);
                  }
               }
            }

            Array.resize(this._meetingResponses, 0);
            this.commit();
            ticket = null;
         }
      }
   }

   public void add(Object[] events) {
      synchronized (this._meetingResponses) {
         Array.resize(this._meetingResponses, this._meetingResponses.length + 1);
         this._meetingResponses[this._meetingResponses.length - 1] = events;
         this.commit();
      }
   }

   public void commit() {
      PersistentObject.commit(this._meetingResponses);
   }
}
