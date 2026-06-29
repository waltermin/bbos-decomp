package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingTable;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class EditHRTVerb extends Verb {
   private HostRoutingTable _hrt;

   public EditHRTVerb(HostRoutingTable hrt) {
      super(16851200);
      this._hrt = hrt;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(435);
   }

   @Override
   public final Object invoke(Object context) {
      HRUtils.getThunks().displayEditor(this._hrt, 0);
      return null;
   }
}
