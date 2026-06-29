package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class SIMCardOptionsItem$DisableSIMSecurityVerb extends Verb {
   private final SIMCardOptionsItem this$0;

   SIMCardOptionsItem$DisableSIMSecurityVerb(SIMCardOptionsItem _1) {
      super(16846848, OptionsResources.getResourceBundle(), 1512);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      String pin = this.this$0.getPIN(1);
      if (pin != null) {
         try {
            SIMCard.requestDisablePIN(pin.getBytes());
            return null;
         } finally {
            Dialog.alert(OptionsResources.getString(1515));
            return null;
         }
      } else {
         return null;
      }
   }
}
