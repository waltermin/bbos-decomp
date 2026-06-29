package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumTextLengthOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumTextLengthOptionField() {
      super("Max Text Length: ", 0, 124000, 1, MMSClientServiceBook.getMaxComposeTextLength());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxComposeTextLength(this.getSelectedIndex());
      }
   }
}
