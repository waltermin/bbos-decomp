package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class SendTextAsSimpleContentOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   SendTextAsSimpleContentOptionField() {
      super("Send Text as Simple Content: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.enableSendTextAsSimpleContent(this.getSelectedIndex() == 1);
      }
   }

   private static final int getInitialChoice() {
      return MMSClientServiceBook.sendTextAsSimpleContent() ? 1 : 0;
   }
}
