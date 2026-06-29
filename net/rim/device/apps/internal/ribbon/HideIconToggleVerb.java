package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationEntry;

final class HideIconToggleVerb extends RibbonBarVerb {
   private ApplicationEntry _applicationEntry;
   private static HideIconToggleVerb _theVerb;

   private HideIconToggleVerb(RibbonLauncherImpl ribbonApp) {
      super(ribbonApp, 614912);
   }

   @Override
   public final String toString() {
      String string = RibbonResources.getString(31);
      if (!this._applicationEntry.isVisible()) {
         string = ((StringBuffer)(new Object())).append('✓').append(string).toString();
      }

      return string;
   }

   static final Verb getInstance(RibbonLauncherImpl ribbonApp, ApplicationEntry applicationEntry) {
      if (_theVerb == null) {
         _theVerb = new HideIconToggleVerb(ribbonApp);
      }

      _theVerb._applicationEntry = applicationEntry;
      return _theVerb;
   }

   @Override
   public final Object invoke(Object parameter) {
      boolean newState = !this._applicationEntry.isVisible();
      this._applicationEntry.getRibbonIcon().invalidate();
      super._ribbonApp.showApplicationEntry(this._applicationEntry, newState);
      super._ribbonOptions.commit();
      return null;
   }
}
