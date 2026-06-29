package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class RetrievalUrlSchemeOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"None", "Verizon"};

   RetrievalUrlSchemeOptionField() {
      super("Retrieve URL Scheme: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setRetrievalUrlScheme(this.getSelectedIndex());
      }
   }

   private static final int getInitialChoice() {
      return MMSClientServiceBook.getRetrievalUrlScheme();
   }
}
