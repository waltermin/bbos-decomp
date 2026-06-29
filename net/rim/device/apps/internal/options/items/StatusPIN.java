package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.Array;

public final class StatusPIN extends StatusListItem implements RIMModel {
   public StatusPIN() {
      super(408);
   }

   @Override
   public final String getDisplayValue() {
      int pin = DeviceInfo.getDeviceId();
      return Integer.toHexString(pin).toUpperCase();
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb v = new StatusPIN$CopyPINVerb(this);
      Array.resize(verbs, 1);
      verbs[0] = v;
      return v;
   }
}
