package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class ExistingEmailSetupScreen$EmailInfoFieldListener implements FieldChangeListener {
   private final ExistingEmailSetupScreen this$0;

   private ExistingEmailSetupScreen$EmailInfoFieldListener(ExistingEmailSetupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      String email = this.this$0._emailAddressEdit.getText();
      String password = this.this$0._passwordEdit.getText();
      if (email != null && email.length() != 0 || password != null && password.length() != 0) {
         this.this$0._nextEvent.setEvent(this.this$0._setupExistingMailEvent);
      } else {
         this.this$0._nextEvent.setEvent(this.this$0._servicesMainLinkEvent);
      }
   }

   ExistingEmailSetupScreen$EmailInfoFieldListener(ExistingEmailSetupScreen x0, ExistingEmailSetupScreen$1 x1) {
      this(x0);
   }
}
