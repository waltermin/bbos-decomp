package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ScreenKeyboardOptionsItem$OKButton extends ButtonField {
   ScreenKeyboardOptionsItem$OKButton() {
      super(OptionsResources.getString(2083), 12884901888L);
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      return super.trackwheelClick(status, time);
   }
}
