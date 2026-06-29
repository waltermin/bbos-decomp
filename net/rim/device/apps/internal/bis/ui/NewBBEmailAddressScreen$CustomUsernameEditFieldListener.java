package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class NewBBEmailAddressScreen$CustomUsernameEditFieldListener implements FieldChangeListener {
   private final NewBBEmailAddressScreen this$0;

   private NewBBEmailAddressScreen$CustomUsernameEditFieldListener(NewBBEmailAddressScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this.this$0._userNameEdit && this.this$0._customUserNameChoice != null) {
         this.this$0._customUserNameChoice.setSelected(true);
      }
   }

   NewBBEmailAddressScreen$CustomUsernameEditFieldListener(NewBBEmailAddressScreen x0, NewBBEmailAddressScreen$1 x1) {
      this(x0);
   }
}
