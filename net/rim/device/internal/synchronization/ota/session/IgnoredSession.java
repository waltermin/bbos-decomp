package net.rim.device.internal.synchronization.ota.session;

final class IgnoredSession {
   private int _sessionId;
   private int _numberOfDatagramsRemaining;
   private int _reason;
   private long _time;
   public static final byte OBSOLETE;
   public static final byte OUT_OF_CONTEXT;
   public static final byte OUT_OF_SYNC;
   public static final byte TIMEDOUT;
   public static final byte ABORTED;
   public static final byte SUCCEEDED;
   public static final byte LATE;

   IgnoredSession(int aSessionId, int reason) {
      this(aSessionId, -1, reason);
   }

   IgnoredSession(int aSessionId, int aNumberOfDatagramsRemaining, int aReason) {
      this._sessionId = aSessionId;
      this._numberOfDatagramsRemaining = aNumberOfDatagramsRemaining;
      this._reason = aReason;
   }

   final int getReason() {
      return this._reason;
   }

   final int getSessionId() {
      return this._sessionId;
   }

   final void setTime(long aTime) {
      this._time = aTime;
   }

   final long getTime() {
      return this._time;
   }

   final boolean allDatagramsReceived() {
      return this._numberOfDatagramsRemaining == 0;
   }

   final void decNumberOfDatagramsRemaining() {
      this._numberOfDatagramsRemaining--;
   }
}
