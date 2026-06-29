package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.util.MathUtilities;

final class ConnectionTimeoutOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   private static final int MIN_VALUE;
   private static final int MAX_VALUE;

   ConnectionTimeoutOptionField() {
      super("Connection Timeout (s): ", 0, 600, 1, initialValue());
   }

   private static final int initialValue() {
      return MathUtilities.clamp(0, MMSClientServiceBook.getConnectionTimeout(), 600);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setConnectionTimeout(this.getSelectedIndex());
      }
   }
}
