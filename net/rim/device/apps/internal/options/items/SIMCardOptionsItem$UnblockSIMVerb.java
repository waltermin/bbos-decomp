package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class SIMCardOptionsItem$UnblockSIMVerb extends Verb {
   private int _pinID;
   private final SIMCardOptionsItem this$0;

   public SIMCardOptionsItem$UnblockSIMVerb(SIMCardOptionsItem _1, int pinID) {
      super(16843008, OptionsResources.getResourceBundle(), pinID == 1 ? 1948 : 1925);
      this.this$0 = _1;
      this._pinID = pinID;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.forceNewPIN(this._pinID);
      return null;
   }
}
