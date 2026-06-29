package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

final class AccountSetupSuggestionsScreen$CustomUsernameEditFieldListener implements FieldChangeListener {
   private final AccountSetupSuggestionsScreen this$0;

   private AccountSetupSuggestionsScreen$CustomUsernameEditFieldListener(AccountSetupSuggestionsScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this.this$0._customUserNameEdit) {
         this.this$0._customUserNameChoice.setSelected(true);
      }
   }

   AccountSetupSuggestionsScreen$CustomUsernameEditFieldListener(AccountSetupSuggestionsScreen x0, AccountSetupSuggestionsScreen$1 x1) {
      this(x0);
   }
}
