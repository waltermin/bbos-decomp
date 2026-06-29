package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class InferAcknowledgementUrlOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   InferAcknowledgementUrlOptionField() {
      super("Infer mmsc ack url: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.enableInferMessageAcknowledgementUrl(this.getSelectedIndex() == 1);
      }
   }

   private static final int getInitialChoice() {
      return MMSClientServiceBook.inferMessageAcknowledgementUrl() ? 1 : 0;
   }
}
