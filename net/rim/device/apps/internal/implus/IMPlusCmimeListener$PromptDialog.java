package net.rim.device.apps.internal.implus;

import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;

public final class IMPlusCmimeListener$PromptDialog extends Dialog {
   public CheckboxField _savePreference = new CheckboxField(IMPlusResources.getString(26), false);

   IMPlusCmimeListener$PromptDialog() {
      super(3, IMPlusResources.getString(25), 4, null, 0);
      this.add(this._savePreference);
   }
}
