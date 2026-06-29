package net.rim.device.apps.internal.idlescreen;

import net.rim.device.apps.api.framework.verb.Verb;

final class ClearIdleScreenVerb extends Verb {
   private ScreenClearer _clearer;

   ClearIdleScreenVerb(ScreenClearer clearer) {
      super(565281, 8585934785835124063L, "net.rim.device.apps.internal.resource.IdleScreen", 5);
      this._clearer = clearer;
   }

   @Override
   public final Object invoke(Object context) {
      this._clearer.clearScreen();
      IdleScreenOptions.setIdleScreenFilename(null);
      return null;
   }
}
