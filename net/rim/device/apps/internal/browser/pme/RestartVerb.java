package net.rim.device.apps.internal.browser.pme;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.resource.PMEPlugginResource;

final class RestartVerb extends Verb implements PMEPlugginResource {
   private PMEBrowserField _field;

   public RestartVerb(PMEBrowserField field) {
      super(341258, 1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin", 12);
      this._field = field;
   }

   @Override
   public final Object invoke(Object context) {
      this._field.restartPlayer();
      return null;
   }
}
