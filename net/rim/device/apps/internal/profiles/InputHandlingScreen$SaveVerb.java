package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class InputHandlingScreen$SaveVerb extends Verb {
   private Profile _profile;
   private final InputHandlingScreen this$0;

   InputHandlingScreen$SaveVerb(InputHandlingScreen _1, Profile profile) {
      super(332288, CommonResource.getBundle(), 18);
      this.this$0 = _1;
      this._profile = profile;
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0.save(this._profile, context);
      return null;
   }
}
