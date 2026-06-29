package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.ui.Border;

final class HTMLResetInputField extends HTMLButtonField {
   HTMLResetInputField(HTMLInput input, String value, long style, int width, int height, int backgroundColor, int foregroundColor, Border parentBorder) {
      super(
         input,
         value != null && value.length() != 0 ? value : BrowserResources.getString(546),
         style,
         width,
         height,
         backgroundColor,
         foregroundColor,
         parentBorder
      );
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.doClickAction(false);
   }
}
