package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FetchRequest;
import net.rim.device.apps.internal.browser.stack.ModelResult;

public class FollowLinkWithContextVerb extends Verb {
   private PageModel _bookmarkNode;
   private static int DESCRIPTION = 100;

   public FollowLinkWithContextVerb(PageModel bookmarkNode) {
      super(341248);
      this._bookmarkNode = bookmarkNode;
   }

   @Override
   public String toString() {
      return BrowserResources.getString(DESCRIPTION);
   }

   @Override
   public Object invoke(Object context) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      ModelResult modelResult = this._bookmarkNode.getModelResult();
      if (modelResult != null) {
         ModelResult mresult = new ModelResult(modelResult.getURL(), 1 | modelResult.getRenderingFlags() & 8192, modelResult.getRequestHeaders());
         mresult.setCacheResult(modelResult.getCacheResult());
         mresult.setPostData(modelResult.getPostData());
         IBrowserContext browserContext = (IBrowserContext)modelResult.getContext();
         if (browserContext != null) {
            mresult.setContext(browserContext.clone());
         }

         mresult.setLabel(modelResult.getLabel());
         mresult.setHomePage(modelResult.isHomePage());
         mresult.setConfigUID(modelResult.getConfigUID());
         browser.initiateFetchRequest(new FetchRequest(mresult));
      }

      return null;
   }
}
