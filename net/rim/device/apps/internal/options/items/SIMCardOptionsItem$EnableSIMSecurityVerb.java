package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class SIMCardOptionsItem$EnableSIMSecurityVerb extends Verb {
   private final SIMCardOptionsItem this$0;

   SIMCardOptionsItem$EnableSIMSecurityVerb(SIMCardOptionsItem _1) {
      super(16846848, OptionsResources.getResourceBundle(), 1511);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      String pin = this.this$0.getPIN(1);
      if (pin != null) {
         try {
            SIMCard.requestEnablePIN(pin.getBytes());
            return null;
         } finally {
            Dialog.alert(OptionsResources.getString(1514));
            return null;
         }
      } else {
         return null;
      }
   }
}
