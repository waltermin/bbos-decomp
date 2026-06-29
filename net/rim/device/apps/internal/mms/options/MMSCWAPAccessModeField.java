package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class MMSCWAPAccessModeField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   MMSCWAPAccessModeField() {
      super("WAP Secure Access: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSTransportServiceBook.setWAPAccessMode(this.getSelectedIndex());
      }
   }

   private static final int getInitialChoice() {
      switch (MMSTransportServiceBook.getWAPAccessMode()) {
         case 1:
            return 1;
         default:
            return 0;
      }
   }

   @Override
   public final int getSelectedIndex() {
      switch (super.getSelectedIndex()) {
         case -1:
            return 0;
         case 0:
         default:
            return 0;
         case 1:
            return 1;
      }
   }
}
