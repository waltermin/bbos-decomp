package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.component.ButtonField;

final class EULA$DisplayEULA$FocusButton extends ButtonField {
   private boolean _focusableState;
   private final EULA$DisplayEULA this$1;

   EULA$DisplayEULA$FocusButton(EULA$DisplayEULA this$1, String label) {
      super(label);
      this.this$1 = this$1;
   }

   @Override
   public final boolean isFocusable() {
      return this._focusableState;
   }
}
