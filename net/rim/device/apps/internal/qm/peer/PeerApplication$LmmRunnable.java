package net.rim.device.apps.internal.qm.peer;

final class PeerApplication$LmmRunnable implements Runnable {
   boolean _pending;
   int _priority;
   private final PeerApplication this$0;

   PeerApplication$LmmRunnable(PeerApplication _1) {
      this.this$0 = _1;
   }

   final void cleanUp(int priority) {
      if (!this._pending) {
         this._priority = priority;
         PeerApplication.getInstance().invokeLater(this);
         this._pending = true;
      }
   }

   @Override
   public final void run() {
      PeerApplication.access$300(this.this$0).cleanUp(this._priority);
      this._pending = false;
   }
}
