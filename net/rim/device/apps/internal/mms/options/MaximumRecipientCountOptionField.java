package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumRecipientCountOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumRecipientCountOptionField() {
      super("Max Recipient Count: ", 0, 2000, 1, MMSClientServiceBook.getMaxRecipientCount());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxRecipientCount(this.getSelectedIndex());
      }
   }
}
