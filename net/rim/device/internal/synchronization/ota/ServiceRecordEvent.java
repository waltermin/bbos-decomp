package net.rim.device.internal.synchronization.ota;

import java.util.Vector;
import net.rim.device.internal.synchronization.ota.util.Event;

class ServiceRecordEvent extends Event {
   private long _eventId;
   private int _serviceGuid;
   private OTASyncDaemon _otaSyncDaemon;
   private byte _specialHandling;
   public static final byte NO_SPECIAL_HANDLING;
   public static final byte HANDLE_AS_REPLACEMENT;
   public static final byte HANDLE_AS_ALREADY_INITIALIZED;

   ServiceRecordEvent(OTASyncDaemon otaSyncDaemon, long eventId, int data0) {
      this(otaSyncDaemon, eventId, data0, (byte)0);
   }

   ServiceRecordEvent(OTASyncDaemon otaSyncDaemon, long eventId, int data0, byte specialHandling) {
      this._eventId = eventId;
      this._serviceGuid = data0;
      this._otaSyncDaemon = otaSyncDaemon;
      this._specialHandling = specialHandling;
   }

   public byte getSpecialHandling() {
      return this._specialHandling;
   }

   long getEventId() {
      return this._eventId;
   }

   int getServiceGuid() {
      return this._serviceGuid;
   }

   @Override
   public boolean onBeforeAddingEvent(Vector queue) {
      return true;
   }

   @Override
   public void onExecute() {
      try {
         this._otaSyncDaemon.onServiceRecordEvent(this);
      } finally {
         return;
      }
   }
}
