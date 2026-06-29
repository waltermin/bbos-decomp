package net.rim.device.internal.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

class InputMethodSwitcher$IMSwitcherDialog extends Dialog {
   public InputMethodSwitcher$IMSwitcherDialog(String message, Object[] choices, int defaultChoice) {
      super(message, choices, null, defaultChoice, null, 0);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      Font f = Font.getDefault();
      f = f.derive(f.getStyle(), 7, 3);
      this.setFont(f);
   }
}
