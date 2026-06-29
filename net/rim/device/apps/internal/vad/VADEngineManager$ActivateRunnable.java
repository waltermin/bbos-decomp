package net.rim.device.apps.internal.vad;

final class VADEngineManager$ActivateRunnable implements Runnable {
   private int _context;
   private final VADEngineManager this$0;

   VADEngineManager$ActivateRunnable(VADEngineManager _1, int context) {
      this.this$0 = _1;
      this._context = context;
   }

   @Override
   public final void run() {
      this.this$0.activate(this._context);
   }
}
