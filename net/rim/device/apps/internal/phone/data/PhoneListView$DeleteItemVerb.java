package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.options.PhoneOptions;

final class PhoneListView$DeleteItemVerb extends Verb {
   private final PhoneListView this$0;

   public PhoneListView$DeleteItemVerb(PhoneListView _1) {
      super(_1.getDeleteVerbOrdering(), CommonResources.getResourceBundle(), 1000);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (!VoiceServices.isPhoneActive() && this.this$0.getCount() != 0) {
         int answer;
         if (this.this$0.getSelectionCount() > 1) {
            answer = Dialog.ask(2, this.this$0.getDeletePrompt(true), 3);
         } else if (PhoneOptions.getOptions().getBooleanOption(8192)) {
            answer = 3;
         } else {
            answer = Dialog.ask(2, this.this$0.getDeletePrompt(false), 3);
         }

         if (answer == 3) {
            this.this$0._app.invokeLater(new PhoneListView$DeleteItemVerb$1(this));
         }

         return null;
      } else {
         return null;
      }
   }
}
