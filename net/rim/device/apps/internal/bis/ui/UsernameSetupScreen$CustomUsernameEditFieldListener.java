package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class UsernameSetupScreen$CustomUsernameEditFieldListener implements FieldChangeListener {
   private final UsernameSetupScreen this$0;

   private UsernameSetupScreen$CustomUsernameEditFieldListener(UsernameSetupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this.this$0._userNameEdit && this.this$0._customUserNameChoice != null) {
         this.this$0._customUserNameChoice.setSelected(true);
      }
   }

   UsernameSetupScreen$CustomUsernameEditFieldListener(UsernameSetupScreen x0, UsernameSetupScreen$1 x1) {
      this(x0);
   }
}
