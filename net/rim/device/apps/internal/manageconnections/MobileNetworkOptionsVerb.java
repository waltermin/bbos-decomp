package net.rim.device.apps.internal.manageconnections;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class MobileNetworkOptionsVerb extends Verb {
   public MobileNetworkOptionsVerb() {
      super(200960, -348546850453906601L, "net.rim.device.apps.internal.manageconnections.ManageConnections", 26);
   }

   @Override
   public final Object invoke(Object context) {
      try {
         RibbonLauncher.getInstance().launch("net_rim_bb_options_app.Network");
         return null;
      } finally {
         ;
      }
   }
}
