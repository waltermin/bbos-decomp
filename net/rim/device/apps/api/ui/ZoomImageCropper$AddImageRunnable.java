package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

class ZoomImageCropper$AddImageRunnable implements Runnable {
   private Manager _manager;
   private Field _field;

   public ZoomImageCropper$AddImageRunnable(Manager manager, Field field) {
      this._manager = manager;
      this._field = field;
   }

   @Override
   public void run() {
      this._manager.add(this._field);
   }
}
