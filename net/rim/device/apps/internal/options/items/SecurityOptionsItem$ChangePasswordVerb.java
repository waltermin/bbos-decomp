package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SecurityDialog;

public final class SecurityOptionsItem$ChangePasswordVerb extends Verb {
   private final SecurityOptionsItem this$0;

   SecurityOptionsItem$ChangePasswordVerb(SecurityOptionsItem _1) {
      super(200960, CommonResources.getResourceBundle(), 1780);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      SecurityDialog.changePassword(null, false, true, false, '\u0000');
      this.this$0._passwordEnabledDisabledField.setAffirmative(this.this$0._security.isPasswordEnabled());
      this.this$0._passwordEnabledDisabledField.setDirty(false);
      return null;
   }
}
