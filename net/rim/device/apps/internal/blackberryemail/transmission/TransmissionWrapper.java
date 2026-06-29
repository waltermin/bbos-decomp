package net.rim.device.apps.internal.blackberryemail.transmission;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.Persistable;

public class TransmissionWrapper implements Persistable {
   private Object _object;
   private Object _context;
   private String _type;
   private byte _attempts;
   private long _sheduledTxTime;
   private int _tag;
   private long _initialSendTime;
   private long _lastSendTime;
   private boolean _sendPending;

   public TransmissionWrapper(String type, Object object, Object context) {
      this._object = object;
      this._context = context;
      this._type = type;
      this.generateTag();
      this._sendPending = true;
   }

   public Object getObject() {
      return this._object;
   }

   public int getTag() {
      return this._tag;
   }

   public Object getContext() {
      return this._context;
   }

   public byte getTransmissionSendAttempts() {
      return this._attempts;
   }

   protected long getInitialSendTime() {
      return this._initialSendTime;
   }

   protected long getLastSendAttemptDelta() {
      return System.currentTimeMillis() - this._lastSendTime;
   }

   byte incrementTransmissionAttempts() {
      if (this._initialSendTime == 0) {
         this._initialSendTime = System.currentTimeMillis();
         this._lastSendTime = this._initialSendTime;
      } else {
         this._lastSendTime = System.currentTimeMillis();
      }

      return ++this._attempts;
   }

   String getType() {
      return this._type;
   }

   void setScheduledActionTime(long time, boolean send) {
      this._sheduledTxTime = time;
   }

   long getScheduledActionTime() {
      return this._sheduledTxTime;
   }

   boolean isSendActionPending() {
      return this._sendPending;
   }

   void generateTag() {
      this._tag = UIDGenerator.getUID();
   }

   protected boolean canSend() {
      return true;
   }

   protected boolean successOnSent() {
      return true;
   }

   protected boolean requiresPersitence() {
      return false;
   }

   public void updateTransmissionStatus(byte _1, int _2) {
      throw null;
   }

   public int getTransmissionRetryDelay() {
      throw null;
   }

   public byte getTransmissionRetryLimit() {
      throw null;
   }
}
