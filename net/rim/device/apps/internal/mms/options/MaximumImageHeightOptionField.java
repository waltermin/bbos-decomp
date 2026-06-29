package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumImageHeightOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumImageHeightOptionField() {
      super("Max Image Height: ", 0, 2000, 1, MMSClientServiceBook.getMaxImageHeight());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxImageHeight(this.getSelectedIndex());
      }
   }
}
