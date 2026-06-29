package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

final class DocViewTextDisplayField$DocViewInsertFieldRunnable implements Runnable {
   private final Manager _manager;
   private final Field _field;
   private final int _insertIndex;
   private final boolean _isAllowedControl;

   DocViewTextDisplayField$DocViewInsertFieldRunnable(Manager manager, Field insertField, int insertIndex, boolean isAllowedControl) {
      if (manager != null && insertField != null && insertIndex >= 0) {
         this._manager = manager;
         this._field = insertField;
         this._insertIndex = insertIndex;
         this._isAllowedControl = isAllowedControl;
      } else {
         throw new Object("Invalid insert parameters");
      }
   }

   @Override
   public final void run() {
      if (this._field.getIndex() == -1) {
         this._manager.insert(this._field, Math.min(this._insertIndex, this._manager.getFieldCount()));
      }
   }
}
