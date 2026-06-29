package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class EditForwardingNumbersScreen$EditNumberVerb extends Verb {
   private final EditForwardingNumbersScreen this$0;

   EditForwardingNumbersScreen$EditNumberVerb(EditForwardingNumbersScreen _1) {
      super(611152);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(3011);
   }

   @Override
   public final Object invoke(Object parameter) {
      int index = this.this$0._listField.getSelectedIndex();
      if (index != -1) {
         int oldLength = this.this$0._fwdingNumbers.length;
         String editNumber = this.this$0._fwdingNumbers[index];
         this.this$0._addNumberVerb.invoke(editNumber);
         if (this.this$0._fwdingNumbers.length > oldLength && PhoneUtilities.getArrayIndex(editNumber, this.this$0._activeForwardNumbers) == -1) {
            index = PhoneUtilities.getArrayIndex(editNumber, this.this$0._fwdingNumbers);
            this.this$0._listField.delete(index);
            PhoneOptions.getOptions().deleteSavedForwardingNumber(index);
         }
      }

      return null;
   }
}
