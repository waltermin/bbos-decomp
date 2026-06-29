package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;

final class ScreenKeyboardOptionsItem$LicensePopupScreen extends PopupScreen {
   private RichTextField _text = (RichTextField)(new Object(""));
   private ScreenKeyboardOptionsItem$OKButton _okButton = new ScreenKeyboardOptionsItem$OKButton();

   ScreenKeyboardOptionsItem$LicensePopupScreen() {
      super((Manager)(new Object(299067162755072L)));
      this.add(this._text);
      this.add(this._okButton);
   }

   private final void setRichText(String aText) {
      this._text.setText(aText);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.close();
         return super.keyChar(key, status, time);
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
