package net.rim.device.internal.synchronization.ota.session;

final class IgnoredSession {
   private int _sessionId;
   private int _numberOfDatagramsRemaining;
   private int _reason;
   private long _time;
   public static final byte OBSOLETE = 1;
   public static final byte OUT_OF_CONTEXT = 2;
   public static final byte OUT_OF_SYNC = 3;
   public static final byte TIMEDOUT = 4;
   public static final byte ABORTED = 5;
   public static final byte SUCCEEDED = 6;
   public static final byte LATE = 7;

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
