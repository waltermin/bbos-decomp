package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.CommonResources;

final class CellBroadcastDialog extends Dialog {
   ButtonField _okButton;
   ButtonField _dismissButton;

   CellBroadcastDialog(String text) {
      super(text, null, null, 0, null);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      this.setEscapeEnabled(false);
      this._dismissButton = (ButtonField)(new Object(CommonResources.getString(9169), 12884901888L));
      this._dismissButton.setChangeListener(this);
      this._okButton = (ButtonField)(new Object(CommonResources.getString(117), 12884901888L));
      this._okButton.setChangeListener(this);
      this.add(this._dismissButton);
      this.add(this._okButton);
      this._dismissButton.setFocus();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this.handleDismiss();
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         this.handleDismiss();
      }

      return super.invokeAction(action);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.handleDismiss();
      return super.navigationClick(status, time);
   }

   private final void handleDismiss() {
      if (this.getLeafFieldWithFocus() == this._dismissButton) {
         RibbonLauncher.getInstance().setNetworkImmediateDisplayString(null);
      }
   }
}
