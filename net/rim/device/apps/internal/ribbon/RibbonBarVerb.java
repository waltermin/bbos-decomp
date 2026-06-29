package net.rim.device.apps.internal.ribbon;

import net.rim.device.apps.api.framework.verb.Verb;

class RibbonBarVerb extends Verb {
   protected RibbonLauncherImpl _ribbonApp;
   protected RibbonOptions _ribbonOptions = RibbonOptions.getOptions();

   RibbonBarVerb(RibbonLauncherImpl ribbonApp, int ordering) {
      super(ordering);
      this._ribbonApp = ribbonApp;
   }
}
