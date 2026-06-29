package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class AdditionalInformationRequiredScreen$EmailTypeChoiceFieldListener implements FieldChangeListener {
   private final AdditionalInformationRequiredScreen this$0;

   private AdditionalInformationRequiredScreen$EmailTypeChoiceFieldListener(AdditionalInformationRequiredScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.this$0._emailAndPasswordField.isSelected()) {
         this.this$0._nextEvent.setEvent(this.this$0._setupExistingMailEvent);
      } else {
         this.this$0._nextEvent.setEvent(this.this$0._selectAccountLinkEvent);
      }
   }

   AdditionalInformationRequiredScreen$EmailTypeChoiceFieldListener(AdditionalInformationRequiredScreen x0, AdditionalInformationRequiredScreen$1 x1) {
      this(x0);
   }
}
