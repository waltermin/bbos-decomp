package net.rim.device.apps.internal.browser.wappush.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.push.BrowserPushResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;
import net.rim.device.apps.internal.browser.wappush.WAPPushModel;

public final class FollowLinkVerb extends Verb {
   private WAPPushModel _model;

   public FollowLinkVerb(WAPPushModel model) {
      super(1183824);
      this._model = model;
   }

   @Override
   public final String toString() {
      return BrowserPushResources.getString(9);
   }

   @Override
   public final Object invoke(Object context) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (browser == null) {
         return null;
      }

      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         session.getHistory().clear();
      }

      this._model.changeStatus(0);
      ModelResult modelResult = new ModelResult(this._model.getURL(), 8193, null);
      BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(
         this._model.getPreferredConfigUID(), this._model.getPreferredConfigType(), this._model.getPreferredTransportCID()
      );
      browser.initiateFetchRequest(new FetchRequest(modelResult, browserConfigRecord));
      return null;
   }
}
