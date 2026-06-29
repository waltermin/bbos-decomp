package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class PrependedDisclaimerModelField extends LabelField {
   private boolean _prependDisclaimer;

   PrependedDisclaimerModelField(PrependedDisclaimerModel disclaimerModel) {
      super(null, 18014398509481984L);
      this._prependDisclaimer = disclaimerModel.prependDisclaimer();
      this.updateFieldText();
   }

   public final boolean prependDisclaimer() {
      return this._prependDisclaimer;
   }

   public final void togglePrependDisclaimer() {
      this._prependDisclaimer = !this._prependDisclaimer;
      this.updateFieldText();
   }

   private final void updateFieldText() {
      int resourceId;
      if (this._prependDisclaimer) {
         resourceId = 75;
      } else {
         resourceId = 76;
      }

      this.setText(EmailResources.getString(resourceId));
   }
}
