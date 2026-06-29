package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class EmailSetupScreen$EmailTypeChoiceFieldListener implements FieldChangeListener {
   private final EmailSetupScreen this$0;

   private EmailSetupScreen$EmailTypeChoiceFieldListener(EmailSetupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.this$0._setupExistingEmailChoice.isSelected()) {
         this.this$0._emailSetupLinkEvent.setLink(4);
      } else {
         this.this$0._emailSetupLinkEvent.setLink(5);
      }
   }

   EmailSetupScreen$EmailTypeChoiceFieldListener(EmailSetupScreen x0, EmailSetupScreen$1 x1) {
      this(x0);
   }
}
