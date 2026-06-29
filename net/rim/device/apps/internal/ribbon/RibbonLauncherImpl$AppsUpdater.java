package net.rim.device.apps.internal.ribbon;

final class RibbonLauncherImpl$AppsUpdater implements Runnable {
   private boolean _updatePending;
   private RibbonLauncherImpl _ribbon;
   private boolean _locateApplications;

   RibbonLauncherImpl$AppsUpdater(RibbonLauncherImpl ribbon, boolean locateApplications) {
      this._ribbon = ribbon;
      this._locateApplications = locateApplications;
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
      if (this._locateApplications) {
         this._ribbon.locateApplications();
      }

      this._ribbon.populateRibbon();
   }
}
