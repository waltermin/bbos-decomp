package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.framework.verb.Verb;

class SSOptionProfileVerb extends Verb {
   protected SSOptionProfile _profile;

   protected SSOptionProfileVerb(int ordering) {
   }

   public Verb setProfile(SSOptionProfile profile) {
      this._profile = profile;
      return this;
   }

   protected SSOptionProfile getProfile() {
      return this._profile;
   }

   @Override
   public Object invoke(Object _1) {
      throw null;
   }
}
