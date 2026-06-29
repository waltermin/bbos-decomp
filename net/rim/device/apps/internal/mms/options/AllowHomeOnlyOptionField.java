package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class AllowHomeOnlyOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   AllowHomeOnlyOptionField() {
      super("Allow Home Only: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.enableAllowHomeOnly(this.getSelectedIndex() == 1);
      }
   }

   private static final int getInitialChoice() {
      return MMSClientServiceBook.allowHomeOnly() ? 1 : 0;
   }
}
