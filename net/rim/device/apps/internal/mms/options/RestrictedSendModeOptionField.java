package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class RestrictedSendModeOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"None", "Restricted", "Warning"};

   RestrictedSendModeOptionField() {
      super("Send Validation: ", _choices, MMSClientServiceBook.getRestrictedSendMode());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setRestrictedSendMode(this.getSelectedIndex());
      }
   }
}
