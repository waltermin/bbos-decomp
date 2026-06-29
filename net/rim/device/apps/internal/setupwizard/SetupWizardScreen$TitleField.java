package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.LabelField;

final class SetupWizardScreen$TitleField extends LabelField {
   public SetupWizardScreen$TitleField(String title) {
      super(title, 64);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setFont(Font.getDefault().derive(1, 7, 3));
   }
}
