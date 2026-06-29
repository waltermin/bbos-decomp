package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.NumericChoiceField;

final class MaximumRecipientSizeOptionField extends NumericChoiceField implements MMSOptionsScreen$Saveable {
   MaximumRecipientSizeOptionField() {
      super("Max Recipient KB: ", 0, 2000, 1, MMSClientServiceBook.getMaxRecipientByteSize() / 1024);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setMaxRecipientByteSize(this.getSelectedIndex() * 1024);
      }
   }
}
