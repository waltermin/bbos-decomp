package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class SetupPersonalEmailScreen$EmailTypeChoiceFieldListener implements FieldChangeListener {
   private final SetupPersonalEmailScreen this$0;

   private SetupPersonalEmailScreen$EmailTypeChoiceFieldListener(SetupPersonalEmailScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.this$0._outlookRadioButton.isSelected()) {
         this.this$0._emailSetupLinkEvent.setLink(24);
      } else {
         this.this$0._emailSetupLinkEvent.setLink(23);
      }
   }

   SetupPersonalEmailScreen$EmailTypeChoiceFieldListener(SetupPersonalEmailScreen x0, SetupPersonalEmailScreen$1 x1) {
      this(x0);
   }
}
