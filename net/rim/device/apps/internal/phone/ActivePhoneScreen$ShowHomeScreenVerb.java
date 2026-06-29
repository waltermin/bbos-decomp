package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class ActivePhoneScreen$ShowHomeScreenVerb extends Verb {
   private final ActivePhoneScreen this$0;

   ActivePhoneScreen$ShowHomeScreenVerb(ActivePhoneScreen _1) {
      super(268501008, PhoneResources.getResourceBundle(), 6032);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (parameter != null) {
         ((VoiceApp)ActivePhoneScreen.access$300(this.this$0)).showHomeScreen();
         return null;
      }

      Dialog dlg = new ActivePhoneScreen$ExitFromActivePhoneScreenDlg(this.this$0, PhoneResources.getString(6257), null);
      ActivePhoneScreen.access$400(this.this$0).invokeLater(new ActivePhoneScreen$ShowHomeScreenVerb$1(this, dlg), 7000, false);
      ActivePhoneScreen.access$500(this.this$0).pushModalScreen(dlg);
      if (dlg.getSelectedValue() == 0) {
         ((VoiceApp)ActivePhoneScreen.access$600(this.this$0)).showHomeScreen();
      }

      return null;
   }
}
