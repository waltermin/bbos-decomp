package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class AddressingSchemeOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"None", "From Field", "To Field", "Both Fields"};

   AddressingSchemeOptionField() {
      super("NDD Required: ", _choices, MMSClientServiceBook.getAddressingOptionsFlags());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         int flags = this.getSelectedIndex();
         MMSClientServiceBook.setAddressingOptionsFlags(flags);
      }
   }
}
