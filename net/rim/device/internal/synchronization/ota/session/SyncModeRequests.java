package net.rim.device.internal.synchronization.ota.session;

import net.rim.device.internal.synchronization.ota.util.Helper;

final class SyncModeRequests {
   private byte _flags;
   static final byte ISSUE_PROGRESSIVE_SYNC;
   static final byte ISSUE_BATCH_SYNC;

   public final boolean inBatchMode() {
      boolean xIssueProgressiveSync = Helper.getFlagValue(this._flags, 1);
      boolean xIssueBatchedSync = Helper.getFlagValue(this._flags, 2);
      return !xIssueProgressiveSync && xIssueBatchedSync;
   }

   public final boolean inIdleMode() {
      boolean xIssueProgressiveSync = Helper.getFlagValue(this._flags, 1);
      boolean xIssueBatchedSync = Helper.getFlagValue(this._flags, 2);
      return !xIssueProgressiveSync && !xIssueBatchedSync;
   }

   public final void request(int aSyncMode) {
      switch (aSyncMode) {
         case -1:
            break;
         case 0:
         default:
            if (!Helper.getFlagValue(this._flags, 2)) {
               this._flags = (byte)Helper.setFlagValue(this._flags, true, 2);
               this.notify();
               return;
            }
            break;
         case 1:
            this._flags = (byte)Helper.setFlagValue(this._flags, true, 1);
            this._flags = (byte)Helper.setFlagValue(this._flags, false, 2);
            this.notify();
      }
   }

   public final void batch(long value) {
      if (this.inIdleMode()) {
         this.wait();
      }

      boolean xIssueProgressiveSync = Helper.getFlagValue(this._flags, 1);
      boolean xIssueBatchedSync = Helper.getFlagValue(this._flags, 2);
      if (!xIssueProgressiveSync && xIssueBatchedSync && value != 0) {
         super.wait(value);
      }
   }

   public final void reset() {
      this._flags = 0;
   }
}
