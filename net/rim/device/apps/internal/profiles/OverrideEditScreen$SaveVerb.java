package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class OverrideEditScreen$SaveVerb extends Verb {
   private final OverrideEditScreen this$0;

   private OverrideEditScreen$SaveVerb(OverrideEditScreen _1) {
      super(332288, CommonResource.getBundle(), 18);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0.save(this.this$0._override, context);
      return null;
   }

   OverrideEditScreen$SaveVerb(OverrideEditScreen x0, OverrideEditScreen$1 x1) {
      this(x0);
   }
}
