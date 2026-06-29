package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

class DocViewTextDisplayField$DocViewDeleteFieldRunnable implements Runnable {
   protected final Manager _manager;
   protected final Field _field;

   DocViewTextDisplayField$DocViewDeleteFieldRunnable(Manager manager, Field deleteField) {
      if (manager != null && deleteField != null) {
         this._manager = manager;
         this._field = deleteField;
      } else {
         throw new Object("Invalid delete parameters");
      }
   }

   @Override
   public void run() {
      if (this._field.getManager() == this._manager) {
         this._manager.delete(this._field);
      }
   }
}
