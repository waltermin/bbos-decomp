package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class EditForwardingNumbersScreen$DeleteNumberVerb extends Verb {
   private final EditForwardingNumbersScreen this$0;

   EditForwardingNumbersScreen$DeleteNumberVerb(EditForwardingNumbersScreen _1) {
      super(611472);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(1000);
   }

   @Override
   public final Object invoke(Object parameter) {
      int index = this.this$0._listField.getSelectedIndex();
      if (index != -1 && Dialog.ask(2, PhoneResources.getString(119), 3) == 3) {
         PhoneOptions.getOptions().deleteSavedForwardingNumber(index);
         this.this$0.onNumberDeleted();
      }

      return null;
   }
}
