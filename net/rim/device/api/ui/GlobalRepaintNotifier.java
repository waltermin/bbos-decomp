package net.rim.device.api.ui;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;

class GlobalRepaintNotifier {
   private int _foreground;
   private boolean _extentChanged;

   public boolean isExtentChanged() {
      return this._extentChanged;
   }

   synchronized void reset() {
      this._foreground = -1;
      this._extentChanged = false;
   }

   synchronized void post(boolean extentChanged) {
      int foreground = ApplicationManager.getApplicationManager().getForegroundProcessId();
      if (this._foreground == foreground) {
         this._extentChanged |= extentChanged;
      } else {
         if (foreground != -1) {
            this._foreground = foreground;
            this._extentChanged = extentChanged;
            RIMGlobalMessagePoster.postGlobalEvent(foreground, 5961289116197897667L, 3, 0, this, null);
         }
      }
   }
}
