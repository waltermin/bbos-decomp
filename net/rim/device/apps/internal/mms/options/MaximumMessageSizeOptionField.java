package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumMessageSizeOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumMessageSizeOptionField() {
      super("Max Message KB: ", 0, 2000, 1, MMSClientServiceBook.getMaxMessageSize() / 1024);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxMessageSize(this.getSelectedIndex() * 1024);
      }
   }
}
