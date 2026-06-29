package net.rim.device.apps.internal.activation;

import net.rim.device.apps.api.framework.verb.Verb;

final class ActivationScreen$HideVerb extends Verb {
   private final ActivationScreen this$0;

   ActivationScreen$HideVerb(ActivationScreen _1) {
      super(524800, ActivationApp._resources, 108);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object anObject) {
      this.this$0.exitApp();
      return null;
   }
}
