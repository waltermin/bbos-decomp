package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;

final class CancelMoveIconVerb extends RibbonBarVerb {
   private static CancelMoveIconVerb _theVerb;

   private CancelMoveIconVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 268500992);
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(22);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new CancelMoveIconVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      super._ribbonApp.completeMoveApplication(false);
      return null;
   }
}
