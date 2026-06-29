package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class DocViewTextDisplayField$DocViewAppendFieldRunnable implements Runnable {
   private final Manager _manager;
   private final Field _field;
   private int _insertIndex = -1;

   DocViewTextDisplayField$DocViewAppendFieldRunnable(Manager manager, Field insertField) {
      if (manager != null && insertField != null) {
         this._manager = manager;
         this._field = insertField;
      } else {
         throw new Object("Invalid insert parameters");
      }
   }

   final void setIndex(int index) {
      this._insertIndex = index;
   }

   @Override
   public final void run() {
      if (this._field.getIndex() == -1 && this._insertIndex >= 0) {
         this._manager.insert(this._field, Math.min(this._insertIndex, this._manager.getFieldCount()));
      }
   }
}
