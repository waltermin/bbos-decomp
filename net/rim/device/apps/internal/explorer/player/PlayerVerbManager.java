package net.rim.device.apps.internal.explorer.player;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;

final class PlayerVerbManager implements VerbFactory {
   private PlayerApplication _app;
   private static final long GUID = -1063589100647616419L;

   final void register() {
      VerbFactoryRepository.addFactory(8522643724050848398L, this);
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (ContextObject.getFlag(context, 90) && this._app != null && !this._app.isForeground()) {
         Verb[] verbs = new Object[1];
         verbs[0] = new NowPlayingVerb(this._app);
         return verbs;
      } else {
         return null;
      }
   }

   final void deRegister() {
      VerbFactoryRepository.removeFactory(8522643724050848398L, this);
      this._app = null;
   }

   PlayerVerbManager(PlayerApplication application) {
      this._app = application;
   }
}
