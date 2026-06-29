package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.wica.runtime.messaging.OutboundQueueConnection;

final class OutboundQueueConnectionImpl implements OutboundQueueConnection {
   private long _agId;
   private long _wicletId;
   private int _msgCount;
   private int _queueFullThreshold;
   private int _queueCriticalThreshold;
   private boolean _gracefulUninstall;

   final long getWicletId() {
      return this._wicletId;
   }

   final long getAgId() {
      return this._agId;
   }

   final void setAgId(long agId) {
      this._agId = agId;
   }

   final int getMessageCount() {
      return this._msgCount;
   }

   final synchronized void incrementMessageCount() {
      this._msgCount++;
   }

   final synchronized void decrementMessageCount() {
      this._msgCount--;
   }

   final synchronized void incrementMessageCount(int increment) {
      this._msgCount += increment;
   }

   final synchronized void decrementMessageCount(int decrement) {
      this._msgCount -= decrement;
   }

   public final boolean isQueueFull() {
      return this._msgCount >= this._queueFullThreshold;
   }

   public final boolean isQueueCritical() {
      return this._msgCount >= this._queueCriticalThreshold;
   }

   final void setQueueLimit(int limit) {
      this._queueFullThreshold = limit;
      this._queueCriticalThreshold = this._queueFullThreshold * 3 >>> 2;
   }

   final boolean gracefulUninstall() {
      return this._gracefulUninstall;
   }

   final void setUninstallMode(boolean graceful) {
      this._gracefulUninstall = graceful;
   }

   @Override
   public final int getQueueStatus() {
      if (this.isQueueFull()) {
         return 2;
      } else {
         return this.isQueueCritical() ? 1 : 0;
      }
   }

   OutboundQueueConnectionImpl(long wicletId, long agId, int queueLimit) {
      this._wicletId = wicletId;
      this._agId = agId;
      this.setQueueLimit(queueLimit);
   }
}
