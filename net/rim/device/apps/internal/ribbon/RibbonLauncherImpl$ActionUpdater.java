package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.internal.ribbon.launcher.HierarchyManager;
import net.rim.vm.Array;

final class RibbonLauncherImpl$ActionUpdater implements Runnable {
   private boolean _updatePending;
   private RibbonLauncherImpl _ribbon;
   private String[] _actionNames;

   RibbonLauncherImpl$ActionUpdater(RibbonLauncherImpl ribbon) {
      this._ribbon = ribbon;
      this._actionNames = new Object[1];
   }

   public final synchronized void invokeLater(String actionName) {
      if (!this._updatePending) {
         this._updatePending = true;
         this._actionNames[0] = actionName;
         this._ribbon._app.invokeLater(this);
      } else {
         int numScheduled = this._actionNames.length;
         Array.resize(this._actionNames, numScheduled + 1);
         this._actionNames[numScheduled] = actionName;
      }
   }

   @Override
   public final synchronized void run() {
      int numScheduled = this._actionNames.length;

      for (int i = 0; i < numScheduled; i++) {
         ((HierarchyManager)this._ribbon._hierarchyManager).refreshTemporaryApplication(this._actionNames[i]);
      }

      this._ribbon.internalUpdateApplicationDescription();
      if (numScheduled > 1) {
         Array.resize(this._actionNames, 1);
      }

      this._updatePending = false;
   }
}
