package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;

final class CompleteMoveIconVerb extends RibbonBarVerb {
   private static CompleteMoveIconVerb _theVerb;

   private CompleteMoveIconVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 614656);
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new CompleteMoveIconVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final String toString() {
      return RibbonResources.getString(23);
   }

   @Override
   public final Object invoke(Object parameter) {
      super._ribbonApp.completeMoveApplication(true);
      return null;
   }
}
