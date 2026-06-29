package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class RestrictedSizeModeOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"None", "Restricted", "Warning"};

   RestrictedSizeModeOptionField() {
      super("Size Validation: ", _choices, MMSClientServiceBook.getRestrictedSizeMode());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setRestrictedSizeMode(this.getSelectedIndex());
      }
   }
}
