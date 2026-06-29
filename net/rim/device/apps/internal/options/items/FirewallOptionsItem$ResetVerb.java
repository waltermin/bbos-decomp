package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.firewall.Firewall;

final class FirewallOptionsItem$ResetVerb extends Verb {
   public FirewallOptionsItem$ResetVerb() {
      super(16986368, OptionsResources.getResourceBundle(), 1701);
   }

   @Override
   public final Object invoke(Object parameter) {
      Firewall.getInstance().reset();
      ApplicationControl.resetAllPrompts(3, 4);
      ApplicationControl.resetAllPrompts(5, 6);
      return null;
   }
}
