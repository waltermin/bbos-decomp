package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumImageWidthOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumImageWidthOptionField() {
      super("Max Image Width: ", 0, 2000, 1, MMSClientServiceBook.getMaxImageWidth());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxImageWidth(this.getSelectedIndex());
      }
   }
}
