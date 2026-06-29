package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class EnableProfileVerb extends Verb {
   private Profile _profile;

   EnableProfileVerb(Profile profile) {
      super(626736, 2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles", 210);
      this._profile = profile;
   }

   @Override
   public final Object invoke(Object anObject) {
      Profiles profiles = Profiles.getInstance();
      profiles.enable(this._profile);
      return new ContextObject(39);
   }
}
