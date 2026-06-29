package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.verb.Verb;

final class OverrideFromField$DeleteNameVerb extends Verb {
   private final OverrideFromField this$0;

   private OverrideFromField$DeleteNameVerb(OverrideFromField _1) {
      super(607776);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      return resources.getString(234);
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0.deleteName();
      return null;
   }

   OverrideFromField$DeleteNameVerb(OverrideFromField x0, OverrideFromField$1 x1) {
      this(x0);
   }
}
