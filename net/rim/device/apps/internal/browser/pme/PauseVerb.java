package net.rim.device.apps.internal.browser.pme;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.resource.PMEPlugginResource;

final class PauseVerb extends Verb implements PMEPlugginResource {
   private PMEBrowserField _field;

   public PauseVerb(PMEBrowserField field) {
      super(341268, 1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin", 11);
      this._field = field;
   }

   @Override
   public final Object invoke(Object context) {
      this._field.stopPlayer();
      return null;
   }
}
