package net.rim.device.apps.internal.manageconnections;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class WiFiOptionsVerb extends Verb {
   public WiFiOptionsVerb() {
      super(201216, -348546850453906601L, "net.rim.device.apps.internal.manageconnections.ManageConnections", 27);
   }

   @Override
   public final Object invoke(Object context) {
      try {
         RibbonLauncher.getInstance().launch("net_rim_bb_options_app?net.rim.device.apps.internal.options.items.WLANOptionsItem");
         return null;
      } finally {
         ;
      }
   }
}
