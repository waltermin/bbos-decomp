package net.rim.device.apps.internal.supl;

public final class SuplEvent {
   private int event;
   private Object data;
   static final int RRLP_PAYLOAD_INDICATION_EVENT = 0;
   static final int RECEIVED_ULP_MESSAGE_EVENT = 1;
   static final int UTIMER_EXPIRY_EVENT = 2;
   static final int CONNECTION_STATUS_EVENT = 3;

   SuplEvent(int event, Object data) {
      this.event = event;
      this.data = data;
   }

   public final int getEventType() {
      return this.event;
   }

   public final Object getData() {
      return this.data;
   }
}
