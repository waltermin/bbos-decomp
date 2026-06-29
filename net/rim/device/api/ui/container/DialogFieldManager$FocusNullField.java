package net.rim.device.api.ui.container;

import net.rim.device.api.ui.component.NullField;

class DialogFieldManager$FocusNullField extends NullField {
   private boolean _canFocus = true;

   private DialogFieldManager$FocusNullField() {
      super(18014398509481984L);
   }

   @Override
   public boolean isFocusable() {
      return this._canFocus;
   }

   DialogFieldManager$FocusNullField(DialogFieldManager$1 x0) {
      this();
   }
}
