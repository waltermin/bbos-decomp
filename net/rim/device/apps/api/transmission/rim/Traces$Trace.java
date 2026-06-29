package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;

class Traces$Trace {
   private long _referenceIdentifier;
   private int _serviceRecordId;
   private String _serviceRecordUid;
   private Traces$Trace _next;
   private Traces$Trace _previous;

   public Traces$Trace(long referenceIdentifierLong, int serviceRecordIdInt, String serviceRecordUidString, Traces$Trace nextTrace, Traces$Trace previousTrace) {
      this._referenceIdentifier = referenceIdentifierLong;
      this._serviceRecordId = serviceRecordIdInt;
      this._serviceRecordUid = serviceRecordUidString;
      this._next = nextTrace;
      this._previous = previousTrace;
      if (this._next != null) {
         this._next._previous = this;
      }
   }

   boolean contains(Traces$Trace aTrace) {
      boolean result = aTrace._referenceIdentifier == this._referenceIdentifier;
      if (result) {
         result &= aTrace._serviceRecordId == this._serviceRecordId;
         if (!result) {
            EventLogger.logEvent(-7509200465648525729L, 1382904681, 3);
         }
      }

      if (result) {
         result &= aTrace._serviceRecordUid != null && StringUtilities.strEqualIgnoreCase(aTrace._serviceRecordUid, this._serviceRecordUid, 1701707776);
         if (!result) {
            EventLogger.logEvent(-7509200465648525729L, 1382904693, 3);
         }
      }

      if (!result && this._next != null) {
         result = this._next.contains(aTrace);
      }

      return result;
   }

   Traces$Trace trim() {
      Traces$Trace next = this._next;
      Traces$Trace previous = this._previous;
      if (previous != null) {
         previous._next = next;
      }

      if (next != null) {
         next._previous = previous;
      }

      this._next = null;
      this._previous = null;
      this._serviceRecordUid = null;
      return previous;
   }
}
