package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;

final class MoveIconVerb extends RibbonBarVerb {
   private static MoveIconVerb _theVerb;

   private MoveIconVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 614656);
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(21);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new MoveIconVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      super._ribbonApp.moveIcon();
      return null;
   }
}
