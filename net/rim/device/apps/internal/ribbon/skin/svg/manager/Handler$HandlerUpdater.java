package net.rim.device.apps.internal.ribbon.skin.svg.manager;

final class Handler$HandlerUpdater implements Runnable {
   private boolean _updatePending;
   private Handler _handler;

   Handler$HandlerUpdater(Handler h) {
      this._handler = h;
   }

   final synchronized void invokeLater() {
      boolean pending = this._updatePending;
      if (!pending) {
         this._updatePending = true;
         this._handler._application.invokeLater(this, 1000, false);
      }
   }

   @Override
   public final void run() {
      this._updatePending = false;
      if (this._handler.MaxEntries > 0) {
         this._handler.update(true);
      }
   }
}
