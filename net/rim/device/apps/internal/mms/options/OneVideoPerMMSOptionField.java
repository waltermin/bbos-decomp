package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class OneVideoPerMMSOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   OneVideoPerMMSOptionField() {
      super("One Video Per MMS: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setOneVideoPerMMS(this.getSelectedIndex() == 1);
      }
   }

   private static final int getInitialChoice() {
      return MMSClientServiceBook.oneVideoPerMMS() ? 1 : 0;
   }
}
