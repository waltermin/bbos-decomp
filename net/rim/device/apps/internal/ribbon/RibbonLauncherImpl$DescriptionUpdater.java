package net.rim.device.apps.internal.ribbon;

final class RibbonLauncherImpl$DescriptionUpdater implements Runnable {
   private boolean _updatePending;
   private RibbonLauncherImpl _ribbon;

   RibbonLauncherImpl$DescriptionUpdater(RibbonLauncherImpl ribbon) {
      this._ribbon = ribbon;
   }

   public final synchronized void invokeLater() {
      boolean pending = this._updatePending;
      this._updatePending = true;
      if (!pending) {
         this._ribbon._app.invokeLater(this);
      }
   }

   @Override
   public final void run() {
      this._updatePending = false;
      this._ribbon.internalUpdateApplicationDescription();
   }
}
