package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class FromFieldSchemeOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"Injected by MMSC", "Set By Client"};

   FromFieldSchemeOptionField() {
      super("From Field: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         int choice = this.getCurrentChoice();
         MMSClientServiceBook.setComposeFromScheme(choice);
      }
   }

   private static final int getInitialChoice() {
      switch (MMSClientServiceBook.getComposeFromScheme()) {
         case -1:
            return 0;
         case 0:
         default:
            return 0;
         case 1:
            return 1;
      }
   }

   private final int getCurrentChoice() {
      switch (this.getSelectedIndex()) {
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
