package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.framework.verb.Verb;

final class ProfilesScreen$ShowTunesVerb extends Verb {
   public ProfilesScreen$ShowTunesVerb() {
      super(16986368, 2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles", 226);
   }

   @Override
   public final Object invoke(Object anObject) {
      TuneManager.getTuneManager().showTuneListingScreen();
      return null;
   }
}
