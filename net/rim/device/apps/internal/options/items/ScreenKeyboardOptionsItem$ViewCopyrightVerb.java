package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ScreenKeyboardOptionsItem$ViewCopyrightVerb extends Verb {
   private Font _font;
   private ScreenKeyboardOptionsItem$LicensePopupScreen _pop = new ScreenKeyboardOptionsItem$LicensePopupScreen();

   public ScreenKeyboardOptionsItem$ViewCopyrightVerb(Font aFont) {
      super(4096);
      this._font = aFont;
   }

   public final void setFont(Font aFont) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final String toString() {
      return OptionsResources.getString(2082);
   }

   @Override
   public final Object invoke(Object parameter) {
      this._pop.setRichText(this._font.getLicense());
      UiApplication.getUiApplication().pushScreen(this._pop);
      return null;
   }
}
