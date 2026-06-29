package net.rim.device.apps.internal.explorer.player;

import net.rim.device.apps.api.framework.verb.Verb;

final class NowPlayingVerb extends Verb {
   private PlayerApplication _app;

   NowPlayingVerb(PlayerApplication application) {
      super(4097, 349501092522026426L, "net.rim.device.apps.internal.resource.Explorer", 155);
      this._app = application;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._app != null) {
         this._app.pushForeground();
      }

      return null;
   }
}
