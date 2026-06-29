package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

final class BrowserProperty$BrowserPropertyScreen extends AppsMainScreen {
   private final BrowserProperty this$0;

   BrowserProperty$BrowserPropertyScreen(BrowserProperty _1) {
      super(0);
      this.this$0 = _1;
      this.setDefaultClose(false);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         if (UiApplication.getUiApplication().getActiveScreen().isDirty()) {
            ExitVerb closeVerb = new ExitVerb(0, this.this$0);
            closeVerb.invoke(null);
            return true;
         } else {
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            return true;
         }
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void makeMenu(SystemEnabledMenu menu, int instance) {
      Verb[] verbs = new Verb[0];
      Verb defaultVerb = this.this$0.getVerbs(verbs);
      menu.add(verbs);
      menu.add(new ExitVerb(0, this.this$0));
      if (UiApplication.getUiApplication().getActiveScreen().isMuddy() && defaultVerb != null) {
         menu.setDefault(defaultVerb);
      }
   }
}
