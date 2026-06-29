package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class EndUserAgreementScreen$ViewUserAgreementListener implements FieldChangeListener {
   private final EndUserAgreementScreen this$0;

   private EndUserAgreementScreen$ViewUserAgreementListener(EndUserAgreementScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this.this$0._viewYesButton) {
         boolean yesEnabled = this.this$0._viewYesButton.isSelected();
         this.this$0._agreeButton.setEditable(yesEnabled);
         this.this$0._agreeEvent.setOnMenu(yesEnabled);
      }
   }

   EndUserAgreementScreen$ViewUserAgreementListener(EndUserAgreementScreen x0, EndUserAgreementScreen$1 x1) {
      this(x0);
   }
}
