package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;

final class ShowAllToggleVerb extends RibbonBarVerb {
   private static ShowAllToggleVerb _theVerb;

   private ShowAllToggleVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 615168);
   }

   @Override
   public final String toString() {
      String string = RibbonResources.getString(30);
      if (super._ribbonOptions.getShowHiddenApps()) {
         string = ((StringBuffer)(new Object())).append('✓').append(string).toString();
      }

      return string;
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp) {
      if (_theVerb == null) {
         _theVerb = new ShowAllToggleVerb(ribbonApp);
      }

      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      super._ribbonOptions.setShowHiddenApps(!super._ribbonOptions.getShowHiddenApps());
      super._ribbonOptions.commit();
      ((RibbonLauncherImpl)super._ribbonApp).populateRibbon();
      return null;
   }
}
