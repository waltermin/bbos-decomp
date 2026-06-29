package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class MMSCVersionOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"1.0", "1.1", "1.2"};

   MMSCVersionOptionField() {
      super("MMSC Version: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         int version = this.getCurrentVersion();
         MMSClientServiceBook.setMMSCVersion(version);
      }
   }

   private static final int getInitialChoice() {
      switch (MMSClientServiceBook.getMMSCVersion()) {
         case 15:
            return 0;
         case 16:
         default:
            return 0;
         case 17:
            return 1;
         case 18:
            return 2;
      }
   }

   private final int getCurrentVersion() {
      switch (this.getSelectedIndex()) {
         case -1:
            return 16;
         case 0:
         default:
            return 16;
         case 1:
            return 17;
         case 2:
            return 18;
      }
   }
}
