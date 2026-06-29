package net.rim.device.apps.internal.browser.util;

import net.rim.device.internal.ui.component.ProgressDialog;

class PNGImageWriter$UpdateProgress implements Runnable {
   private ProgressDialog _pg;
   private int _percent;

   public PNGImageWriter$UpdateProgress(ProgressDialog pg) {
      this._pg = pg;
   }

   public void setProgress(int percent) {
      this._percent = percent;
   }

   @Override
   public void run() {
      this._pg.setProgress(this._percent);
   }
}
