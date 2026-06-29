package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class SIMCardOptionsItem$ChangePINVerb extends Verb {
   private int _pinID;
   private final SIMCardOptionsItem this$0;

   public SIMCardOptionsItem$ChangePINVerb(SIMCardOptionsItem _1, int pinID) {
      super(pinID == 1 ? 16843008 : 16843024, OptionsResources.getResourceBundle(), pinID == 1 ? 1509 : 1510);
      this.this$0 = _1;
      this._pinID = pinID;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.changePIN(null, this._pinID);
      return null;
   }
}
