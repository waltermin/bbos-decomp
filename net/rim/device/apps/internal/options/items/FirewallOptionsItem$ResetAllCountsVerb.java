package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsMainScreen;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.firewall.Firewall;

final class FirewallOptionsItem$ResetAllCountsVerb extends Verb {
   private final FirewallOptionsItem this$0;

   public FirewallOptionsItem$ResetAllCountsVerb(FirewallOptionsItem _1) {
      super(16986368, OptionsResources.getResourceBundle(), 1997);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      Firewall.getInstance().resetBlockedCounts();
      Screen s = UiApplication.getUiApplication().getActiveScreen();
      if (s instanceof Object) {
         OptionsMainScreen oms = (OptionsMainScreen)s;
         oms.deleteAll();
         this.this$0.populateMainScreen(oms);
      }

      return null;
   }
}
