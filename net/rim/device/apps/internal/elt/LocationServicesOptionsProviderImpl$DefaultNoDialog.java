package net.rim.device.apps.internal.elt;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

final class LocationServicesOptionsProviderImpl$DefaultNoDialog extends Dialog {
   LocationServicesOptionsProviderImpl$DefaultNoDialog() {
      super(3, LocationServicesOptionsProviderImpl._lbsBundle.getString(1), -1, null, 0);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
   }
}
