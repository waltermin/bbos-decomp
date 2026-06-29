package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.cldc.io.gme.GMEAddress;
import net.rim.device.cldc.io.gme.GMETarget;

class Traces {
   private int _capacity;
   private int _size;
   private Traces$Trace _first;
   private Traces$Trace _last;

   public Traces(int capacityInt) {
      this._capacity = capacityInt;
      this._size = 0;
      this._first = null;
      this._last = null;
   }

   boolean isDuplicate(String typeString, Object transmissionObject, Object contextObject) {
      boolean result = false;
      if (transmissionObject instanceof RIMMessagingIncomingMessage) {
         if (((RIMMessagingIncomingMessage)transmissionObject).getReferenceIdentifier() == 0) {
            return false;
         }

         ContextObject context = ContextObject.castOrCreate(contextObject);
         ServiceRecord record = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
         Traces$Trace trace;
         if (record != null) {
            trace = new Traces$Trace(
               ((RIMMessagingIncomingMessage)transmissionObject).getReferenceIdentifier(), record.getId(), record.getUid(), this._first, null
            );
         } else {
            try {
               GMETarget src = ((GMEAddress)ContextObject.get(context, -7981905408958106750L)).getSrc();
               if (src.type != 2) {
                  return false;
               }

               trace = new Traces$Trace(((RIMMessagingIncomingMessage)transmissionObject).getReferenceIdentifier(), -1, src.address, this._first, null);
            } finally {
               ;
            }
         }

         if (this._first != null) {
            result = this._first.contains(trace);
         }

         if (result) {
            return result;
         }

         this._size++;
         this._first = trace;
         if (this._last == null) {
            this._last = this._first;
         }

         if (this._size > this._capacity && this._last != null) {
            this._last = this._last.trim();
            this._size--;
         }
      }

      return result;
   }
}
