package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.apps.api.framework.verb.Verb;

final class ConvenienceKeyOptionsImpl$SwitchApplicationVerb extends Verb {
   ConvenienceKeyOptionsImpl$SwitchApplicationVerb() {
      super(268501000, ConvenienceKeyOptionsImpl._resources, 203);
   }

   @Override
   public final Object invoke(Object parameter) {
      RIMGlobalMessagePoster.postGlobalEvent(7563637690172082503L, 0, 2048, null, null);
      return null;
   }
}
