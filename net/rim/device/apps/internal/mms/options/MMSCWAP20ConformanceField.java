package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class MMSCWAP20ConformanceField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   MMSCWAP20ConformanceField() {
      super("WAP 20 Conformance: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSTransportServiceBook.setWAP20Conformance(this.getSelectedIndex() == 1);
      }
   }

   private static final int getInitialChoice() {
      return MMSTransportServiceBook.getWAP20Conformance() ? 1 : 0;
   }
}
