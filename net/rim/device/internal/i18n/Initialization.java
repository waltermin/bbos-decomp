package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.ResourceBundle;

public final class Initialization {
   public static final void I18nMain() {
      ResourceBundle.registerGlobalEventListener();
   }
}
