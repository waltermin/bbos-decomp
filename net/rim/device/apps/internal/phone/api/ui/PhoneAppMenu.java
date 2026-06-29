package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;

public final class PhoneAppMenu extends SystemEnabledMenu {
   public PhoneAppMenu() {
      this(new ContextObject());
   }

   public PhoneAppMenu(ContextObject context) {
      this(context, null);
   }

   public PhoneAppMenu(ContextObject context, AppsMainScreen screen) {
      super(context, screen);
      Verb defaultVerb = (Verb)ContextObject.get(context, -3185095355580406181L);
      if (defaultVerb != null) {
         VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
         verbToMenu.setDefaultVerb(defaultVerb);
      }
   }
}
