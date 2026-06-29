package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.ui.Border;

public final class HTMLSubmitInputField extends HTMLButtonField implements SubmitButton {
   private boolean _selected;
   private boolean _isImage;

   HTMLSubmitInputField(
      HTMLInput input,
      String value,
      String alt,
      boolean isImage,
      long style,
      int width,
      int height,
      int backgroundColor,
      int foregroundColor,
      Border parentBorder
   ) {
      super(input, selectLabel(value, alt), style, width, height, backgroundColor, foregroundColor, parentBorder);
      this._isImage = isImage;
   }

   private static final String selectLabel(String value, String alt) {
      String label = BrowserResources.getString(537);
      if (alt != null && alt.length() > 0) {
         return alt;
      }

      if (value != null && value.length() > 0) {
         label = value;
      }

      return label;
   }

   public final boolean isImage() {
      return this._isImage;
   }

   @Override
   public final boolean getSelected() {
      boolean value = this._selected;
      this._selected = false;
      return value;
   }

   @Override
   public final void setSelected() {
      this._selected = true;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      this.doClickAction(false);
   }
}
