package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class AllowImageReductionBeforeSendOptionField extends ObjectChoiceField implements MMSOptionsScreen$Saveable {
   private static String[] _choices = new String[]{"No", "Yes"};

   AllowImageReductionBeforeSendOptionField() {
      super("Reduce Images Before Send: ", _choices, getInitialChoice());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.enableImageReductionBeforeSend(this.getSelectedIndex() == 1);
      }
   }

   private static final int getInitialChoice() {
      return MMSClientServiceBook.allowImageReductionBeforeSend() ? 1 : 0;
   }
}
