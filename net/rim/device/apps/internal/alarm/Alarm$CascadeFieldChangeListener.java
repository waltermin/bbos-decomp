package net.rim.device.apps.internal.alarm;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class Alarm$CascadeFieldChangeListener implements FieldChangeListener {
   private FieldChangeListener _originalListener;
   private FieldChangeListener _newListener;

   Alarm$CascadeFieldChangeListener(Field originalField, FieldChangeListener newListener) {
      this._originalListener = originalField.getChangeListener();
      this._newListener = newListener;
      originalField.setChangeListener(null);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this._originalListener != null) {
         this._originalListener.fieldChanged(field, context);
      }

      if (this._newListener != null) {
         this._newListener.fieldChanged(field, context);
      }
   }
}
