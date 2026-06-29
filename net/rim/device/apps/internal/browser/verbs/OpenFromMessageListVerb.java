package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.api.ui.RunnableDialog;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public final class OpenFromMessageListVerb extends BrowserVerb {
   private PageModel _pageModel;

   public OpenFromMessageListVerb(PageModel pageModel) {
      super(1183824);
      this._pageModel = pageModel;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(114);
   }

   @Override
   public final Object invoke(Object context) {
      int status = this._pageModel.getStatus();
      if (status != 1 && status != 2) {
         BrowserImpl browser = BrowserDaemonRegistry.getInstance();
         if (browser == null) {
            return null;
         }

         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            session.getHistory().clear();
         }

         if (status != 5) {
            this._pageModel.changeStatus(4);
         }

         ModelResult modelResult = this._pageModel.getModelResult();
         modelResult.setNavigation(1);
         BrowserConfigRecord browserConfigRecord = BrowserConfigRecord.getDecodedConfig(
            modelResult.getConfigUID(), modelResult.getConfigType(), modelResult.getTransportCID()
         );
         browser.initiateFetchRequest(new FetchRequest(modelResult, browserConfigRecord));
         return null;
      } else {
         RunnableDialog dialog = (RunnableDialog)(new Object(BrowserResources.getString(589), 1));
         dialog.run();
         return null;
      }
   }
}
