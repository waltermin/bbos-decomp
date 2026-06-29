package net.rim.device.apps.internal.browser.pme;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.resource.PMEPlugginResource;

final class ResumeVerb extends Verb implements PMEPlugginResource {
   private PMEBrowserField _field;

   public ResumeVerb(PMEBrowserField field) {
      super(341268, 1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin", 13);
      this._field = field;
   }

   @Override
   public final Object invoke(Object context) {
      this._field.startPlayer();
      return null;
   }
}
