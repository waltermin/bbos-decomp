package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.component.NullField;

public class FocusHolder extends NullField {
   private boolean _focusable = false;

   public FocusHolder() {
      super(18014398509481984L);
   }

   @Override
   public boolean isFocusable() {
      return this._focusable;
   }

   public void setFocusable(boolean focusable) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
