package net.rim.device.apps.internal.ribbon.skin.svg.manager;

final class ContentInteractorManager$GlobalUpdater implements Runnable {
   private boolean _updatePending;
   private ContentInteractorManager _ciManager;

   ContentInteractorManager$GlobalUpdater(ContentInteractorManager cim) {
      this._ciManager = cim;
   }

   final void invokeLater() {
      boolean pending = this._updatePending;
      if (!pending) {
         this._updatePending = true;
         this._ciManager._application.invokeLater(this);
      }
   }

   @Override
   public final void run() {
      this._updatePending = false;

      for (int i = 0; i < 4; i++) {
         if (this._ciManager._handlers[i] != null) {
            this._ciManager._handlers[i].update(false);
            if (this._ciManager._handlers[i]._unreadCount != null) {
               this._ciManager._handlers[i]._unreadCount.ribbonComponentChanged(null);
            }
         }
      }

      this._ciManager._application.repaint();
   }
}
